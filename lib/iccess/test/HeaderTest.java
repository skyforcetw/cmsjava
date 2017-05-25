import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class HeaderTest extends TestCase
{
	public void testReadHeader()
	{
		try
		{
			ICCProfileReader reader = new ICCProfileReader("./data/printroom.icc");
			ICCProfileHeader header = reader.readProfileHeader();
			assertTrue("Erro: Header is null", header!=null);
			assertEquals("The filename is different","./data/printroom.icc",reader.getFilename());
			assertEquals("the profile size is not 802008", 802008, header.getProfileSize());
			reader.close();
		}
		catch (ICCProfileException e)
		{
			System.err.println(e.getMessage());
		}
	}
}
