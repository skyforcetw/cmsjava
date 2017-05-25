
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class UInt8ArrayTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x75,(byte)0x69,(byte)0x30,(byte)0x38,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x80	//array_:UInt8Number[]
	};
	private byte[] ba2 = new byte[]
		{
			(byte)0x75,(byte)0x69,(byte)0x30,(byte)0x38,	//signature
			(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
			(byte)0x01,(byte)0x02	//array_:UInt8Number[]
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
			UInt8ArrayType empty = new UInt8ArrayType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			UInt8ArrayType inst1 = new UInt8ArrayType(ba);
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
		UInt8ArrayType data = new UInt8ArrayType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertEquals(UInt8ArrayType.SIGNATURE, data.getSignature().intValue());
			assertEquals(1, data.getArraySize());
			UInt8Number[] rs = data.getArray();
			assertEquals(0x80, rs[0].intValue());

			data.fromByteArray(ba2, 0, 0);
			assertEquals(2, data.getArraySize());
			UInt8Number[] rs2 = data.getArray();
			assertEquals(0x01, rs2[0].intValue());
			assertEquals(0x02, rs2[1].intValue());
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
			UInt8ArrayType ct = new UInt8ArrayType(ba2);
			byte[] bac = ct.toByteArray();
			compareBytes(ba2, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			UInt8ArrayType ct = new UInt8ArrayType();
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
		UInt8ArrayType ct = new UInt8ArrayType();
		UInt8Number[] ns = new UInt8Number[3];
		try
		{
			for (int i=0; i<ns.length; i++)
				ns[i] = new UInt8Number(1+i);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		for (int i=0; i<ns.length; i++)
		{
			ct.addNumber(ns[i]);
			assertEquals(i+1, ct.getArraySize());
			UInt8Number[] rs = ct.getArray();
			assertEquals(ns[i].intValue(), rs[rs.length-1].intValue());
		}
	}
}

