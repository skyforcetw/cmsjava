
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class Response16NumberTest extends TestCase
{
	private Response16Number	data_;

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
			Response16Number empty = new Response16Number();

			byte[] ba = new byte[]
				{
					(byte)0xff,(byte)0xff, //UInt16Number
					(byte)0,(byte)0,		//reserved
					(byte)0x7f,(byte)0xff,(byte)0xff,(byte)0xff	//S15Fixed16Number
				};
			Response16Number inst1 = new Response16Number(ba);
			assertEquals(65535, inst1.getDeviceCode());
			assertEquals(S15Fixed16Number.MAX_VALUE, inst1.getMeasurement(), 0.000000000000001);
		}
		catch (ICCProfileException e)
		{
			System.err.println("Error occurred:"+e.getMessage());
		}
	}

	public void testFromByteArray()
	{
		Response16Number data = new Response16Number();
		byte[] ba = new byte[]
			{
				(byte)0,(byte)0,
				(byte)0x7f,(byte)0xff, //UInt16Number
				(byte)0,(byte)0,		//reserved
				(byte)0x80,(byte)0,(byte)0,(byte)0	//S15Fixed16Number
			};
		try
		{
			data.fromByteArray(ba, 2, 8);
			assertEquals(32767, data.getDeviceCode());
			assertEquals(S15Fixed16Number.MIN_VALUE, data.getMeasurement());
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
			data.fromByteArray(ba, ba.length, Response16Number.SIZE);
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

	public void testToByteArray()
	{
		byte[] vals = new byte[]
		{
			(byte)255,(byte)255,(byte)0,(byte)0,
			(byte)0,(byte)1,(byte)0,(byte)0};
		try 
		{
			Response16Number n = new Response16Number(vals);
			byte[] ret = n.toByteArray();
			compareBytes(vals, ret);
		} 
		catch (ICCProfileException e) 
		{
			System.err.println(e.getMessage());
		}
	}

	public void testGetDeviceCode()
	{
		try
		{
			Response16Number r = new Response16Number(65535, 1.0);
			assertEquals(65535, r.getDeviceCode());
		}
		catch (ICCProfileException e)
		{
			assertFalse(true);
		}
	}

	public void testGetMeasurement()
	{
		try
		{
			Response16Number r = new Response16Number(65535, 1.0);
			assertEquals(1.0, r.getMeasurement());
		}
		catch (ICCProfileException e)
		{
			assertFalse(true);
		}
	}

	public void testSize()
	{
		Response16Number data = new Response16Number();
		assertEquals(Response16Number.SIZE, data.size());
	}

	public void testToString()
	{
		try
		{
			Response16Number r = new Response16Number(65535, 1.0);
			assertEquals("65535,0,1.0", r.toString());
		}
		catch (ICCProfileException e)
		{
			assertFalse(true);
		}
	}


}
