
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ChromaticityTypeTest extends TestCase
{
	private byte[] ba = new byte[]
		{
			(byte)0x63,(byte)0x68,(byte)0x72,(byte)0x6D, //signature
			(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
			(byte)0,(byte)1, //1 device channel
			(byte)0,(byte)1, //colorant code: ITU-R BT.709
			(byte)0,(byte)1,(byte)0,(byte)0,	//cie x value 1.0
			(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff	//cie y value 65535.9999847412109375
		};

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
			ChromaticityType empty = new ChromaticityType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());
			//ChromaticityType clone = new ChromaticityType(empty);

			ChromaticityType inst1 = new ChromaticityType(ba);
			byte[] bac = inst1.toByteArray();
			//compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
		}
	}

	public void testFromByteArray()
	{
		ChromaticityType data = new ChromaticityType();
		try
		{
			data.fromByteArray(ba, 0, ba.length);
			byte[] res = data.toByteArray();
			//compareBytes(ba, res);
			assertEquals(0x6368726D, data.getSignature().intValue());
			assertEquals(1, data.getDeviceChannels().intValue());
			assertEquals(1, data.getColorantCode().intValue());
			U16Fixed16Number[] cies = data.getCIEXYCoords();
			assertEquals(1.0, cies[0].doubleValue(), 0.001);
			assertEquals(65535.9999847412109375, cies[1].doubleValue(), 0.00000001);
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
			data.fromByteArray(ba, ba.length, ba.length);
			assertFalse("index out of bounds, should raise exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(e.getMessage(),ICCProfileException.IndexOutOfBoundsException, e.getType());
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
		try
		{
			ChromaticityType ct = new ChromaticityType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			ChromaticityType ct = new ChromaticityType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testGetDeviceChannels()
	{
		try
		{
			ChromaticityType ct = new ChromaticityType(ba);
			assertEquals(1, ct.getDeviceChannels().intValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
		}
	}

	public void testGetColorantCode()
	{
		try
		{
			ChromaticityType ct = new ChromaticityType(ba);
			assertEquals(1, ct.getColorantCode().intValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
		}
	}

	public void testGetCIEXYCoords()
	{
		try
		{
			ChromaticityType ct = new ChromaticityType(ba);
			U16Fixed16Number[] colorants = ct.getCIEXYCoords();
			assertEquals(1.0, colorants[0].doubleValue());
			assertEquals(65535.9999847412109375, colorants[1].doubleValue(), 0.000001);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
		}
	}
}
