
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class LutAtoBTypeTest extends TestCase
{
	//TODO: this is unreasonable data sample, use valid data for complete testing!
	private byte[] ba = new byte[]
	{
		(byte)0x6D,(byte)0x41,(byte)0x42,(byte)0x20,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)1,	//numInputChannels_:UInt8Number
		(byte)1,	//numOutputChannels_:UInt8Number
		(byte)0,(byte)0,  //reserved for padding
		(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,	//offsetBcurve_:UInt32Number
		(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,	//offsetMatrix_:UInt32Number
		(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,	//offsetMcurve_:UInt32Number
		(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,	//offsetClut_:UInt32Number
		(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,	//offsetAcurve_:UInt32Number
		(byte)0x00,(byte)0x01	//data_:byte[]
	};


	private void compareBytes(byte[] expected, byte[] result)
	{
		assertEquals("two byte array not same size",expected.length, result.length);
		for (int i=0; i<result.length; i++)
			assertEquals("byte "+i, expected[i], result[i]);
	}

	private void compareBytes(byte[] expected, int offset1, byte[] result, int offset2, int len)
	{
		for (int i=0; i<len; i++)
			assertEquals("byte "+i, expected[i+offset1], result[i+offset2]);
	}

	public void testConstructors()
	{
		try
		{
			LutAtoBType empty = new LutAtoBType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			LutAtoBType inst1 = new LutAtoBType(ba);
			byte[] bac = inst1.toByteArray();
			//compareBytes(ba, bac);

		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testFromByteArray()
	{
		LutAtoBType data = new LutAtoBType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			//byte[] res = data.toByteArray();
			assertEquals(LutAtoBType.SIGNATURE, data.getSignature().intValue());
			assertEquals(1, data.getNumInputChannels().intValue());
			assertEquals(1, data.getNumOutputChannels().intValue());
			assertEquals(0x80000000, data.getOffsetBcurve().intValue());
			assertEquals(0x80000000, data.getOffsetMatrix().intValue());
			assertEquals(0x80000000, data.getOffsetMcurve().intValue());
			assertEquals(0x80000000, data.getOffsetClut().intValue());
			assertEquals(0x80000000, data.getOffsetAcurve().intValue());
			byte[] b = data.getData();
			compareBytes(ba, 32, b, 0, b.length);
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
			LutAtoBType ct = new LutAtoBType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			LutAtoBType ct = new LutAtoBType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}


}
