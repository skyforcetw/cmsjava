
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ProfileFlagTest extends TestCase
{
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
		ProfileFlag empty = new ProfileFlag();
		try
		{
			ProfileFlag one = new ProfileFlag(1);
			assertEquals(1, one.intValue());
			ProfileFlag copy = new ProfileFlag(one);
			assertEquals(1, one.intValue());
			byte[] ba = new byte[]
			{
				(byte)0,(byte)0,(byte)0,(byte)1
			};
			ProfileFlag inst1 = new ProfileFlag(ba);
			assertEquals(1, inst1.intValue());
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
		ProfileFlag data = new ProfileFlag();
		byte[] ba = new byte[]
		{
			(byte)0,(byte)0,(byte)0,(byte)1,
			(byte)0,(byte)0,(byte)0,(byte)2
		};
		try
		{
			data.fromByteArray(ba, 0, 4);
			byte[] res = data.toByteArray();
			compareBytes(ba,0,4,res);
			data.fromByteArray(ba, 4, 4);
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

	public void testSetEmbeded()
	{
		ProfileFlag pf = new ProfileFlag();
		pf.setValue(ProfileFlag.EMBED_FLAG);
		assertTrue(pf.isEmbedded());
	}

	public void testSetIndependent()
	{
		ProfileFlag pf = new ProfileFlag();
		pf.setIndependent();
		assertTrue(pf.isIndependent());
	}

	public void testIsEmbedded()
	{
	}

	public void testIsIndependent()
	{
	}

	public void testToString()
	{
		ProfileFlag data = new ProfileFlag(1);
		String bs = data.toString();
		String s = "Embedded,Dependent";	//original string
		assertEquals(s, bs);
	}


}
