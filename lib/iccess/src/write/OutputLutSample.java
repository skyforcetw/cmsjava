package write;

import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

/**
 * Required tags include:
				"desc","profileDescriptionTag",
				"wtpt","mediaWhitePointTag",
				"cprt","copyrightTag",
				"chad","chromaticAdaptationTag",
				"A2B0","AToB0Tag",
				"B2A0","BToA0Tag",
				"A2B1","AToB1Tag",
				"B2A1","BToA1Tag",
				"A2B2","AToB2Tag",
				"B2A2","BToA2Tag",
				"gamt","gamutTag"
 */
public class OutputLutSample
{
	public static final String COPYRIGHT = "Copyright (C) Mei-Chun Lo, 2006.";
	public static final String COPYRIGHTU = "\u7F85\u6885\u541B\u7248\u6B0A\u6240\u6709\uFF0C\u0032\u0030\u0030\u0036\u5E74\u0031\u0032\u6708\u3002";
	public static final String DESCRIPTION = "This is an dummy profile for testing only.";

	public static void main(String[] args)
	{
		String filename = "dummy-lut-output-profile.icc";
		if (args.length > 0)
			filename = args[0];
		try
		{
			ICCProfileHeader header = new ICCProfileHeader();
			header.setDeviceClass(ProfileClass.OUTPUT_DEVICE);

			ICCProfileTagTable table = new ICCProfileTagTable();

			ICCOutputProfileLut profile = new ICCOutputProfileLut(header, table);

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
			//now add A2B0 using Lut8Type data
			Lut8Type lut1 = new Lut8Type();
			lut1.setNumInputChannels(new UInt8Number((byte)1));
			lut1.setNumOutputChannels(new UInt8Number((byte)1));
			lut1.setNumInputEntries(new UInt16Number((short)256));
			lut1.setNumOutputEntries(new UInt16Number((short)256));
			lut1.setNumGridPoints(new UInt8Number((byte)16));
			lut1.setE00(new S15Fixed16Number(0));
			lut1.setE01(new S15Fixed16Number(0));
			lut1.setE02(new S15Fixed16Number(0));
			lut1.setE10(new S15Fixed16Number(0));
			lut1.setE11(new S15Fixed16Number(0));
			lut1.setE12(new S15Fixed16Number(0));
			lut1.setE20(new S15Fixed16Number(0));
			lut1.setE21(new S15Fixed16Number(0));
			lut1.setE22(new S15Fixed16Number(0));
			byte[] itabvals = new byte[256];
			for (int i=0; i<256; i++)
				itabvals[i] = (byte)i;
			UInt8ArrayType itab = new UInt8ArrayType();
			itab.addNumbers(itabvals);
			lut1.setInputTables(itab.getArray());
			lut1.setOutputTables(itab.getArray());
			int cluts = (int)Math.pow(16, 1) * 256;
			byte[] clutvals = new byte[cluts];
			UInt8ArrayType clut = new UInt8ArrayType();
			clut.addNumbers(clutvals);
			lut1.setClutValues(clut.getArray());
			profile.addTagTypeEntry(Tags.AToB0Tag, lut1);
			//clone lut1 for B2A0,B2A0,A2B1,B2A1,A2B2,B2A2
			profile.addTagTypeEntry(Tags.BToA0Tag, lut1);
			profile.addTagTypeEntry(Tags.AToB1Tag, lut1);
			profile.addTagTypeEntry(Tags.BToA1Tag, lut1);
			profile.addTagTypeEntry(Tags.AToB2Tag, lut1);
			profile.addTagTypeEntry(Tags.BToA2Tag, lut1);
			//add gamutTag using the lut1 as well
			profile.addTagTypeEntry(Tags.gamutTag, lut1);

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
