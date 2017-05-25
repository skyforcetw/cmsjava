package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.XYZNumber;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * XYZType is a tag type for array of XYZNumber type of data.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The XYZType contains an array of three encoded values for the XYZ tristimulus values. The number of sets of
 * values is determined from the size of the tag.
 * 
 * BytePosition	Length(bytes)	Content								Encoded as...
 * 0..3				4			'XYZ ' (58595A20h) type signature
 * 4..7				4			reserved, must be set to 0
 * 8..end						an array of XYZ numbers				XYZNumber
 */
public class XYZType extends AbstractTagType
{
	public static final int SIGNATURE = 0x58595A20;
	
	private	Signature		signature_;
	private	XYZNumber[]		xyzValues_;

	public XYZType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"XYZ ");
	}

	public XYZType(byte[] byteArray) throws ICCProfileException
	{
		fromByteArray(byteArray, 0, 0);
	}

	/**
	 * Parse a byte array to make a XYZType data object.
	 * If the len parameter is zero, the number of XYZ numbers are calculated from the actual bytes in the byte array.
	 * @param byteArray - byte array containing the bytes to form a XYZType object.
	 * @param offset - starting position in the array for the data object.
	 * @param len - number of XYZNumber items, not bytes, or zero for available bytes in array.
	 */
	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > byteArray.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x58595A20)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		if (len <= 0)
			len = (int) (byteArray.length - offset - 8) / XYZNumber.SIZE;
		if (len <= 0)
			throw new ICCProfileException("byte array empty", ICCProfileException.WrongSizeException);

		xyzValues_ = new XYZNumber[len];
		int idx = offset + 8;
		for (int i=0; i<len; i++)
		{
			xyzValues_[i] = new XYZNumber(byteArray, idx);
			idx += XYZNumber.SIZE;
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
    if (this.xyzValues_ == null)
      throw new ICCProfileException("XYZType.toByteArray():data not set",
        ICCProfileException.InvalidDataValueException);
        
		int len = 8 + this.xyzValues_.length * XYZNumber.SIZE;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		int idx = 8;
		for (int i=0; i<this.xyzValues_.length; i++)
		{
			ICCUtils.appendByteArray(all, idx, this.xyzValues_[i]);
			idx += XYZNumber.SIZE;
		}

		return all;
	}
	
	public int size()
	{
		assert(this.xyzValues_!=null);
		return 8 + this.xyzValues_.length * XYZNumber.SIZE;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	public int getXYZCount()
	{
		return this.xyzValues_.length;
	}

	public XYZNumber[] getXYZNumbers()
	{
		return this.xyzValues_;
	}

	public void addXYZNumber(XYZNumber xyz)
	{
		if (this.xyzValues_==null)
			this.xyzValues_ = new XYZNumber[1];
		else
		{
			XYZNumber[] bac = this.xyzValues_;
			this.xyzValues_ = new XYZNumber[bac.length+1];
			for (int i=0; i<bac.length; i++)
				this.xyzValues_[i] = bac[i];
		}
		this.xyzValues_[xyzValues_.length-1] = xyz;
	}
	
	/**
	 * Add an array of byte values to the byte array type.
	 */
	public void addNumbers(double[][] vals) throws ICCProfileException
	{
		int len = vals.length;
		if (len <= 0)
			return;
		int idx = 0;
		if (this.xyzValues_==null)
			this.xyzValues_ = new XYZNumber[len];
		else
		{
			XYZNumber[] bac = this.xyzValues_;
			idx = bac.length;
			this.xyzValues_ = new XYZNumber[bac.length+len];
			for (int i=0; i<bac.length; i++)
				this.xyzValues_[i] = bac[i];
		}
		for (int i=0; i<len; i++)
			this.xyzValues_[idx++] = new XYZNumber(vals[i][0],vals[i][1],vals[i][2]);
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
			sb.append("<XYZType sig=\"XYZ \">");
		else
			sb.append("<XYZType name=\""+name+"\" sig=\"XYZ \">");
		sb.append(signature_.toXmlString());
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<xyzValues_.length; i++)
		{
			sb.append(xyzValues_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("</XYZType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
