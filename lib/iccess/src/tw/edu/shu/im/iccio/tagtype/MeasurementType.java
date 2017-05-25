package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.datatype.XYZNumber;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * MeasurementType.java
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The measurementType information refers only to the internal profile data and is meant to provide profile
 * makers an alternative to the default measurement specifications.
 * 
 * 0..3		4	'meas' (6D656173h) type signature
 * 4..7		4	reserved, must be set to 0
 * 8..11	4	encoded value for standard observer see Table 40 below
 * 12..23	12	XYZ tristimulus values for measurement backing XYZNumber
 * 24..27	4	encoded value for measurement geometry see Table 41 below
 * 28..31	4	encoded value for measurement flare see Table 42 below
 * 32..35	4	encoded value for standard illuminant see Table 43 below
 */
public class MeasurementType extends AbstractTagType
{
	public static final int SIGNATURE = 0x6D656173;
	
	private	Signature	signature_;		//0
	private	UInt32Number	observerCode_;		//8	coded
	private	XYZNumber	xyzValues_;		//12
	private	UInt32Number	measureGeometry_;	//24	coded
	private	UInt32Number	measureFlare_;		//28	coded
	private	UInt32Number	standardIlluminant_;	//32	coded

	public MeasurementType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"meas");
	}

	public MeasurementType(byte[] byteArray) throws ICCProfileException
	{
		fromByteArray(byteArray, 0, 0);
	}
	
	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > byteArray.length || offset+36 > byteArray.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x6D656173)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.observerCode_ = new UInt32Number(byteArray, offset + 8);
		this.xyzValues_ = new XYZNumber(byteArray, offset + 12);
		this.measureGeometry_ = new UInt32Number(byteArray, offset + 24);
		this.measureFlare_ = new UInt32Number(byteArray, offset + 28);
		this.standardIlluminant_ = new UInt32Number(byteArray, offset + 32);
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.observerCode_ == null)
			throw new ICCProfileException("data not set", ICCProfileException.InvalidDataValueException);
		
		int len = 36;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.observerCode_);
		ICCUtils.appendByteArray(all, 12, this.xyzValues_);
		ICCUtils.appendByteArray(all, 24, this.measureGeometry_);
		ICCUtils.appendByteArray(all, 28, this.measureFlare_);
		ICCUtils.appendByteArray(all, 32, this.standardIlluminant_);

		return all;
	}

	public int size()
	{
		return 36;
	}
	
	public Signature getSignature()
	{
		return this.signature_;
	}
	public UInt32Number getObserverCode()
	{
		return this.observerCode_;
	}
	public XYZNumber getXYZValues()
	{
		return this.xyzValues_;
	}
	public UInt32Number getMeasureGeometryCode()
	{
		return this.measureGeometry_;
	}
	public UInt32Number getMeasureFlareCode()
	{
		return this.measureFlare_;
	}
	public UInt32Number getStandardIlluminantCode()
	{
		return this.standardIlluminant_;
	}
	
	public void setObserverCode(int code)
	{
		this.observerCode_ = new UInt32Number(code);
	}
	public void setXYZValues(double x, double y, double z) throws ICCProfileException
	{
		this.xyzValues_ = new XYZNumber(x,y,z);
	}
	public void setMeasureGeometryCode(int code)
	{
		this.measureGeometry_ = new UInt32Number(code);
	}
	public void setMeasureFlareCode(int code)
	{
		this.measureFlare_ = new UInt32Number(code);
	}
	public void setStandardIlluminantCode(int code)
	{
		this.standardIlluminant_ = new UInt32Number(code);
	}



	/**
	 * Return XML element of this object.
	 * @param name - attribute name on element
	 * @return XML fragment as a string
	 */
	public String toXmlString(String name)
	{
		StringBuffer sb = new StringBuffer();
		if (name==null || name.length()<1)
			sb.append("<measurementType sig=\"meas\">");
		else
			sb.append("<measurementType name=\""+name+"\" sig=\"meas\">");
		sb.append(signature_.toXmlString());
		sb.append(observerCode_.toXmlString());
		sb.append(xyzValues_.toXmlString());
		sb.append(measureGeometry_.toXmlString());
		sb.append(measureFlare_.toXmlString());
		sb.append(standardIlluminant_.toXmlString());
		sb.append("</measurementType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
