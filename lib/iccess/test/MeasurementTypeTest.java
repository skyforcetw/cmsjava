
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class MeasurementTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x6D,(byte)0x65,(byte)0x61,(byte)0x73,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,	//observerCode_:UInt32Number
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//xyzValues_:XYZNumber
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,	//measureGeometry_:UInt32Number
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,	//measureFlare_:UInt32Number
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01	//standardIlluminant_:UInt32Number
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
			MeasurementType empty = new MeasurementType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			MeasurementType inst1 = new MeasurementType(ba);
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
		MeasurementType data = new MeasurementType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			//byte[] res = data.toByteArray();
			assertEquals(MeasurementType.SIGNATURE, data.getSignature().intValue());
			assertEquals(1, data.getObserverCode().intValue());
			//TODO: check for []
			XYZNumber xyz = data.getXYZValues();
			assertEquals(1.0,xyz.getCIEX().doubleValue());
			assertEquals(1.0,xyz.getCIEY().doubleValue());
			assertEquals(1.0,xyz.getCIEZ().doubleValue());
			assertEquals(1, data.getMeasureGeometryCode().intValue());
			assertEquals(1, data.getMeasureFlareCode().intValue());
			assertEquals(1, data.getStandardIlluminantCode().intValue());
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
			MeasurementType ct = new MeasurementType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			MeasurementType ct = new MeasurementType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testGetObserverCode()
	{
	}

	public void testGetXYZValues()
	{
	}

	public void testGetMeasureGeometryCode()
	{
	}

	public void testGetMeasureFlareCode()
	{
	}

	public void testGetStandardIlluminantCode()
	{
	}

	public void testSetObserverCode()
	{
		MeasurementType mt = new MeasurementType();
		mt.setObserverCode(32768);
		assertEquals(32768, mt.getObserverCode().intValue());
	}

	public void testSetXYZValues()
	{
		MeasurementType mt = new MeasurementType();
		try
		{
			mt.setXYZValues(1.0,2.0,3.0);
			XYZNumber xyz = mt.getXYZValues();
			assertEquals(1.0, xyz.getCIEX().doubleValue());
			assertEquals(2.0, xyz.getCIEY().doubleValue());
			assertEquals(3.0, xyz.getCIEZ().doubleValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
		}
	}

	public void testSetMeasureGeometryCode()
	{
		MeasurementType mt = new MeasurementType();
		mt.setMeasureGeometryCode(32768);
		assertEquals(32768, mt.getMeasureGeometryCode().intValue());
	}

	public void testSetMeasureFlareCode()
	{
		MeasurementType mt = new MeasurementType();
		mt.setMeasureFlareCode(32768);
		assertEquals(32768, mt.getMeasureFlareCode().intValue());
	}

	public void testSetStandardIlluminantCode()
	{
		MeasurementType mt = new MeasurementType();
		mt.setStandardIlluminantCode(32768);
		assertEquals(32768, mt.getStandardIlluminantCode().intValue());
	}

}
