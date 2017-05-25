
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class UInt32NumberTest extends TestCase
{
	private UInt32Number	data_;

	public void setUp()
	{
	}


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
			UInt32Number empty = new UInt32Number();

			UInt32Number one = new UInt32Number(1);
			assertEquals(1, one.intValue());

			UInt32Number copy = new UInt32Number(one);
			assertEquals(1, copy.intValue());

			UInt32Number minv = new UInt32Number(UInt32Number.MIN_VALUE);
			assertEquals(UInt32Number.MIN_VALUE,minv.intValue());

			UInt32Number maxv = new UInt32Number(UInt32Number.MAX_VALUE);
			assertEquals(UInt32Number.MAX_VALUE,maxv.longValue());

			byte[] ba = new byte[]
			{
				(byte)0,(byte)76,(byte)76,(byte)231
			};
			UInt32Number inst1 = new UInt32Number(ba);
			byte[] bac = inst1.toByteArray();
			compareBytes(ba, bac);

		}
		catch (ICCProfileException e)
		{
			System.err.println("Error occurred:"+e.getMessage());
		}
	}

	public void testFromByteArray()
	{
		UInt32Number data = new UInt32Number();
		byte[] ba = new byte[]
		{
			(byte)198,(byte)208,(byte)237,(byte)75,(byte)65,(byte)68,(byte)190,(byte)104
		};
		try
		{
			data.fromByteArray(ba, 0, 4);
			assertEquals(0xC6D0ED4BL,data.longValue());
			byte[] res = data.toByteArray();
			compareBytes(ba,0,4,res);
			data.fromByteArray(ba, 4, 4);
			byte[] re2 = data.toByteArray();
			compareBytes(ba,4,4,re2);
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
			data.fromByteArray(ba, ba.length, UInt32Number.SIZE);
			assertFalse("index out of bounds, should raise exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(e.getMessage(),ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(ba, 0, 3);
			assertFalse("should raise wrong size exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(e.getMessage(),ICCProfileException.WrongSizeException, e.getType());
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

	public void testLongValue()
	{
		UInt32Number n = new UInt32Number();
		n.setValue(-1);	//int -1
		assertEquals(4294967295L, n.longValue());
	}

	public void testIntValue()
	{
		UInt32Number n = new UInt32Number();
		n.setValue(32767);
		assertEquals(32767, n.intValue());
	}

	public void testSetValue()
	{
		UInt32Number n = new UInt32Number();
		try
		{
			n.setValue(-1);
			assertEquals(4294967295L, n.longValue());
			n.setValue(-1L);
			assertEquals(4294967295L, n.longValue());
			n.setValue("65535");
			assertEquals(65535L, n.longValue());
			n.setValue("ffff", 16);
			assertEquals(65535L, n.longValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
		}
	}

	public void testSize()
	{
		UInt32Number data = new UInt32Number();
		assertEquals(UInt32Number.SIZE, data.size());
	}

	public void testToString()
	{
		UInt32Number data = new UInt32Number(123);
		//TODO: add setValue here or in constructor
		String bs = data.toString();
		String s = "123";	//original string
		assertEquals(s, bs);
	}


}
