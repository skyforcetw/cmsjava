
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

//TODO: ProfileDesc class not complete
public class ProfileSequenceDescTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x70,(byte)0x73,(byte)0x65,(byte)0x71,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,	//count_:UInt32Number
		//pds_:ProfileDesc[0]:
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
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,	//nameRecSize_:UInt32Number
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
			ProfileSequenceDescType empty = new ProfileSequenceDescType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			ProfileSequenceDescType inst1 = new ProfileSequenceDescType(ba);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testFromByteArray()
	{
		ProfileSequenceDescType data = new ProfileSequenceDescType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			//byte[] res = data.toByteArray();
			assertEquals(ProfileSequenceDescType.SIGNATURE, data.getSignature().intValue());
			assertEquals(1, data.getCount());
			ProfileDesc[] pd = data.getProfileDescs();
			assertEquals(1, pd.length);
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
			ProfileSequenceDescType ct = new ProfileSequenceDescType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			ProfileSequenceDescType ct = new ProfileSequenceDescType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}


}
