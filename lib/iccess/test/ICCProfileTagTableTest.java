
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ICCProfileTagTableTest extends TestCase
{
	private ICCProfileTagTable	data_;

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
			ICCProfileTagTable table = new ICCProfileTagTable();
			assertEquals(0, table.size());
			assertEquals(0, table.getTagCount());

			byte[] ba = new byte[]
				{
					(byte)0x01,(byte)2, (byte)3, (byte)4, 
					(byte)0x12, (byte)0x34, (byte)0, (byte)0,
					(byte)0, (byte)0, (byte)0, (byte)0xff,
					(byte)0,(byte)0,(byte)1,(byte)0x20,
					(byte)0,(byte)0,(byte)0,(byte)12,
					(byte)0,(byte)0,(byte)1,(byte)0,
					(byte)0,(byte)0,(byte)2,(byte)0x40,
					(byte)0,(byte)0,(byte)0,(byte)24,
					(byte)0,(byte)0,(byte)2,(byte)0
				};
			ICCProfileTagTable table2 = new ICCProfileTagTable(ba);
			assertEquals(3, table2.getTagCount());
		}
		catch (ICCProfileException e)
		{
			assertFalse("Error occurred:"+e.getMessage(),true);
		}
		try 
		{
			byte[] b = new byte[5];
			ICCProfileTagTable failtag = new ICCProfileTagTable(b);
			assertFalse("should not come here", true);
		} 
		catch (ICCProfileException e) 
		{
			assertTrue("should throw exception", true);
		}
	}

	public void testParseByteArray()
	{
		byte[] ba = new byte[]
			{
				(byte)0x01,(byte)2, (byte)3, (byte)4, 
				(byte)0x12, (byte)0x34, (byte)0, (byte)0,
				(byte)0, (byte)0, (byte)0, (byte)0xff,
				(byte)0,(byte)0,(byte)1,(byte)0x20,
				(byte)0,(byte)0,(byte)0,(byte)12,
				(byte)0,(byte)0,(byte)1,(byte)0,
				(byte)0,(byte)0,(byte)2,(byte)0x40,
				(byte)0,(byte)0,(byte)0,(byte)24,
				(byte)0,(byte)0,(byte)2,(byte)0
			};
		ICCProfileTagTable table = new ICCProfileTagTable();
		try 
		{
			table.fromByteArray(ba, 0, 0);
			assertEquals(3, table.getTagCount());
			ICCProfileTagEntry tag = table.getTag(0);
			assertEquals(0x01020304, tag.getSignature().intValue());
			assertEquals(0x12340000, tag.getOffset());
			assertEquals(255, tag.getSize());
			tag = table.getTag(1);
			assertEquals(0x120, tag.getSignature().intValue());
			assertEquals(12,tag.getOffset());
			assertEquals(0x100, tag.getSize());
			tag = table.getTag(2);
			assertEquals(0x240, tag.getSignature().intValue());
			assertEquals(24,tag.getOffset());
			assertEquals(0x200, tag.getSize());
		} 
		catch (ICCProfileException e)
		{
			//System.err.println(e.getMessage());
			assertFalse("should not come here", true);
		}
	}

	public void testSave()
	{
		//TODO: 
	}

	public void testComputeAddress()
	{
		//TODO: difficult to make
	}

	public void testGetTagCount()
	{
		//ignore
	}

	public void testAddTag()
	{
		//ignore
	}

	public void testClear()
	{
		ICCProfileTagTable table = new ICCProfileTagTable();
		try
		{
			table.addTag(120,12,10);
			table.addTag(240,24,20);
			assertEquals(2, table.getTagCount());
			table.clear();
			assertEquals(0, table.getTagCount());
		}
		catch (ICCProfileException e)
		{
			assertFalse(true);
		}
	}

	public void testSize()
	{
		ICCProfileTagTable data = new ICCProfileTagTable();
		assertEquals(0, data.size());
	}

	public void testGetTag()
	{
		ICCProfileTagTable table = new ICCProfileTagTable();
		try
		{
			table.addTag(120,12,10);
			table.addTag(240,24,20);
			ICCProfileTagEntry tag = table.getTag(0);
			assertEquals(120, tag.getSignature().intValue());
			tag = table.getTag(1);
			assertEquals(240, tag.getSignature().intValue());
			tag = table.getTag(2);
			if (tag != null) 
			{
				assertFalse("getTag not out of bound", true);
			}
		}
		catch (ICCProfileException e)
		{
			assertFalse(true);
		}
	}

	public void testToString()
	{
		ICCProfileTagTable table = new ICCProfileTagTable();
		try
		{
			table.addTag(120,12,10);
			table.addTag(240,24,20);
			String s0 = "2\r\n120,12,10\r\n240,24,20\r\n";
			String s1 = table.toString();
			assertEquals(s0,s1);
		}
		catch (ICCProfileException e)
		{
			assertFalse(true);
		}
	}


}
