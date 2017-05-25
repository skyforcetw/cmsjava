
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ICCProfileTest extends TestCase
{
	private ICCProfile	data_;

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
		ICCProfile empty = new ICCProfile();
		try
		{
			ICCProfile copy = new ICCProfile(empty);

			ICCProfileHeader header = new ICCProfileHeader();
			ICCProfileTagTable tagtable = new ICCProfileTagTable();
			ICCProfile profile = new ICCProfile(header, tagtable);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testGetters()
	{
		ICCProfileHeader header = new ICCProfileHeader();
		ICCProfileTagTable tagtable = new ICCProfileTagTable();
		ICCProfile inst1 = new ICCProfile(header, tagtable);
		assertEquals(header, inst1.getHeader());
		assertEquals(tagtable, inst1.getTagTable());
	}

	public void testSetters()
	{
		ICCProfile inst1 = new ICCProfile();
		ICCProfileHeader header = new ICCProfileHeader();
		ICCProfileTagTable tagtable = new ICCProfileTagTable();
		inst1.setHeader(header);
		inst1.setTagTable(tagtable);
		assertEquals(header, inst1.getHeader());
		assertEquals(tagtable, inst1.getTagTable());
	}

	public void testSave()
	{
		//TODO: load data chunks with java.io.InputStream and assign to ICCProfile and save
	}

	public void testValidate()
	{
		//TODO:
	}
}
