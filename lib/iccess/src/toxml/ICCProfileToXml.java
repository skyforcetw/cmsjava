package toxml;

import tw.edu.shu.im.iccio.ICCProfile;
import tw.edu.shu.im.iccio.ICCProfileReader;
import tw.edu.shu.im.iccio.ICCProfileException;

import java.io.FileWriter;
import java.io.IOException;

/**
 * This sample is a utility to save a ICC profile into an XML file.
 */
public class ICCProfileToXml
{
	public static void main(String[] args)
	{
		if (args.length < 2)
		{
			System.err.println("Usage: toxml.ICCProfileToXml <filename> <xmlfilename>");
			return;
		}

		try
		{
			ICCProfile profile = ICCProfileReader.loadProfile(args[0]);
			String xml = profile.toXmlString(args[0]);
			FileWriter pw = new FileWriter(args[1]);
			pw.write(xml);
			pw.close();
		}
		catch (ICCProfileException e)
		{
			System.err.println(e.getMessage());
		}
		catch (IOException x)
		{
			System.err.println(x.getMessage());
		}
	}
}
