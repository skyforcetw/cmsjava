
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class U8Fixed8NumberTest extends TestCase
{
	private U8Fixed8Number	data_;

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
			U8Fixed8Number empty = new U8Fixed8Number();

			U8Fixed8Number one = new U8Fixed8Number(1.0);
			assertEquals(1.0, one.doubleValue());

			U8Fixed8Number copy = new U8Fixed8Number(one);
			assertEquals(1.0, copy.doubleValue());

			U8Fixed8Number minv = new U8Fixed8Number(U8Fixed8Number.MIN_VALUE);
			assertEquals(U8Fixed8Number.MIN_VALUE,minv.doubleValue());

			U8Fixed8Number maxv = new U8Fixed8Number(U8Fixed8Number.MAX_VALUE);
			assertEquals(U8Fixed8Number.MAX_VALUE,maxv.doubleValue());

			byte[] ba = new byte[]
			{
				(byte)0,(byte)221
			};
			U8Fixed8Number inst1 = new U8Fixed8Number(ba);
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
		U8Fixed8Number data = new U8Fixed8Number();
		byte[] ba = new byte[]
		{
			(byte)82,(byte)201,(byte)231,(byte)90
		};
		try
		{
			data.fromByteArray(ba, 0, 2);
			byte[] res = data.toByteArray();
			compareBytes(ba,0,2,res);
			data.fromByteArray(ba,2,2);
			byte[] re2 = data.toByteArray();
			compareBytes(ba,2,2,re2);
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
			assertEquals(ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(ba, ba.length, U8Fixed8Number.SIZE);
			assertFalse("index out of bounds, should raise exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(ba, 0, 3);
			assertFalse("should raise wrong size exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.WrongSizeException, e.getType());
		}
		try
		{
			data.fromByteArray(null, 0, 0);
			assertFalse("should raise null pointer exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.NullPointerException, e.getType());
		}
	}

	public void testParseValue()
	{
		U8Fixed8Number n = new U8Fixed8Number();
		try
		{
			n.parseValue(U8Fixed8Number.MIN_VALUE);
			assertEquals(U8Fixed8Number.MIN_VALUE, n.doubleValue(), 0.00000001);
			n.parseValue(U8Fixed8Number.MID_VALUE);
			assertEquals(U8Fixed8Number.MID_VALUE, n.doubleValue(), 0.00000001);
			n.parseValue(U8Fixed8Number.MAX_VALUE);
			assertEquals(U8Fixed8Number.MAX_VALUE, n.doubleValue(), 0.00000001);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testDoubleValue()
	{
		//done in testParseValue
	}

	public void testSize()
	{
		U8Fixed8Number data = new U8Fixed8Number();
		assertEquals(U8Fixed8Number.SIZE, data.size());
	}

	public void testToString()
	{
		try
		{
			U8Fixed8Number data = new U8Fixed8Number(1.5);
			//TODO: add setValue here or in constructor
			String bs = data.toString();
			String s = "1.5";	//original string
			assertEquals(s, bs);
		}
		catch (ICCProfileException e)
		{
			assertFalse(true);
		}
	}


}
