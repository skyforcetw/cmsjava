
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ProfileClassTest extends TestCase
{
	private int[] codes_ = new int[]
	{
		ProfileClass.INPUT_DEVICE,ProfileClass.DISPLAY_DEVICE,
		ProfileClass.OUTPUT_DEVICE,ProfileClass.DEVICE_LINK,
		ProfileClass.COLOR_CONV,ProfileClass.ABSTRACT,
		ProfileClass.NAMED_COLOR
	};
	private String[] names_ = new String[]
	{
		"scnr","mntr","prtr","link","spac","abst","nmcl"
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
		ProfileClass empty = new ProfileClass();
		try
		{
			ProfileClass p = new ProfileClass(ProfileClass.INPUT_DEVICE);
			assertEquals(ProfileClass.INPUT_DEVICE, p.intValue());
			ProfileClass copy = new ProfileClass(p);
			assertEquals(ProfileClass.INPUT_DEVICE, p.intValue());

			byte[] ba = new byte[]
			{
				(byte)0x73,(byte)0x63,(byte)0x6E,(byte)0x72
			};
			ProfileClass inst1 = new ProfileClass(ba);
			assertEquals(ProfileClass.INPUT_DEVICE, inst1.intValue());
			byte[] bac = inst1.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			System.err.println("Error occurred:"+e.getMessage());
		}
	}

	public void testFromByteArray()
	{
		ProfileClass data = new ProfileClass();
		byte[] ba = new byte[]
		{
			(byte)0x73,(byte)0x63,(byte)0x6E,(byte)0x72,	//imput
			(byte)0x6D,(byte)0x6E,(byte)0x74,(byte)0x72	//display
		};
		try
		{
			data.fromByteArray(ba, 0, 4);
			assertEquals(ProfileClass.INPUT_DEVICE, data.intValue());
			byte[] res = data.toByteArray();
			compareBytes(ba,0,4,res);
			data.fromByteArray(ba, 4, 4);
			assertEquals(ProfileClass.DISPLAY_DEVICE, data.intValue());
			byte[] re2 = data.toByteArray();
			compareBytes(ba,4,4,re2);
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
			assertEquals(ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(ba, ba.length, 4);
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

	public void testSetters()
	{
		ProfileClass pc = new ProfileClass();
		for (int i=0; i<codes_.length; i++)
		{
			pc.setValue(codes_[i]);
			assertEquals(names_[i], pc.toString());
		}
	}

	public void testSetInputDevice()
	{
		ProfileClass pc = new ProfileClass();
		pc.setInputDevice();
		assertTrue(pc.isInputDevice());
		assertEquals("scnr", pc.toString());
	}

	public void testSetDisplayDevice()
	{
		ProfileClass pc = new ProfileClass();
		pc.setDisplayDevice();
		assertTrue(pc.isDisplayDevice());
		assertEquals("mntr", pc.toString());
	}

	public void testSetOutputDevice()
	{
		ProfileClass pc = new ProfileClass();
		pc.setOutputDevice();
		assertTrue(pc.isOutputDevice());
		assertEquals("prtr", pc.toString());
	}

	public void testSetDeviceLink()
	{
		ProfileClass pc = new ProfileClass();
		pc.setDeviceLink();
		assertTrue(pc.isDeviceLink());
		assertEquals("link", pc.toString());
	}

	public void testSetColorConversion()
	{
		ProfileClass pc = new ProfileClass();
		pc.setColorConversion();
		assertTrue(pc.isColorConversion());
		assertEquals("spac", pc.toString());
	}

	public void testSetAbstract()
	{
		ProfileClass pc = new ProfileClass();
		pc.setAbstract();
		assertTrue(pc.isAbstract());
		assertEquals("abst", pc.toString());
	}

	public void testSetNamedColor()
	{
		ProfileClass pc = new ProfileClass();
		pc.setNamedColor();
		assertTrue(pc.isNamedColor());
		assertEquals("nmcl", pc.toString());
	}

	public void testIsInputDevice()
	{
	}

	public void testIsDisplayDevice()
	{
	}

	public void testIsOutputDevice()
	{
	}

	public void testIsDeviceLink()
	{
	}

	public void testIsColorConversion()
	{
	}

	public void testIsAbstract()
	{
	}

	public void testIsNamedColor()
	{
	}


}
