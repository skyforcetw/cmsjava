
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class XYZNumberTest extends TestCase
{
	private XYZNumber	data_;

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
			XYZNumber empty = new XYZNumber();

			byte[] ba = new byte[]
			{
				(byte)0,(byte)81,(byte)243,(byte)201,
				(byte)132,(byte)18,(byte)132,(byte)22,
				(byte)115,(byte)19,(byte)54,(byte)198
			};
			XYZNumber inst1 = new XYZNumber(ba);
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
		XYZNumber data = new XYZNumber();
		byte[] ba = new byte[]
		{
			(byte)199,(byte)121,(byte)253,(byte)166,(byte)230,(byte)78,(byte)50,(byte)4,(byte)199,(byte)131,(byte)60,(byte)231,
			(byte)8,(byte)69,(byte)185,(byte)161,(byte)39,(byte)139,(byte)126,(byte)146,(byte)18,(byte)104,(byte)238,(byte)177
		};
		try
		{
			data.fromByteArray(ba, 0, 12);
			byte[] res = data.toByteArray();
			compareBytes(ba,0,12,res);
			data.fromByteArray(ba,12,12);
			byte[] re2 = data.toByteArray();
			compareBytes(ba,12,12,re2);
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
			data.fromByteArray(ba, ba.length, XYZNumber.SIZE);
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

	public void testSize()
	{
		XYZNumber data = new XYZNumber();
		assertEquals(XYZNumber.SIZE, data.size());
	}

	public void testGetCIEX()
	{
		try
		{
			XYZNumber data = new XYZNumber(1.5,2.5,3.5);
			assertEquals(1.5, data.getCIEX().doubleValue(), 0.000001);
			assertEquals(2.5, data.getCIEY().doubleValue(), 0.000001);
			assertEquals(3.5, data.getCIEZ().doubleValue(), 0.000001);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testGetCIEY()
	{
		//ignore
	}

	public void testGetCIEZ()
	{
		//ignore
	}

	public void testToString()
	{
		try
		{
			XYZNumber data = new XYZNumber(1.,2.,3.);
			//TODO: add setValue here or in constructor
			String bs = data.toString();
			String s = "1.0,2.0,3.0";	//original string
			assertEquals(s, bs);
		}
		catch (ICCProfileException e)
		{
			assertFalse(true);
		}
	}


}
