
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class UInt16NumberTest extends TestCase
{
	private UInt16Number	data_;

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
			UInt16Number empty = new UInt16Number();

			UInt16Number one = new UInt16Number(1);
			assertEquals(1, one.intValue());

			UInt16Number copy = new UInt16Number(one);
			assertEquals(1, copy.intValue());

			UInt16Number minv = new UInt16Number(UInt16Number.MIN_VALUE);
			assertEquals(UInt16Number.MIN_VALUE,minv.intValue());

			UInt16Number maxv = new UInt16Number(UInt16Number.MAX_VALUE);
			assertEquals(UInt16Number.MAX_VALUE,maxv.intValue());

			byte[] ba = new byte[]
			{
				(byte)0,(byte)86
			};
			UInt16Number inst1 = new UInt16Number(ba);
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
		UInt16Number data = new UInt16Number();
		byte[] ba = new byte[]
		{
			(byte)82,(byte)224,(byte)224,(byte)51,(byte)0,(byte)0xff
		};
		try
		{
			data.fromByteArray(ba, 0, 2);
			byte[] res = data.toByteArray();
			compareBytes(ba,0,2,res);
			data.fromByteArray(ba,2,2);
			byte[] re2 = data.toByteArray();
			compareBytes(ba,2,2,re2);
			data.fromByteArray(ba,4,2);
			byte[] re3 = data.toByteArray();
			compareBytes(ba,4,2,re3);
			assertEquals(255,data.intValue());
		}
			catch (ICCProfileException e)
		{
			assertFalse("should not get here",true);
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
			data.fromByteArray(ba, ba.length, UInt16Number.SIZE);
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

	public void testShortValue()
	{
		UInt16Number n = new UInt16Number();
		n.setValue((short)32767);
		assertEquals(32767, n.shortValue());
	}

	public void testIntValue()
	{
		UInt16Number n = new UInt16Number();
		n.setValue((short)-1);
		assertEquals(65535, n.intValue());
	}

	public void testLongValue()
	{
		UInt16Number n = new UInt16Number();
		n.setValue((short)-1);
		assertEquals(65535L, n.longValue());
	}

	public void testSetValue()
	{
		UInt16Number n = new UInt16Number();
		n.setValue((short)-1);
		assertEquals(65535, n.intValue());
		try
		{
			n.setValue(-1);
			assertEquals(65535, n.intValue());
			n.setValue(-1L);
			assertEquals(65535, n.intValue());
			n.setValue("65535");
			assertEquals(65535, n.intValue());
			n.setValue("ffff", 16);
			assertEquals(65535, n.intValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(true);
		}
	}

	public void testSize()
	{
		UInt16Number data = new UInt16Number();
		assertEquals(UInt16Number.SIZE, data.size());
	}

	public void testToString()
	{
		UInt16Number data = new UInt16Number(123);
		String bs = data.toString();
		String s = "123";	//original string
		assertEquals(s, bs);
	}


}
