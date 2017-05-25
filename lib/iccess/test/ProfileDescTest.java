
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

//TODO: original class not ready
public class ProfileDescTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,	//deviceMakerSig
		(byte)0x45,(byte)0x46,(byte)0x47,(byte)0x48,	//deviceModelSig
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,	//attributes
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,	//tech info
		
		(byte)0x6D,(byte)0x6C,(byte)0x75,(byte)0x63,	//signature for deviceMfgDesc
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,	//numNames_:UInt32Number
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,	//nameRecSize_:UInt32Number
		
		(byte)0x6D,(byte)0x6C,(byte)0x75,(byte)0x63,	//signature for deviceModelDesc
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,	//numNames_:UInt32Number
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C	//nameRecSize_:UInt32Number
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

	public void testFromByteArray()
	{
		ProfileDesc data = new ProfileDesc();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertEquals(0x41424344, data.getDeviceMakerSignature().intValue());
			assertEquals(0x45464748, data.getDeviceModelSignature().intValue());
			assertEquals(0x1L, data.getDeviceAttributes().longValue());
			assertEquals(0, data.getDeviceTechInfo().intValue());
			MultiLocalizedUnicodeType manufacturer = data.getDeviceMakerDesc();
			assertEquals(MultiLocalizedUnicodeType.SIGNATURE, manufacturer.getSignature().intValue());
			MultiLocalizedUnicodeType model = data.getDeviceModelDesc();
			assertEquals(MultiLocalizedUnicodeType.SIGNATURE, model.getSignature().intValue());
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
			ProfileDesc ct = new ProfileDesc(ba);
			byte[] bac = ct.toByteArray();
			assertEquals(ct.size(), bac.length);
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			ProfileDesc ct = new ProfileDesc();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}


}
