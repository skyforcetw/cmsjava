
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class PlatformTest extends TestCase
{
	private int[] codes_ = new int[]
	{
		Platform.APPLE, Platform.MICROSOFT, Platform.SILICONGRAPHICS, Platform.SUN
	};
	private String[] names_ = new String[]
	{
		"APPL","MSFT","SGI ","SUNW"
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
		Platform empty = new Platform();
		try
		{
			Platform sun = new Platform(Platform.SUN);
			Platform copy = new Platform(sun);
			assertEquals(Platform.SUN, copy.intValue());
			byte[] ba = new byte[]
			{
				(byte)0x41,(byte)0x50,(byte)0x50,(byte)0x4C
			};
			Platform inst1 = new Platform(ba);
			assertEquals(Platform.APPLE, inst1.intValue());
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
		Platform data = new Platform();
		byte[] ba = new byte[]
		{
			(byte)0x53,(byte)0x55,(byte)0x4E,(byte)0x57,
			(byte)0x53,(byte)0x47,(byte)0x49,(byte)0x20
		};
		try
		{
			data.fromByteArray(ba, 0, 4);
			assertEquals(Platform.SUN, data.intValue());
			byte[] res = data.toByteArray();
			compareBytes(ba,0,4,res);
			data.fromByteArray(ba, 4, 4);
			assertEquals(Platform.SILICONGRAPHICS, data.intValue());
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
		Platform pf = new Platform();
		for (int i=0; i<codes_.length; i++)
		{
			pf.setValue(codes_[i]);
			assertEquals(names_[i], pf.toString());
		}
	}

	public void testSetApple()
	{
		Platform pf = new Platform();
		pf.setApple();
		assertEquals(Platform.APPLE, pf.intValue());
	}

	public void testSetMicrosoft()
	{
		Platform pf = new Platform();
		pf.setMicrosoft();
		assertEquals(Platform.MICROSOFT, pf.intValue());
	}

	public void testSetSiliconGraphics()
	{
		Platform pf = new Platform();
		pf.setSiliconGraphics();
		assertEquals(Platform.SILICONGRAPHICS, pf.intValue());
	}

	public void testSetSun()
	{
		Platform pf = new Platform();
		pf.setSun();
		assertEquals(Platform.SUN, pf.intValue());
	}

	public void testIsMicrosoft()
	{
	}

	public void testIsApple()
	{
	}

	public void testIsSiliconGraphics()
	{
	}

	public void testIsSun()
	{
	}

	public void testToString()
	{
		Platform data = new Platform(Platform.SILICONGRAPHICS);
		String bs = data.toString();
		String s = "SGI ";	//original string
		assertEquals(s, bs);
	}
}
