
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class UInt64NumberTest extends TestCase
{
	private UInt64Number	data_;

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
			UInt64Number empty = new UInt64Number();

			byte[] ba = new byte[]
			{
				(byte)0,(byte)22,(byte)235,(byte)95,(byte)126,(byte)65,(byte)54,(byte)68
			};
			UInt64Number inst1 = new UInt64Number(ba);
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
		UInt64Number data = new UInt64Number();
		byte[] ba = new byte[]
		{
			(byte)53,(byte)14,(byte)179,(byte)110,(byte)254,(byte)158,(byte)121,(byte)153,(byte)114,(byte)178,(byte)80,(byte)128,(byte)110,(byte)28,(byte)244,(byte)242
		};
		try
		{
			data.fromByteArray(ba, 0, 8);
			byte[] res = data.toByteArray();
			compareBytes(ba,0,8,res);
			data.fromByteArray(ba,8,8);
			byte[] re2 = data.toByteArray();
			compareBytes(ba,8,8,re2);
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
			data.fromByteArray(ba, ba.length, UInt64Number.SIZE);
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
		UInt64Number n = new UInt64Number(1024);
		assertEquals(1024L, n.longValue());
	}

	public void testIntValue()
	{
		UInt64Number n = new UInt64Number(1024);
		assertEquals(1024, n.intValue());
	}

	public void testSize()
	{
		UInt64Number data = new UInt64Number();
		assertEquals(UInt64Number.SIZE, data.size());
	}

	public void testSetValue()
	{
		UInt64Number n = new UInt64Number();
		n.setValue(1024);
		assertEquals(1024, n.intValue());
		n.setValue(1024L);
		assertEquals(1024L, n.longValue());
		try
		{
			n.setValue("1024");
			assertEquals(1024, n.intValue());
			n.setValue("ffff",16);
			assertEquals(65535, n.intValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
		}
	}

	public void testToString()
	{
		UInt64Number data = new UInt64Number(123L);
		String bs = data.toString();
		String s = "123";	//original string
		assertEquals(s, bs);
	}


}
