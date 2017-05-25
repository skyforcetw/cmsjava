
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ICCProfileTagEntryTest extends TestCase
{
	private ICCProfileTagEntry	data_;

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
			ICCProfileTagEntry tag1 = new ICCProfileTagEntry();
			assertEquals(null, tag1.getSignature());
			assertEquals(-1, tag1.getOffset());
			assertEquals(-1, tag1.getSize());

			ICCProfileTagEntry tag2 = new ICCProfileTagEntry(0x41424344, 100, 200);
			assertEquals(0x41424344, tag2.getSignature().intValue());
			assertEquals(100, tag2.getOffset());
			assertEquals(200, tag2.getSize());
			Signature s = tag2.getSignature();
			assertEquals("ABCD", s.getSignature());

			byte[] ba = new byte[]
				{
					(byte)0x01,(byte)2, (byte)3, (byte)4, 
					(byte)0x12, (byte)0x34, (byte)0, (byte)0,
					(byte)0, (byte)0, (byte)0, (byte)0xff
				};
			ICCProfileTagEntry tag3 = new ICCProfileTagEntry(ba, 0);
			assertEquals(0x01020304, tag3.getSignature().intValue());
			assertEquals(0x12340000, tag3.getOffset());
			assertEquals(255, tag3.getSize());
		} 
		catch (ICCProfileException e) 
		{
			assertFalse("should not throw exception", false);
		}
		try 
		{
			byte[] b = new byte[5];
			ICCProfileTagEntry failtag = new ICCProfileTagEntry(b, 0);
			assertFalse("should not come here", true);
		} 
		catch (ICCProfileException e) 
		{
			assertTrue("should throw exception", true);
		}
	}

	public void testFromByteArray()
	{
		byte[] ba = new byte[]
			{
				(byte)0,(byte)0,(byte)1,(byte)0x20,
				(byte)0,(byte)0,(byte)0,(byte)12,
				(byte)0,(byte)0,(byte)1,(byte)0,
				(byte)0,(byte)0,(byte)2,(byte)0x40,
				(byte)0,(byte)0,(byte)0,(byte)24,
				(byte)0,(byte)0,(byte)2,(byte)0,
			};
		ICCProfileTagEntry data = new ICCProfileTagEntry();
		try 
		{
			data.fromByteArray(ba, 0, 12);
			assertEquals(0x120, data.getSignature().intValue());
			assertEquals(12,data.getOffset());
			assertEquals(0x100, data.getSize());
			data.fromByteArray(ba, 12, 12);
			assertEquals(0x240, data.getSignature().intValue());
			assertEquals(24,data.getOffset());
			assertEquals(0x200, data.getSize());
		} 
		catch (ICCProfileException e) 
		{
			assertFalse("should not come here", false);
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

	public void testToByteArray()
	{
		byte[] ba = new byte[]
			{
				(byte)0x01,(byte)2, (byte)3, (byte)4, 
				(byte)0x12, (byte)0x34, (byte)0, (byte)0,
				(byte)0, (byte)0, (byte)0, (byte)0xff
			};
		try 
		{
			ICCProfileTagEntry tag = new ICCProfileTagEntry(ba, 0);
			byte[] ret = tag.toByteArray();
			for (int i=0; i<ret.length; i++) 
			{
				assertEquals("toByteArray, index="+String.valueOf(i),ba[i], ret[i]);
			}
		} 
		catch (ICCProfileException e) 
		{
			assertFalse("should not here", false);
		}
	}

	public void testGetSignature()
	{
	}

	public void testGetOffset()
	{
	}

	public void testSetOffset()
	{
	}

	public void testGetSize()
	{
	}

	public void testSetSize()
	{
	}

	public void testGetData()
	{
	}

	public void testSetData()
	{
	}

	public void testSize()
	{
		ICCProfileTagEntry data = new ICCProfileTagEntry();
		assertEquals(ICCProfileTagEntry.SIZE, data.size());
	}

	public void testToString()
	{
		try
		{
			ICCProfileTagEntry tag = new ICCProfileTagEntry(100, 200, 300);
			String s = tag.toString();
			assertEquals("100,200,300",s);
		}
		catch (ICCProfileException e)
		{
			assertFalse(true);
		}
	}


}
