
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class NamedColor2TypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x6E,(byte)0x63,(byte)0x6C,(byte)0x32,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,	//vendorFlag_:UInt32Number
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,	//namedColors_:UInt32Number
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,	//numDeviceCoords_:UInt32Number
		(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,	//prefix_:TextType 32 bytes
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0x61,(byte)0x62,(byte)0x63,(byte)0x64,	//suffix_:TextType 32 bytes
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0x45,(byte)0x46,(byte)0x47,(byte)0x48,	//rootNames_:TextType 32
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x03,	//pcsCoords_:UInt16Number[1][3]
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x02	//deviceCoords_:UInt16Number[1][2]
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
			NamedColor2Type empty = new NamedColor2Type();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			NamedColor2Type inst1 = new NamedColor2Type(ba);
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
		NamedColor2Type data = new NamedColor2Type();
		try
		{
			data.fromByteArray(ba, 0, 0);
			//byte[] res = data.toByteArray();
			assertEquals(NamedColor2Type.SIGNATURE, data.getSignature().intValue());
			assertEquals(0x80000000, data.getVendorFlag().intValue());
			assertEquals(1, data.getNamedColors().intValue());
			assertEquals(2, data.getNumDeviceCoords().intValue());
			//TODO: check for []
			byte[] ttbs = data.getPrefix().toByteArray();
			compareBytes(ba, 20, ttbs, 8, ttbs.length-8);
			//TODO: check for []
			ttbs = data.getSuffix().toByteArray();
			compareBytes(ba, 52, ttbs, 8, ttbs.length-8);
			//TODO: check for []
			TextType[] tts = data.getRootNames();
			ttbs = tts[0].toByteArray();
			compareBytes(ba, 84, ttbs, 8, ttbs.length-8);
			UInt16Number[][] ar1 = data.getPcsCoords();
			assertEquals(1, ar1.length);
			assertEquals(3, ar1[0].length);
			assertEquals(1, ar1[0][0].intValue());
			assertEquals(2, ar1[0][1].intValue());
			assertEquals(3, ar1[0][2].intValue());
			UInt16Number[][] ar2 = data.getDeviceCoords();
			assertEquals(1, ar2.length);
			assertEquals(1, ar2[0][0].intValue());
			assertEquals(2, ar2[0][1].intValue());
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
			NamedColor2Type ct = new NamedColor2Type(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			NamedColor2Type ct = new NamedColor2Type();
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
		NamedColor2Type ct = new NamedColor2Type();
		try
		{
			ct.setVendorFlag(100);
			assertEquals(100, ct.getVendorFlag().intValue());
			ct.setPrefix("aaaa");
			assertEquals("aaaa", ct.getPrefix().getText().trim());
			ct.setSuffix("bbbb");
			assertEquals("bbbb", ct.getSuffix().getText().trim());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}		
	}

	public void testAddParameter()
	{
		NamedColor2Type ct = new NamedColor2Type();
		try
		{
			for (int i=0; i<5; i++)
			{
				int[] pcs = new int[]{i+100, i+200, i+300};
				int[] dev = new int[]{i+1000,i+2000,i+3000};
				ct.addParameter("aaaa"+String.valueOf(i), pcs, dev);
				assertEquals(i+1, ct.getNamedColors().intValue());
				assertEquals(3, ct.getNumDeviceCoords().intValue());
				TextType[] rns = ct.getRootNames();
				assertEquals("aaaa"+String.valueOf(i), rns[i].getText().trim());
				UInt16Number[][] bpcs = ct.getPcsCoords();
				assertEquals(i+100, bpcs[i][0].intValue());
				assertEquals(i+200, bpcs[i][1].intValue());
				assertEquals(i+300, bpcs[i][2].intValue());
				UInt16Number[][] bdev = ct.getDeviceCoords();
				assertEquals(i+1000, bdev[i][0].intValue());
				assertEquals(i+2000, bdev[i][1].intValue());
				assertEquals(i+3000, bdev[i][2].intValue());
			}
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}
}
