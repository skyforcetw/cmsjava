
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class UInt8NumberTest extends TestCase
{
	private UInt8Number	data_;

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
			UInt8Number empty = new UInt8Number();

			UInt8Number one = new UInt8Number(1);
			assertEquals(1, one.intValue());

			UInt8Number copy = new UInt8Number(one);
			assertEquals(1, copy.intValue());

			UInt8Number minv = new UInt8Number(UInt8Number.MIN_VALUE);
			assertEquals(UInt8Number.MIN_VALUE,minv.intValue());

			UInt8Number maxv = new UInt8Number(UInt8Number.MAX_VALUE);
			assertEquals(UInt8Number.MAX_VALUE,maxv.intValue());

			byte[] ba = new byte[]
			{
				(byte)0x20
			};
			UInt8Number inst1 = new UInt8Number(ba);
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
		UInt8Number data = new UInt8Number();
		byte[] ba = new byte[]
		{
			(byte)71,(byte)9
		};
		try
		{
			data.fromByteArray(ba, 0, 1);
			byte[] res = data.toByteArray();
			compareBytes(ba,0,1,res);
			data.fromByteArray(ba,1,1);
			byte[] re2 = data.toByteArray();
			compareBytes(ba,1,1,re2);
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
			data.fromByteArray(ba, ba.length, UInt8Number.SIZE);
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

	public void testByteValue()
	{
		UInt8Number n = new UInt8Number();
		n.setValue((byte)0xff);
		assertEquals((byte)255, n.byteValue());
	}

	public void testShortValue()
	{
		UInt8Number n = new UInt8Number();
		n.setValue((byte)0xff);
		assertEquals((short)255, n.shortValue());
	}

	public void testIntValue()
	{
		UInt8Number n = new UInt8Number();
		n.setValue((byte)0xff);
		assertEquals(255, n.intValue());
	}

	public void testLongValue()
	{
		UInt8Number n = new UInt8Number();
		n.setValue((byte)0xff);
		assertEquals(255L, n.longValue());
	}

	public void testSetValue()
	{
		UInt8Number n = new UInt8Number();
		try
		{
			n.setValue((byte)0xff);
			assertEquals((byte)255, n.byteValue());
			n.setValue((short)0xff);
			assertEquals((short)255, n.shortValue());
			n.setValue((int)0xff);
			assertEquals((int)255, n.shortValue());
			n.setValue((long)0xff);
			assertEquals((long)255, n.shortValue());
			n.setValue("255");
			assertEquals(255, n.intValue());
			n.setValue("ff",16);
			assertEquals(255, n.intValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse("testSetValue exception",true);
		}
	}

	public void testSize()
	{
		UInt8Number data = new UInt8Number();
		assertEquals(UInt8Number.SIZE, data.size());
	}

	public void testToString()
	{
		UInt8Number data = new UInt8Number((byte)123);
		//TODO: add setValue here or in constructor
		String bs = data.toString();
		String s = "123";	//original string
		assertEquals(s, bs);
	}


}
