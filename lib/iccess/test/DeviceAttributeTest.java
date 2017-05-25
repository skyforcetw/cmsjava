
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class DeviceAttributeTest extends TestCase
{
	private DeviceAttribute	data_;

	public void setUp()
	{
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
		try
		{
			DeviceAttribute empty = new DeviceAttribute();
			DeviceAttribute one = new DeviceAttribute(1L);
			assertEquals(1L, one.longValue());
			DeviceAttribute copy = new DeviceAttribute(one);
			assertEquals(1L, copy.longValue());

			byte[] ba = new byte[]
			{
				(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0xff
			};
			DeviceAttribute inst1 = new DeviceAttribute(ba);
			byte[] bac = inst1.toByteArray();
			assertEquals(0xff, inst1.intValue());
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			System.err.println("Error occurred:"+e.getMessage());
		}
	}

	public void testSetters()
	{
		DeviceAttribute da = new DeviceAttribute(0L);
		da.setTransparent();
		da.setMatte();
		da.setNegative();
		da.setBlackWhite();
		assertEquals(0xf, da.intValue());
	}

	public void testSetTransparent()
	{
		DeviceAttribute da = new DeviceAttribute(0L);
		assertFalse(da.isTransparent());
		da.setTransparent();
		assertTrue(da.isTransparent());
	}

	public void testSetReflective()
	{
		DeviceAttribute da = new DeviceAttribute(255L);
		assertFalse(da.isReflective());
		da.setReflective();
		assertTrue(da.isReflective());
	}

	public void testSetMatte()
	{
		DeviceAttribute da = new DeviceAttribute(0L);
		assertFalse(da.isMatte());
		da.setMatte();
		assertTrue(da.isMatte());
	}

	public void testSetGlossy()
	{
		DeviceAttribute da = new DeviceAttribute(255L);
		assertFalse(da.isGlossy());
		da.setGlossy();
		assertTrue(da.isGlossy());
	}

	public void testSetNegative()
	{
		DeviceAttribute da = new DeviceAttribute(0L);
		assertFalse(da.isNegative());
		da.setNegative();
		assertTrue(da.isNegative());
	}

	public void testSetPositive()
	{
		DeviceAttribute da = new DeviceAttribute(255L);
		assertFalse(da.isPositive());
		da.setPositive();
		assertTrue(da.isPositive());
	}

	public void testSetBlackWhite()
	{
		DeviceAttribute da = new DeviceAttribute(0L);
		assertFalse(da.isBlackWhite());
		da.setBlackWhite();
		assertTrue(da.isBlackWhite());
	}

	public void testSetColor()
	{
		DeviceAttribute da = new DeviceAttribute(255L);
		assertFalse(da.isColor());
		da.setColor();
		assertTrue(da.isColor());
	}

	public void testIsTransparent()
	{
	}

	public void testIsReflective()
	{
	}

	public void testIsMatte()
	{
	}

	public void testIsGlossy()
	{
	}

	public void testIsNegative()
	{
	}

	public void testIsPositive()
	{
	}

	public void testIsBlackWhite()
	{
	}

	public void testIsColor()
	{
	}

	public void testToString()
	{
		DeviceAttribute data = new DeviceAttribute(0L);
		String bs = data.toString();
		String s = "Reflective,Glossy,Positive,Color";	//original string
		assertEquals(s, bs);
	}


}
