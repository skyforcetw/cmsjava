package header;

import tw.edu.shu.im.iccio.ICCProfileReader;
import tw.edu.shu.im.iccio.ICCProfileHeader;
import tw.edu.shu.im.iccio.ICCProfileException;

import java.io.IOException;
import java.util.logging.*;

/**
 * This is a small application to display the header information from an ICC Profile file.
 * Usage:
 *		java -cp ./ header.ICCProfileHeaderDump <filename>
 * To use it from Ant script:
 *		ant dump-header -Dfilename=<filename>
 */
public class ICCProfileHeaderDump
{
	private static Logger logger = Logger.getLogger(ICCProfileHeaderDump.class.getName());
	
	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.out.println("Usage: java -cp ./ header.ICCProfileHeaderDump <iccprofile_filename>");
			return;
		}
		ICCProfileReader reader = null;
		try
		{
			try 
			{
				FileHandler fh  = new FileHandler("mylog.txt");
				logger.addHandler(fh);
				logger.setLevel(Level.ALL);
			} 
			catch (IOException e)
			{
				System.out.println("FileHandler(mylog.txt) failed");
			}
			reader = new ICCProfileReader(args[0]);
			ICCProfileHeader header = reader.readProfileHeader();
			header.dump();
		}
		catch (ICCProfileException e)
		{
			System.err.println(e.getMessage());
		}
		finally
		{
			if (reader != null)
				try
				{
					reader.close();
				}
				catch (Exception e)
				{
				}
		}
	}

}
