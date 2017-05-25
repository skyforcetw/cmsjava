package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.ICCProfileException;
import tw.edu.shu.im.iccio.datatype.UInt16Number;

/**
 * ColorantCode is used by ChromoticityType to define colorant name and values.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * This is not a specified tag type, but an internal type for Colorant and phosphor encoding.
 * It is used by the ChromoticityType.
 * 
 * Type				Encoded	Value	Channel 1 x,y	Channel 2 x,y	Channel 3 x,y
 * unknown			0000h			any				any				any
 * ITU-R BT.709		0001h			(0,640, 0,330)	(0,300, 0,600)	(0,150, 0,060)
 * SMPTE RP145-1994 0002h			(0,630, 0,340)	(0,310, 0,595)	(0,155, 0,070)
 * EBU Tech.3213-E	0003h			(0,64 0,33)		(0,29, 0,60)	(0,15, 0,06)
 * P22				0004h			(0,625, 0,340)	(0,280, 0,605)	(0,155, 0,070)
 */
public class ColorantCode extends UInt16Number
{
	private class Code
	{
		public short code_;
		public String typeName_;
		public double x1_, y1_;    //channel 1
		public double x2_, y2_;   //channel 2
		public double x3_, y3_;   //channel 3
		public Code(short code, String name, double x1, double y1, double x2, double y2, double x3, double y3)
		{
			this.code_ = code;
			this.typeName_ = name;
			this.x1_ = x1;
			this.y1_ = y1;
			this.x2_ = x2;
			this.y2_ = y2;
			this.x3_ = x3;
			this.y3_ = y3;
		}
	}
	
	private Code[] codes_ = new Code[]
	{
		new Code((short)0,"unknown",0.,0.,0.,0.,0.,0.),
		new Code((short)1,"ITU-R BT.709",0.64,0.33,0.3,0.6,0.15,0.6),
		new Code((short)2,"SMPTE RP145-1994",0.63,0.34,0.31,0.595,0.155,0.07),
		new Code((short)3,"EBU Tech.3213-E",0.64,0.33,0.29,0.6,0.15,0.06),
		new Code((short)4,"P22",0.625,0.34,0.28,0.605,0.155,0.07)
	};
	
	public ColorantCode()
	{
	}

	public ColorantCode(int code)
	{
		super(code);
	}

	public ColorantCode(byte[] byteArray) throws ICCProfileException
	{
		super(byteArray);
	}

	public ColorantCode(byte[] byteArray, int offset) throws ICCProfileException
	{
		super(byteArray, offset);
	}

	public String getName()
	{
		int code = intValue();
		if (code < codes_.length)
			return codes_[code].typeName_;
		return null;
	}
	
	public double getX1()
	{
		int code = intValue();
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].x1_;
	}
	public double getX2()
	{
		int code = intValue();
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].x2_;
	}
	public double getX3()
	{
		int code = intValue();
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].x3_;
	}
	public double getY1()
	{
		int code = intValue();
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].y1_;
	}
	public double getY2()
	{
		int code = intValue();
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].y2_;
	}
	public double getY3()
	{
		int code = intValue();
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].y3_;
	}
/*
	public static String getCodeName(int code)
	{
		if (code < 0 || code >= codes_.length)
			return null;
		return this.codes_[code].typeName_;
	}

	public static double getCodeX1(int code)
	{
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].x1_;
	}
	public static double getCodeX2(int code)
	{
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].x2_;
	}
	public static double getCodeX3(int code)
	{
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].x3_;
	}
	public static double getCodeY1(int code)
	{
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].y1_;
	}
	public static double getCodeY2(int code)
	{
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].y2_;
	}
	public static double getCodeY3(int code)
	{
		if (code < 0 || code >= this.codes_.length)
			return -1.0;
		return this.codes_[code].y3_;
	}
*/


}
