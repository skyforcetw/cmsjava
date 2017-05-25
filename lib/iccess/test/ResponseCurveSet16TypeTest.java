
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ResponseCurveSet16TypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x72,(byte)0x63,(byte)0x73,(byte)0x32,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x01,	//numChannels_:UInt16Number (1)
		(byte)0x00,(byte)0x01,	//numMeasurements_:UInt16Number (1)
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,	//offsets_:UInt32Number[]
		//CurveStructure[0]
		(byte)0x53,(byte)0x74,(byte)0x61,(byte)0x41,	//unit sig, StaA
		(byte)0,(byte)0,(byte)0,(byte)1,	//num measurements for each channel
		
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//patch XYZNumber[1]
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,
		
		(byte)0x7f,(byte)0xff, //UInt16Number for Response16Number[1]
		(byte)0,(byte)0,		//reserved
		(byte)0x80,(byte)0,(byte)0,(byte)0	//S15Fixed16Number
	};


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
			ResponseCurveSet16Type empty = new ResponseCurveSet16Type();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			ResponseCurveSet16Type inst1 = new ResponseCurveSet16Type(ba);
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
		ResponseCurveSet16Type data = new ResponseCurveSet16Type();
		try
		{
			data.fromByteArray(ba, 0, 0);
			//byte[] res = data.toByteArray();
			assertEquals(ResponseCurveSet16Type.SIGNATURE, data.getSignature().intValue());
			assertEquals(1, data.getNumChannels().intValue());
			assertEquals(1, data.getMeasurementTypes().intValue());
			UInt32Number[] offsets = data.getOffsets();
			assertEquals(1, offsets.length);
			assertEquals(0x10, offsets[0].intValue());
			CurveStructure[] cs = data.getCurveStructures();
			assertEquals(1, cs.length);
			assertEquals(0x53746141, cs[0].getMeasureUnitSignature().intValue());
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
			ResponseCurveSet16Type ct = new ResponseCurveSet16Type(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			ResponseCurveSet16Type ct = new ResponseCurveSet16Type();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}


}
