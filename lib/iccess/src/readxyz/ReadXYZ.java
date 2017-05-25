package readxyz;

import tw.edu.shu.im.iccio.ICCProfileReader;
import tw.edu.shu.im.iccio.ICCProfileException;
import tw.edu.shu.im.iccio.datatype.XYZNumber;
import tw.edu.shu.im.iccio.tagtype.XYZType;
import tw.edu.shu.im.iccio.ICCProfileTagTable;
import tw.edu.shu.im.iccio.ICCProfileTagEntry;
import tw.edu.shu.im.iccio.datatype.Signature;

/**
 * This program will list all XYZ tags with data values in a given ICC profile.
 */
public class ReadXYZ
{
	public static void main(String[] args)
	{
		if (args.length<1)
		{
			System.out.println("ReadXYZ <ICC filename>");
			return;
		}
		String filename = args[0];
		System.out.println("Reading data from "+filename);
		
		try
		{
			ICCProfileReader reader = new ICCProfileReader(filename);
			ICCProfileTagTable table = reader.readProfileTagTable();
			for (int i=0; i<table.getTagCount(); i++)
			{
				ICCProfileTagEntry entry = table.getTag(i);
				Signature sig = entry.getSignature();
				byte[] tagdata = reader.readTagData(entry);
				if (tagdata[0]==88 && tagdata[1]==89 && tagdata[2]==90)
				{
					//right, we got a XYZ tag here
					XYZType xyzs = new XYZType(tagdata);
					XYZNumber[] xyzns = xyzs.getXYZNumbers();
					for (int k=0; k<xyzns.length; k++)
					{
						System.out.format("Tag %d [%s] XYZ: %s\n", i+1, sig.getSignature(), xyzns[k].toString());
					}
				}
			}
			reader.close();
		}
		catch (ICCProfileException e)
		{
			System.err.println(e.getMessage());
		}
	}
}
