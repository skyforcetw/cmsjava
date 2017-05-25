
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class UInt64ArrayTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x75,(byte)0x69,(byte)0x36,(byte)0x34,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,	//array_:UInt64Number[]
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00
	};
	private byte[] ba2 = new byte[]
		{
			(byte)0x75,(byte)0x69,(byte)0x36,(byte)0x34,	//signature
			(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
			(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,	//array_:UInt64Number[]
			(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,
			(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,	//array_:UInt64Number[]
			(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,
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
			UInt64ArrayType empty = new UInt64ArrayType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			UInt64ArrayType inst1 = new UInt64ArrayType(ba);
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
		UInt64ArrayType data = new UInt64ArrayType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertEquals(UInt64ArrayType.SIGNATURE, data.getSignature().intValue());
			assertEquals(1, data.getArraySize());
			UInt64Number[] ars = data.getArray();
			assertEquals(0x8000000000000000L, ars[0].longValue());

			data.fromByteArray(ba2, 0, 0);
			assertEquals(2, data.getArraySize());
			UInt64Number[] ar2 = data.getArray();
			assertEquals(1L, ar2[0].longValue());
			assertEquals(2L, ar2[1].longValue());
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
			UInt64ArrayType ct = new UInt64ArrayType(ba2);
			byte[] bac = ct.toByteArray();
			compareBytes(ba2, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			UInt64ArrayType ct = new UInt64ArrayType();
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
		UInt64ArrayType ct = new UInt64ArrayType();
		UInt64Number[] ns = new UInt64Number[]
		{
			new UInt64Number(1), new UInt64Number(2), new UInt64Number(3)
		};
		for (int i=0; i<ns.length; i++)
		{
			ct.addNumber(ns[i]);
			assertEquals(i+1, ct.getArraySize());
			UInt64Number[] rs = ct.getArray();
			assertEquals(ns[i].longValue(), rs[rs.length-1].longValue());
		}
	}

}
