package clone;

import tw.edu.shu.im.iccio.ICCProfile;
import tw.edu.shu.im.iccio.ICCProfileReader;
import tw.edu.shu.im.iccio.ICCFileWriter;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * Sample application to read in an ICC profile and save as another file.
 * It can be used to test the library to see whether all tagged data are reproduced correctly.
 */
public class ICCProfileClone
{
	public static void main(String[] args)
	{
		if (args.length < 2)
		{
			System.err.println("Usage: clone.ICCProfileClone <in_filename> <out_filename>");
			return;
		}

		try
		{
			ICCProfile profile = ICCProfileReader.loadProfile(args[0]);
			ICCFileWriter writer = new ICCFileWriter(args[1]);
			profile.save(writer);
			writer.close();
		}
		catch (ICCProfileException e)
		{
			System.err.println(e.getMessage());
		}
	}
}
