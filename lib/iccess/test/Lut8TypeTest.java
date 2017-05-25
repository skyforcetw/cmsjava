
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class Lut8TypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x6D,(byte)0x66,(byte)0x74,(byte)0x31,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x02,	//numInputChannels_:UInt8Number
		(byte)0x02,	//numOutputChannels_:UInt8Number
		(byte)0x02,	//numGridPoints_:UInt8Number
		(byte)0x00,	//padding_:UInt8Number
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//e00_:S15Fixed16Number
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//e01_:S15Fixed16Number
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//e02_:S15Fixed16Number
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//e10_:S15Fixed16Number
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//e11_:S15Fixed16Number
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//e12_:S15Fixed16Number
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//e20_:S15Fixed16Number
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//e21_:S15Fixed16Number
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//e22_:S15Fixed16Number
		(byte)0x00,(byte)0x02,	//numInputEntries_:UInt16Number
		(byte)0x00,(byte)0x02,	//numOutputEntries_:UInt16Number
		(byte)0x80,(byte)0x81,(byte)0x82,(byte)0x83,	//inputTables_:UInt8Number[4]
		(byte)0x80,(byte)0x81,(byte)0x82,(byte)0x83,	//clutValues_:UInt8Number[8]
		(byte)0x84,(byte)0x85,(byte)0x86,(byte)0x87,
		(byte)0x80,(byte)0x81,(byte)0x82,(byte)0x83	//outputTables_:UInt8Number[4]
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
			Lut8Type empty = new Lut8Type();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			Lut8Type inst1 = new Lut8Type(ba);
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
		Lut8Type data = new Lut8Type();
		try
		{
			data.fromByteArray(ba, 0, 0);
			//byte[] res = data.toByteArray();
			assertEquals(Lut8Type.SIGNATURE, data.getSignature().intValue());
			assertEquals(2, data.getNumInputChannels().intValue());
			assertEquals(2, data.getNumOutputChannels().intValue());
			assertEquals(2, data.getNumGridPoints().intValue());
			assertEquals(1.0, data.getE00().doubleValue());
			assertEquals(1.0, data.getE01().doubleValue());
			assertEquals(1.0, data.getE02().doubleValue());
			assertEquals(1.0, data.getE10().doubleValue());
			assertEquals(1.0, data.getE11().doubleValue());
			assertEquals(1.0, data.getE12().doubleValue());
			assertEquals(1.0, data.getE20().doubleValue());
			assertEquals(1.0, data.getE21().doubleValue());
			assertEquals(1.0, data.getE22().doubleValue());
			assertEquals(2, data.getNumInputEntries().intValue());
			assertEquals(2, data.getNumOutputEntries().intValue());
			UInt8Number[] it = data.getInputTables();
			assertEquals(4, it.length);
			for (int i=0; i<it.length; i++)
				assertEquals(0x80+i, it[i].intValue());
			UInt8Number[] cl = data.getClutValues();
			assertEquals(8, cl.length);
			for (int i=0; i<cl.length; i++)
				assertEquals(0x80+i, cl[i].intValue());
			UInt8Number[] ot = data.getOutputTables();
			assertEquals(4, ot.length);
			for (int i=0; i<ot.length; i++)
				assertEquals(0x80+i, ot[i].intValue());
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
			Lut8Type ct = new Lut8Type(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			Lut8Type ct = new Lut8Type();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testSetters()
	{
		//skip
	}
}
