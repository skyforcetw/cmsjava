
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class SignatureTest extends TestCase
{
	private Signature	data_;

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
			Signature empty = new Signature();

			byte[] ba = new byte[]
			{
				(byte)65,(byte)66,(byte)67,(byte)68
			};
			Signature inst1 = new Signature(ba);
			assertEquals("ABCD", inst1.getSignature());
			byte[] bac = inst1.toByteArray();
			compareBytes(ba, bac);

			Signature inst2 = new Signature(inst1);
			assertEquals("ABCD", inst2.getSignature());

			Signature st = new Signature("data");
			assertEquals("data", st.getSignature());
		}
		catch (ICCProfileException e)
		{
			System.err.println("Error occurred:"+e.getMessage());
		}
	}

	public void testFromByteArray()
	{
		Signature data = new Signature();
		byte[] ba = new byte[]
		{
			(byte)97,(byte)98,(byte)99,(byte)100,
			(byte)101,(byte)102,(byte)103,(byte)104
		};
		try
		{
			data.fromByteArray(ba, 0, 4);
			assertEquals("abcd", data.getSignature());
			byte[] res = data.toByteArray();
			compareBytes(ba,0,0,res);
			data.fromByteArray(ba, 4, 4);
			assertEquals("efgh", data.getSignature());
			byte[] re2 = data.toByteArray();
			compareBytes(ba,0,0,re2);
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
			data.fromByteArray(ba, ba.length, 4);
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

	public void testSetSignature()
	{
		Signature sig = new Signature();
		try
		{
			sig.setSignature("text");
			assertEquals("text", sig.getSignature());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testGetSignature()
	{
		//done in testSetSignature()
	}


}
