
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ICCProfileHeaderTest extends TestCase
{
	int[] data0 = new int[]
		{
			0x00, 0x00, 0x3F, 0x38, 0x4B, 0x43, 0x4D, 0x53, 0x02, 0x10, 0xCD, 0xCD, 0x73, 0x63, 0x6E, 0x72, 
			0x52, 0x47, 0x42, 0x20, 0x4C, 0x61, 0x62, 0x20, 0x07, 0xCF, 0x00, 0x0B, 0x00, 0x02, 0x00, 0x09, 
			0x00, 0x17, 0x00, 0x07, 0x61, 0x63, 0x73, 0x70, 0x4D, 0x53, 0x46, 0x54, 0x00, 0x00, 0x00, 0x00, 
			0x50, 0x52, 0x4D, 0x58, 0x75, 0x6E, 0x66, 0x6D, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xF6, 0xD6, 0x00, 0x01, 0x00, 0x00, 0x00, 0x00, 0xD3, 0x2D, 
			0x61, 0x72, 0x67, 0x6C, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 
			0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 
			0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 0xCD, 
			0x00, 0x00, 0x00, 0x0C, 0x63, 0x70, 0x72, 0x74, 0x00, 0x00, 0x01, 0x14, 0x00, 0x00, 0x00, 0x4C, 
			0x64, 0x6D, 0x6E, 0x64, 0x00, 0x00, 0x01, 0x60, 0x00, 0x00, 0x00, 0x5F, 0x64, 0x6D, 0x64, 0x64, 
			0x00, 0x00, 0x01, 0xC0, 0x00, 0x00, 0x00, 0x66, 0x64, 0x65, 0x73, 0x63, 0x00, 0x00, 0x02, 0x28, 
			0x00, 0x00, 0x00, 0x6B, 0x72, 0x58, 0x59, 0x5A, 0x00, 0x00, 0x02, 0x94, 0x00, 0x00, 0x00, 0x14, 
			0x67, 0x58, 0x59, 0x5A, 0x00, 0x00, 0x02, 0xA8, 0x00, 0x00, 0x00, 0x14, 0x62, 0x58, 0x59, 0x5A, 
			0x00, 0x00, 0x02, 0xBC, 0x00, 0x00, 0x00, 0x14, 0x77, 0x74, 0x70, 0x74, 0x00, 0x00, 0x02, 0xD0, 
			0x00, 0x00, 0x00, 0x14, 0x72, 0x54, 0x52, 0x43, 0x00, 0x00, 0x02, 0xE4, 0x00, 0x00, 0x02, 0x0C, 
			0x67, 0x54, 0x52, 0x43, 0x00, 0x00, 0x04, 0xF0, 0x00, 0x00, 0x02, 0x0C, 0x62, 0x54, 0x52, 0x43, 
			0x00, 0x00, 0x06, 0xFC, 0x00, 0x00, 0x02, 0x0C, 0x41, 0x32, 0x42, 0x30, 0x00, 0x00, 0x09, 0x08, 
			0x00, 0x00, 0x36, 0x30, 0x74, 0x65, 0x78, 0x74, 0x00, 0x00, 0x00, 0x00, 0x43, 0x4F, 0x50, 0x59, 
			0x52, 0x49, 0x47, 0x48, 0x54, 0x20, 0x28, 0x63, 0x29, 0x20, 0x31, 0x39, 0x39, 0x39, 0x20, 0x4B, 
			0x57, 0x20, 0x4C, 0x45, 0x45, 0x28, 0x62, 0x69, 0x72, 0x64, 0x29, 0x2C, 0x20, 0x4E, 0x6F, 0x20, 
			0x72, 0x69, 0x67, 0x68, 0x74, 0x73, 0x20, 0x72, 0x65, 0x73, 0x65, 0x72, 0x76, 0x65, 0x64, 0x2C, 
			0x20, 0x77, 0x65, 0x6C, 0x63, 0x6F, 0x6D, 0x65, 0x20, 0x74, 0x6F, 0x20, 0x75, 0x73, 0x65, 0x00, 
			0x64, 0x65, 0x73, 0x63, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x05, 0x55, 0x4D, 0x41, 0x58, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x64, 0x65, 0x73, 0x63, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0C, 0x41, 0x73, 0x74, 0x72, 
			0x61, 0x20, 0x32, 0x31, 0x30, 0x30, 0x55, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x64, 0x65, 0x73, 0x63, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x11, 0x55, 0x4D, 0x41, 0x58, 0x20, 0x41, 0x73, 0x74, 0x72, 0x61, 0x20, 0x32, 
			0x31, 0x30, 0x30, 0x55, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x58, 0x59, 0x5A, 0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x5B, 0x49, 
			0x00, 0x00, 0x33, 0xB4, 0x00, 0x00, 0x03, 0x77, 0x58, 0x59, 0x5A, 0x20, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x68, 0xF6, 0x00, 0x00, 0x42, 0x63, 0x00, 0x00, 0x03, 0x80, 0x58, 0x59, 0x5A, 0x20, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x87, 0x7B, 0x00, 0x00, 0x1D, 0x85, 0x00, 0x01, 0x7D, 0xB8, 
			0x58, 0x59, 0x5A, 0x20, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0xC0, 0x57, 0x00, 0x00, 0xC7, 0x9E, 
			0x00, 0x00, 0x9C, 0x86, 0x63, 0x75, 0x72, 0x76, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
			0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
		};
	byte[] data1;

	public void setUp()
	{
		data1 = new byte[data0.length];
		for (int i=0; i<data0.length; i++)
		{
			data1[i] = (byte)data0[i];
		}
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
		ICCProfileHeader header = new ICCProfileHeader();
		assertTrue(header != null);
		try 
		{
			ICCProfileHeader header2 = new ICCProfileHeader(data1);
			assertTrue(header2 != null);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
			System.err.println("ICCProfileException at constructing ICCProfileHeader instance");
		}
	}

	public void testParseByteArray()
	{
		byte[] ba = new byte[128];
		ICCProfileHeader data = new ICCProfileHeader();
		try
		{
			data.fromByteArray(ba, -1, ba.length);
			assertFalse("index out of bounds, should raise exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(ba, ba.length, ba.length);
			assertFalse("index out of bounds, should raise exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(ba, 0, 3);
			assertFalse("should raise wrong size exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.WrongSizeException, e.getType());
		}
		try
		{
			data.fromByteArray(null, 0, 0);
			assertFalse("should raise null pointer exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.NullPointerException, e.getType());
		}
	}

	public void testGetters()
	{
		try
		{
			ICCProfileHeader header = new ICCProfileHeader(data1);
			assertEquals("profile size",16184,header.getProfileSize());
			assertEquals("cmm type","KCMS",header.getPreferredCmmType());
			assertEquals("profile version major",2,header.getProfileVersionMajor());
			assertEquals("profile version minor",1,header.getProfileVersionMinor());
			assertEquals("profile version build",0,header.getProfileVersionBuild());
			assertEquals("device class","scnr",header.getDeviceClass());
			assertEquals("color space","RGB ",header.getColorSpace());
			assertEquals("PCS","Lab ",header.getPcs());
			assertEquals("datetime","1999-11-2 9:23:7",header.getDateTime());
			assertEquals("signautre","acsp",header.getSignature());
			assertEquals("platform signature","MSFT",header.getPlatformSignature());
			assertEquals("flags","Not Embedded,Dependent",header.getFlags());
			assertEquals("device manufacturoer","PRMX",header.getManufacturer());
			assertEquals("device model","unfm",header.getDeviceModel());
			assertEquals("device attrib","Reflective,Glossy,Positive,Color",header.getDeviceAttribute());
			assertEquals("render intent","Perceptual",header.getRenderIntent());
			XYZNumber illuminant = header.getIlluminant();
			assertEquals("wp-ciex",0.964203,illuminant.getCIEX().doubleValue(),0.00001);
			assertEquals("wp-ciey",1.0,illuminant.getCIEY().doubleValue(),0.00001);
			assertEquals("wp-ciez",0.824905,illuminant.getCIEZ().doubleValue(),0.00001);
		}
		catch (ICCProfileException e)
		{
			System.err.println(e.getMessage());
		}
	}

	public void testSetters()
	{
		ICCProfileHeader profile = new ICCProfileHeader();
		try
		{
			profile.setProfileSize(1000);
			assertEquals(1000,profile.getProfileSize());
			profile.setPreferredCmmType("blablabla");
			assertEquals("blab", profile.getPreferredCmmType());
			profile.setProfileVersion((short)4, (short)8, (short)15);
			assertEquals(0x048F0000, profile.getProfileVersion());
			assertEquals(4, profile.getProfileVersionMajor());
			assertEquals(8, profile.getProfileVersionMinor());
			assertEquals(15, profile.getProfileVersionBuild());
			profile.setDeviceClass(0x73636E72);
			assertEquals("scnr", profile.getDeviceClass());
			profile.setColorSpace(0x58595A20);
			assertEquals("XYZ ",profile.getColorSpace());
			profile.setPcs(0x58595A20);
			assertEquals("XYZ ",profile.getPcs());
			//TODO: not complete here
		}
		catch (ICCProfileException e)
		{
			System.err.println(e.getMessage());
		}
	}

	public void testSave()
	{
		try
		{
			java.io.File f = new java.io.File("test.icc");
			if (f.exists())
				f.delete();
			ICCFileWriter writer = new ICCFileWriter("test.icc");
			ICCProfileHeader header = new ICCProfileHeader(data1);
			header.save(writer);
			writer.close();
			//read back the file and compare
			java.io.FileInputStream fis = new java.io.FileInputStream("test.icc");
			byte[] b = new byte[128];
			fis.read(b);
			fis.close();
			//System.out.println("data size: "+data1.length);
			compareBytes(data1, 0, 128, b);
		}
		catch (ICCProfileException e)
		{
			System.err.println(e.getMessage());
		}
		catch (java.io.IOException x)
		{
			System.err.println(x.getMessage());
		}
	}

	public void testSize()
	{
		ICCProfileHeader data = new ICCProfileHeader();
		assertEquals(ICCProfileHeader.ICC_PROFILE_HEADER_SIZE, data.size());
	}
/*
	public void testGetProfileSize()
	{
		//ignore, tested in testGetters()
	}

	public void testSetProfileSize()
	{
	}

	public void testGetPreferredCmmType()
	{
	}

	public void testSetPreferredCmmType()
	{
	}

	public void testGetProfileVersion()
	{
	}

	public void testSetProfileVersion()
	{
	}

	public void testGetProfileVersionMajor()
	{
	}

	public void testGetProfileVersionMinor()
	{
	}

	public void testGetProfileVersionBuild()
	{
	}

	public void testGetDeviceClass()
	{
	}

	public void testSetDeviceClass()
	{
	}

	public void testGetColorSpace()
	{
	}

	public void testSetColorSpace()
	{
	}

	public void testGetPcs()
	{
	}

	public void testSetPcs()
	{
	}

	public void testGetDateTime()
	{
	}

	public void testSetDateTime()
	{
	}

	public void testGetSignature()
	{
	}

	public void testSetSignature()
	{
	}

	public void testGetPlatformSignature()
	{
	}

	public void testSetPlatformSignature()
	{
	}

	public void testGetFlags()
	{
	}

	public void testSetFlags()
	{
	}

	public void testGetManufacturer()
	{
	}

	public void testSetManufacturer()
	{
	}

	public void testGetDeviceModel()
	{
	}

	public void testSetDeviceModel()
	{
	}

	public void testGetDeviceAttribute()
	{
	}

	public void testSetDeviceAttribute()
	{
	}

	public void testGetRenderIntent()
	{
	}

	public void testSetRenderIntent()
	{
	}

	public void testGetIlluminant()
	{
	}

	public void testSetIlluminant()
	{
	}

	public void testGetCreatorSignature()
	{
	}

	public void testSetCreatorSignature()
	{
	}

	public void testSetProfileId()
	{
	}
*/
}