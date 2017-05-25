package tagtype;

import tw.edu.shu.im.iccio.ICCProfileReader;
import tw.edu.shu.im.iccio.ICCProfileTagTable;
import tw.edu.shu.im.iccio.ICCProfileTagEntry;
import tw.edu.shu.im.iccio.ICCProfileException;
import tw.edu.shu.im.iccio.datatype.Signature;

import java.io.UnsupportedEncodingException;

/**
 * This application displays the tag signatures of all tagged data chunks in a ICC profile file.
 */
public class ICCProfileTagTypeDump
{
	public static void main(String[] args)
	{
		if (args.length < 1)
		{
			System.err.println("Usage: tagtable.ICCProfileTagTypeDump <filename>");
			return;
		}
		ICCProfileReader reader = null;
		try
		{
			reader = new ICCProfileReader(args[0]);
			ICCProfileTagTable table = reader.readProfileTagTable();
			byte[] signature = new byte[4];
			System.out.println("File:"+args[0]);
			System.out.println("   #    Tag   Tag-Type   Offset       Size     Padded");
			System.out.println("------------------------------------------------------");
			for (int i=0; i<table.getTagCount(); i++)
			{
				ICCProfileTagEntry entry = table.getTag(i);
				Signature sig = entry.getSignature();
				int offset = entry.getOffset();
				int size = entry.getSize();
				int padsize = size;
				int x = padsize % 4;
				if (x > 0)
					padsize += 4-x;
				try
				{
					byte[] data = reader.readTagData(entry);
					System.arraycopy(data, 0, signature, 0, 4);
				}
				catch (ICCProfileException ie)
				{
					System.err.println(ie.getMessage());
					continue;
				}
				String s = new String(signature, "ISO-8859-1");
				System.out.format("%4d:   %s    %s %10d %10d %10d\n",i+1,sig.getSignature(),s,offset,size,padsize);
			}
		}
		catch (ICCProfileException e)
		{
			System.err.println(e.getMessage());
		}
		catch (UnsupportedEncodingException x)
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
