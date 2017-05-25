package write;

import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class DisplayMonoSample
{
	public static final String COPYRIGHT = "Copyright (C) Mei-Chun Lo, 2006.";
	public static final String COPYRIGHTU = "\u7F85\u6885\u541B\u7248\u6B0A\u6240\u6709\uFF0C\u0032\u0030\u0030\u0036\u5E74\u0031\u0032\u6708\u3002";
	public static final String DESCRIPTION = "This is an dummy profile for testing only.";

	public static void main(String[] args)
	{
		String filename = "dummy-mono-display-profile.icc";
		if (args.length > 0)
			filename = args[0];
		try
		{
			ICCProfileHeader header = new ICCProfileHeader();
			header.setDeviceClass(ProfileClass.DISPLAY_DEVICE);
			
			ICCProfileTagTable table = new ICCProfileTagTable();
			
			ICCDisplayProfileMono profile = new ICCDisplayProfileMono(header, table);

			MultiLocalizedUnicodeType copyrightmt = new MultiLocalizedUnicodeType();
			byte[] unitext = ICCUtils.enUnicode(COPYRIGHT);
			copyrightmt.addText("EN","US",unitext);
			//add Chinese description as well
			byte[] uni2 = ICCUtils.enUnicode(COPYRIGHTU);
			copyrightmt.addText("ZH","TW",uni2);
			profile.setMultiTextTag(Tags.copyrightTag, copyrightmt);

			MultiLocalizedUnicodeType pdmt = new MultiLocalizedUnicodeType();
			byte[] uni1 = ICCUtils.enUnicode(DESCRIPTION);
			pdmt.addText("EN","US",uni1);
			profile.setMultiTextTag(Tags.profileDescriptionTag, pdmt);

			//add white point
			double wp_ciex = 0.76;
			double wp_ciey = 0.8;
			double wp_ciez = 0.63;
			profile.setWhitePoint(wp_ciex, wp_ciey, wp_ciez);
			//add chromatic adaptation
			double[] adaptation = new double[]{
				  1.0414,   0.0293,  -0.0525,
				  0.0211,   1.0027,  -0.0209,
				 -0.0011,  -0.0034,   0.7620
			};
			profile.setChromaticAdaptation(adaptation);
			//add kTRC (just mock data for testing)
			UInt16Number[] cvs = new UInt16Number[8];
			for (int i=0; i<cvs.length; i++)
			{
				cvs[i] = new UInt16Number(i);
			}
			CurveType ct = new CurveType();
			ct.setCurveValues(cvs);
			profile.addTagTypeEntry(Tags.grayTRCTag, ct);
			
			ICCFileWriter writer = new ICCFileWriter(filename);
			profile.save(writer);
			writer.close();
		}
		catch (ICCProfileException e)
		{
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}
}
