
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ViewingConditionsTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x76,(byte)0x69,(byte)0x65,(byte)0x77,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//illuminantXYZ_:XYZNumber
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//surroundXYZ_:XYZNumber
		(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00	//illuminantType_:UInt32Number, 0x80000000
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
			ViewingConditionsType empty = new ViewingConditionsType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			ViewingConditionsType inst1 = new ViewingConditionsType(ba);
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
		ViewingConditionsType data = new ViewingConditionsType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			//byte[] res = data.toByteArray();
			assertEquals(ViewingConditionsType.SIGNATURE, data.getSignature().intValue());
			//TODO: check for []
			XYZNumber xyz = data.getIlluminantXYZ();
			assertEquals(1.0,xyz.getCIEX().doubleValue());
			assertEquals(1.0,xyz.getCIEY().doubleValue());
			assertEquals(1.0,xyz.getCIEZ().doubleValue());
			//TODO: check for []
			xyz = data.getSurroundXYZ();
			assertEquals(1.0,xyz.getCIEX().doubleValue());
			assertEquals(1.0,xyz.getCIEY().doubleValue());
			assertEquals(1.0,xyz.getCIEZ().doubleValue());
			assertEquals(0x80000000, data.getIlluminantType().intValue());
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
			ViewingConditionsType ct = new ViewingConditionsType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			ViewingConditionsType ct = new ViewingConditionsType();
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
		ViewingConditionsType ct = new ViewingConditionsType();
		try
		{
			XYZNumber xyz = new XYZNumber(1.0,2.0,3.0);
			ct.setIlluminantXYZ(xyz);
			ct.setSurroundXYZ(xyz);
			assertEquals(1.0, ct.getIlluminantXYZ().getCIEX().doubleValue());
			assertEquals(3.0, ct.getIlluminantXYZ().getCIEZ().doubleValue());
			assertEquals(2.0, ct.getSurroundXYZ().getCIEY().doubleValue());
			ct.setIlluminantType(1);
			assertEquals(1, ct.getIlluminantType().intValue());
			ct.setIlluminantType(new UInt32Number(2));
			assertEquals(2, ct.getIlluminantType().intValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}
}
