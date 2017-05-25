
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ICCProfileReaderTest extends TestCase
{
	private ICCProfileReader	data_;

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
			String filename = "./data/umx2100u.icm";
			ICCProfileReader reader = new ICCProfileReader(filename);
			assertEquals(filename, reader.getFilename());
			reader.close();
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			ICCProfileReader reader = new ICCProfileReader("nofile");
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.IOException, e.getType());
		}
	}

	public void testGetProfile()
	{
		//ignore
	}

	public void testGetFilename()
	{
		try
		{
			ICCProfileReader reader = new ICCProfileReader("./data/printroom.icc");
			assertEquals("./data/printroom.icc", reader.getFilename());
			reader.close();
		}
		catch (ICCProfileException e)
		{
			assertFalse("error open ./data/printroom.icc",true);
		}
	}

	public void testGetFile()
	{
		try
		{
			ICCProfileReader reader = new ICCProfileReader("./data/printroom.icc");
			assertTrue(reader.getFile()!=null);
			reader.close();
		}
		catch (ICCProfileException e)
		{
			assertFalse("error open ./data/printroom.icc",true);
		}
	}

	public void testClose()
	{
		//ignore
	}

	public void testReadProfileHeader()
	{
		try
		{
			ICCProfileReader reader = new ICCProfileReader("./data/printroom.icc");
			ICCProfileHeader header = reader.readProfileHeader();
			assertTrue("header is null", header != null);
			//TODO: verify header
			reader.close();
		}
		catch (ICCProfileException e)
		{
			assertFalse("error open ./data/printroom.icc",true);
		}
	}

	public void testReadProfileTagTable()
	{
		try
		{
			ICCProfileReader reader = new ICCProfileReader("./data/printroom.icc");
			ICCProfileTagTable table = reader.readProfileTagTable();
			assertTrue("table is null", table != null);
			//TODO: verify tag table
			reader.close();
		}
		catch (ICCProfileException e)
		{
			assertFalse("error open ./data/printroom.icc",true);
		}
	}

	public void testReadProfile()
	{
		//TODO
	}

	public void testReadTagData()
	{
		//TODO
	}
}

