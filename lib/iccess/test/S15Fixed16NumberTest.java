
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class S15Fixed16NumberTest extends TestCase
{
	private S15Fixed16Number	data_;

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
			S15Fixed16Number empty = new S15Fixed16Number();

			S15Fixed16Number one = new S15Fixed16Number(1.0);
			assertEquals(1.0, one.doubleValue());

			S15Fixed16Number copy = new S15Fixed16Number(one);
			assertEquals(1.0, copy.doubleValue());

			S15Fixed16Number minv = new S15Fixed16Number(S15Fixed16Number.MIN_VALUE);
			assertEquals(S15Fixed16Number.MIN_VALUE,minv.doubleValue());

			S15Fixed16Number maxv = new S15Fixed16Number(S15Fixed16Number.MAX_VALUE);
			assertEquals(S15Fixed16Number.MAX_VALUE,maxv.doubleValue());

			byte[] ba = new byte[]
			{
				(byte)0,(byte)239,(byte)238,(byte)184
			};
			S15Fixed16Number inst1 = new S15Fixed16Number(ba);
			assertEquals(239.9324951171875, inst1.doubleValue(), 0.00000001);
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
		S15Fixed16Number data = new S15Fixed16Number();
		byte[] ba = new byte[]
		{
			(byte)125,(byte)30,(byte)208,(byte)13,(byte)240,(byte)151,(byte)179,(byte)97
		};
		try
		{
			data.fromByteArray(ba, 0, 4);
			byte[] res = data.toByteArray();
			compareBytes(ba,0,4,res);
			data.fromByteArray(ba,4,4);
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
			data.fromByteArray(ba, ba.length, S15Fixed16Number.SIZE);
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

	public void testParseValue()
	{
		S15Fixed16Number n = new S15Fixed16Number();
		try
		{
			n.parseValue(S15Fixed16Number.MIN_VALUE);
			assertEquals(S15Fixed16Number.MIN_VALUE, n.doubleValue(), 0.00000001);
			n.parseValue(S15Fixed16Number.MID_VALUE);
			assertEquals(S15Fixed16Number.MID_VALUE, n.doubleValue(), 0.00000001);
			n.parseValue(S15Fixed16Number.MAX_VALUE);
			assertEquals(S15Fixed16Number.MAX_VALUE, n.doubleValue(), 0.00000001);
			n.parseValue(-0.0525);
			assertEquals(-0.0525, n.doubleValue(), 0.00001);
			n.parseValue(0.0525);
			assertEquals(0.0525, n.doubleValue(), 0.00001);
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
		S15Fixed16Number data = new S15Fixed16Number();
		assertEquals(S15Fixed16Number.SIZE, data.size());
	}

	public void testToString()
	{
		try
		{
			S15Fixed16Number data = new S15Fixed16Number(123.0);
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
