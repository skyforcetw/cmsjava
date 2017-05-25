
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class RenderIntentTest extends TestCase
{
	private int[] codes_ = new int[]
	{
		RenderIntent.PERCEPTUAL, RenderIntent.MEDIA_RELATIVE,
		RenderIntent.SATURATION, RenderIntent.ABSOLUTE
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
			RenderIntent empty = new RenderIntent();

			byte[] ba = new byte[]
			{
				(byte)0,(byte)0,(byte)0,(byte)2
			};
			RenderIntent inst1 = new RenderIntent(ba);
			assertEquals(RenderIntent.SATURATION, inst1.intValue());
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
		RenderIntent data = new RenderIntent();
		byte[] ba = new byte[]
		{
			(byte)0,(byte)0,(byte)0,(byte)1,
			(byte)0,(byte)0,(byte)0,(byte)3
		};
		try
		{
			data.fromByteArray(ba, 0, 4);
			byte[] res = data.toByteArray();
			compareBytes(ba,0,4,res);
			data.fromByteArray(ba,4,4);
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
		RenderIntent ri = new RenderIntent();
		for (int i=0; i<codes_.length; i++)
		{
			ri.setValue(codes_[i]);
			assertEquals(codes_[i], ri.intValue());
		}
	}

	public void testSetPerceptual()
	{
		RenderIntent ri = new RenderIntent();
		ri.setPerceptual();
		assertTrue(ri.isPerceptual());
	}

	public void testSetMediaRelative()
	{
		RenderIntent ri = new RenderIntent();
		ri.setMediaRelative();
		assertTrue(ri.isMediaRelative());
	}

	public void testSetSaturation()
	{
		RenderIntent ri = new RenderIntent();
		ri.setSaturation();
		assertTrue(ri.isSaturation());
	}

	public void testSetAbsolute()
	{
		RenderIntent ri = new RenderIntent();
		ri.setAbsolute();
		assertTrue(ri.isAbsolute());
	}

	public void testIsPerceptual()
	{
	}

	public void testIsMediaRelative()
	{
	}

	public void testIsSaturation()
	{
	}

	public void testIsAbsolute()
	{
	}

	public void testToString()
	{
		RenderIntent data = new RenderIntent(3);
		String bs = data.toString();
		String s = "Absolute";	//original string
		assertEquals(s, bs);
	}


}
