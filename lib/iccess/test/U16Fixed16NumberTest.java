
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class U16Fixed16NumberTest extends TestCase
{
	private U16Fixed16Number	data_;

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
			U16Fixed16Number empty = new U16Fixed16Number();

			U16Fixed16Number one = new U16Fixed16Number(1);
			assertEquals(1.0, one.doubleValue());

			U16Fixed16Number copy = new U16Fixed16Number(one);
			assertEquals(1.0, copy.doubleValue());

			U16Fixed16Number minv = new U16Fixed16Number(U16Fixed16Number.MIN_VALUE);
			assertEquals(U16Fixed16Number.MIN_VALUE,minv.doubleValue());

			U16Fixed16Number maxv = new U16Fixed16Number(U16Fixed16Number.MAX_VALUE);
			assertEquals(U16Fixed16Number.MAX_VALUE,maxv.doubleValue());

			byte[] ba = new byte[]
			{
				(byte)0,(byte)60,(byte)15,(byte)248
			};
			U16Fixed16Number inst1 = new U16Fixed16Number(ba);
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
		U16Fixed16Number data = new U16Fixed16Number();
		byte[] ba = new byte[]
		{
			(byte)85,(byte)6,(byte)247,(byte)140,(byte)40,(byte)199,(byte)119,(byte)137
		};
		try
		{
			data.fromByteArray(ba, 0, 4);
			byte[] res = data.toByteArray();
			compareBytes(ba,0,4,res);
			data.fromByteArray(ba, 4, 4);
			byte[] re2 = data.toByteArray();
			compareBytes(ba,4,4,re2);
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
			data.fromByteArray(ba, ba.length, U16Fixed16Number.SIZE);
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
		U16Fixed16Number n = new U16Fixed16Number();
		try
		{
			n.parseValue(U16Fixed16Number.MIN_VALUE);
			assertEquals(U16Fixed16Number.MIN_VALUE, n.doubleValue(), 0.00000001);
			n.parseValue(U16Fixed16Number.MID_VALUE);
			assertEquals(U16Fixed16Number.MID_VALUE, n.doubleValue(), 0.00000001);
			n.parseValue(U16Fixed16Number.MAX_VALUE);
			assertEquals(U16Fixed16Number.MAX_VALUE, n.doubleValue(), 0.00000001);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testDoubleValue()
	{
		//done in testParseValue()
	}

	public void testSize()
	{
		U16Fixed16Number data = new U16Fixed16Number();
		assertEquals(U16Fixed16Number.SIZE, data.size());
	}

	public void testToString()
	{
		try
		{
			U16Fixed16Number data = new U16Fixed16Number(123.0);
			String bs = data.toString();
			String s = "123.0";	//original string
			assertEquals(s, bs);
		}
		catch (ICCProfileException e)
		{
			assertFalse(true);
		}
	}


}
