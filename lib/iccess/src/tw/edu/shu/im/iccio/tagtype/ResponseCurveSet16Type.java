package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * ResponseCurveSet16Type is a tag type for relations of physical colorant amounts with normalized device codes.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The purpose of this tag type is to provide a mechanism to relate physical colorant amounts with the normalized
 * device codes produced by lut8Type, lut16Type, lutAToBType or lutBtoAType tags so that corrections can be
 * made for variation in the device without having to produce a new profile. The mechanism can be used by
 * applications to allow users with relatively inexpensive and readily available instrumentation to apply corrections
 * to individual output colour channels in order to achieve consistent results. (see spec for more)
 * <code>
 * 0..3		4	'rcs2' (72637332h) [response curve set with 2-byte precision] type signature
 * 4..7		4	reserved, must be set to 0
 * 8..9		2	number of channels uInt16Number
 * 10..11	2	count of measurement types uInt16Number
 * 12..m		an array of offsets, each relative to byte 0 of this structure, with one entry for each measurement type. Each will point to the response data for the measurement unit. uInt32Number[...]
 * m+1..n		count response curve structures see Table 51 below
 * </code>
 * 
 */
public class ResponseCurveSet16Type extends AbstractTagType
{
	public static final int SIGNATURE = 0x72637332;
	
	private	Signature	signature_;
	private	UInt16Number	numChannels_;
	private	UInt16Number	measurementTypes_;
	private	UInt32Number[]	offsets_;
	private	CurveStructure[]	curveStructures_;

	public ResponseCurveSet16Type()
	{
		this.signature_ = new Signature(SIGNATURE);	//"rcs2");
	}

	public ResponseCurveSet16Type(byte[] byteArray) throws ICCProfileException
	{
		fromByteArray(byteArray, 0, 0);
	}

	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > byteArray.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x72637332)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);
		
		this.numChannels_ = new UInt16Number(byteArray, offset + 8);
		int channels = this.numChannels_.intValue();
		
		this.measurementTypes_ = new UInt16Number(byteArray, offset + 10);
		int types = this.measurementTypes_.intValue();
		
		this.offsets_ = new UInt32Number[types];
		int idx = offset + 12;
		for (int i=0; i<types; i++)
		{
			this.offsets_[i] = new UInt32Number(byteArray, idx);
			idx += UInt32Number.SIZE;
		}
		
		this.curveStructures_ = new CurveStructure[types];
		for (int i=0; i<types; i++)
		{
			this.curveStructures_[i] = new CurveStructure(byteArray, idx, channels);
			idx += this.curveStructures_[i].size();
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
    if (this.numChannels_==null || this.measurementTypes_==null)
      throw new ICCProfileException("ResponseCurveSet16Type.toByteArray():data not set",ICCProfileException.InvalidDataValueException);

		int types = this.measurementTypes_.intValue();
		int len = 12 + types * UInt32Number.SIZE;
		for (int i=0; i<types; i++)
			len += this.curveStructures_[i].size();
		
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.numChannels_);
		ICCUtils.appendByteArray(all, 10, this.measurementTypes_);
		int idx = 12;
		for (int i=0; i<types; i++)
		{
			ICCUtils.appendByteArray(all, idx, this.offsets_[i]);
			idx += UInt32Number.SIZE;
		}
		for (int i=0; i<types; i++)
		{
			byte[] b = this.curveStructures_[i].toByteArray();
			System.arraycopy(b, 0, all, idx, b.length);
			idx += b.length;
		}
		return all;
	}
	
	public int size()
	{
		assert(this.measurementTypes_!=null);
		int types = this.measurementTypes_.intValue();
		int len = 12 + types * UInt32Number.SIZE;
		for (int i=0; i<types; i++)
			len += this.curveStructures_[i].size();
		return len;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	public UInt16Number getNumChannels()
	{
		return this.numChannels_;
	}
	public void setNumChannels(UInt16Number num)
	{
		this.numChannels_ = num;
	}
	public UInt16Number getMeasurementTypes()
	{
		return this.measurementTypes_;
	}
	public void setMeasurementTypes(UInt16Number num)
	{
		this.measurementTypes_ = num;
	}
	public UInt32Number[] getOffsets()
	{
		return this.offsets_;
	}
	public void setOffsets(UInt32Number[] offsets)
	{
		this.offsets_ = offsets;
	}
	public CurveStructure[] getCurveStructures()
	{
		return this.curveStructures_;
	}
	public void setCurveStructures(CurveStructure[] cs)
	{
		this.curveStructures_ = cs;
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
			sb.append("<responseCurveSet16Type sig=\"rcs2\">");
		else
			sb.append("<responseCurveSet16Type name=\""+name+"\" sig=\"rcs2\">");
		sb.append(signature_.toXmlString());
		sb.append(numChannels_.toXmlString());
		sb.append(measurementTypes_.toXmlString());
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<offsets_.length; i++)
		{
			sb.append(offsets_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<curveStructures_.length; i++)
		{
			sb.append(curveStructures_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("</responseCurveSet16Type>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
