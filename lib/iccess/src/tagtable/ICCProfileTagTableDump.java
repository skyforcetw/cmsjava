package tagtable;

import tw.edu.shu.im.iccio.ICCProfileReader;
import tw.edu.shu.im.iccio.ICCProfileTagTable;
import tw.edu.shu.im.iccio.ICCProfileTagEntry;
import tw.edu.shu.im.iccio.ICCProfileException;
import tw.edu.shu.im.iccio.datatype.Signature;

/**
 * This small utility displays the tag table entries in an ICC profile file.
 * It uses the System.out.format method in Java 1.5.
 */
public class ICCProfileTagTableDump
{
	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.out.println("Usage: java -cp ./ tagtable.ICCProfileTagTableDump <icc_profile_filename>");
		}
		else
		{
			ICCProfileReader reader = null;
			try
			{
				reader = new ICCProfileReader(args[0]);
				ICCProfileTagTable table = reader.readProfileTagTable();
				System.out.println("   #   Tag       [Hex]     Offset       Size");
				System.out.println("--------------------------------------------");
				for (int i=0; i<table.getTagCount(); i++)
				{
					ICCProfileTagEntry entry = table.getTag(i);
					Signature sig = entry.getSignature();
					System.out.format("%4d: %5s [%x] %10d %10d\n",i+1,sig.getSignature(),sig.intValue(),entry.getOffset(),entry.getSize());
				}
			}
			catch (ICCProfileException x)
			{
				System.err.println(x.getMessage());
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
}
