
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class U1Fixed15NumberTest extends TestCase
{
	private U1Fixed15Number	data_;

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
			U1Fixed15Number empty = new U1Fixed15Number();

			U1Fixed15Number one = new U1Fixed15Number(1.0);
			assertEquals(1.0, one.doubleValue());

			U1Fixed15Number copy = new U1Fixed15Number(one);
			assertEquals(1.0, copy.doubleValue());

			U1Fixed15Number minv = new U1Fixed15Number(U1Fixed15Number.MIN_VALUE);
			assertEquals(U1Fixed15Number.MIN_VALUE,minv.doubleValue());

			U1Fixed15Number maxv = new U1Fixed15Number(U1Fixed15Number.MAX_VALUE);
			assertEquals(U1Fixed15Number.MAX_VALUE,maxv.doubleValue());

			byte[] ba = new byte[]
			{
				(byte)0,(byte)57
			};
			U1Fixed15Number inst1 = new U1Fixed15Number(ba);
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
		U1Fixed15Number data = new U1Fixed15Number();
		byte[] ba = new byte[]
		{
			(byte)131,(byte)41,(byte)62,(byte)169
		};
		try
		{
			data.fromByteArray(ba, 0, 2);
			assertEquals(1.024688720703125, data.doubleValue());
			byte[] res = data.toByteArray();
			compareBytes(ba,0,2,res);
			data.fromByteArray(ba,2,2);
			assertEquals(0.489532470703125, data.doubleValue());
			byte[] re2 = data.toByteArray();
			compareBytes(ba,2,2,re2);
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
			assertEquals(ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(ba, ba.length, U1Fixed15Number.SIZE);
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
		U1Fixed15Number n = new U1Fixed15Number();
		try
		{
			n.parseValue(U1Fixed15Number.MIN_VALUE);
			assertEquals(U1Fixed15Number.MIN_VALUE, n.doubleValue(), 0.00000001);
			n.parseValue(U1Fixed15Number.MID_VALUE);
			assertEquals(U1Fixed15Number.MID_VALUE, n.doubleValue(), 0.00000001);
			n.parseValue(U1Fixed15Number.MAX_VALUE);
			assertEquals(U1Fixed15Number.MAX_VALUE, n.doubleValue(), 0.00000001);
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
		U1Fixed15Number data = new U1Fixed15Number();
		assertEquals(U1Fixed15Number.SIZE, data.size());
	}

	public void testToString()
	{
		try
		{
			U1Fixed15Number data = new U1Fixed15Number(1.5);
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
