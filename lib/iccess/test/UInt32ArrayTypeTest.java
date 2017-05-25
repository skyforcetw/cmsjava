
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class UInt32ArrayTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x75,(byte)0x69,(byte)0x33,(byte)0x32,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00	//array_:UInt32Number[]
	};
	private byte[] ba2 = new byte[]
		{
			(byte)0x75,(byte)0x69,(byte)0x33,(byte)0x32,	//signature
			(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
			(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,	//array_:UInt32Number[]
			(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02
		};


	private void compareBytes(byte[] expected, byte[] result)
	{
		assertEquals("two byte array not same size",expected.length, result.length);
		for (int i=0; i<result.length; i++)
			assertEquals("byte "+i, expected[i], result[i]);
	}

	private void compareBytes(byte[] expected, int offset, int len, byte[] result)
	{
		for (int i=0; i<len; i++)
			assertEquals("byte "+i, expected[i+offset], result[i]);
	}

	public void testConstructors()
	{
		try
		{
			UInt32ArrayType empty = new UInt32ArrayType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			UInt32ArrayType inst1 = new UInt32ArrayType(ba);
			byte[] bac = inst1.toByteArray();
			//compareBytes(ba, bac);

		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testFromByteArray()
	{
		UInt32ArrayType data = new UInt32ArrayType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertEquals(UInt32ArrayType.SIGNATURE, data.getSignature().intValue());
			assertEquals(1, data.getArraySize());
			UInt32Number[] ar1 = data.getArray();
			assertEquals(0x80000000L, ar1[0].longValue());

			data.fromByteArray(ba2, 0, 0);
			assertEquals(2, data.getArraySize());
			UInt32Number[] ar2 = data.getArray();
			assertEquals(1, ar2[0].intValue());
			assertEquals(2, ar2[1].intValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
		}

		try
		{
			data.fromByteArray(ba, -1, ba.length);
			assertFalse("index out of bounds, should raise exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(e.getMessage(),ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(ba, ba.length, ba.length);
			assertFalse("index out of bounds, should raise exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(e.getMessage(),ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(null, 0, 0);
			assertFalse("should raise null pointer exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(e.getMessage(),ICCProfileException.NullPointerException, e.getType());
		}
	}


	public void testToByteArray()
	{
		try
		{
			UInt32ArrayType ct = new UInt32ArrayType(ba2);
			byte[] bac = ct.toByteArray();
			compareBytes(ba2, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			UInt32ArrayType ct = new UInt32ArrayType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testAddNumber()
	{
		UInt32ArrayType ct = new UInt32ArrayType();
		UInt32Number[] ns = new UInt32Number[]
		{
			new UInt32Number(1), new UInt32Number(2), new UInt32Number(3)
		};
		for (int i=0; i<ns.length; i++)
		{
			ct.addNumber(ns[i]);
			assertEquals(i+1, ct.getArraySize());
			UInt32Number[] rs = ct.getArray();
			assertEquals(ns[i].intValue(), rs[rs.length-1].intValue());
		}
	}

}
