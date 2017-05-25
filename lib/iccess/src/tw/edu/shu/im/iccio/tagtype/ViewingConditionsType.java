package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.XYZNumber;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * ViewingConditionsType is a tag type for a set of viewing condition parameters.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * This type represents a set of viewing condition parameters including: CIE 'absolute' illuminant white point
 * tristimulus values and CIE 'absolute' surround tristimulus values.
 * 
 * BytePosition	Length(bytes)	Content					Encoded as...
 * 0..3				4			'view' (76696577h) type signature
 * 4..7				4			reserved, must be set to 0
 * 8..19			12			CIE 'absolute' XYZ values for illuminant (in which Y is in cd/m2)	XYZNumber
 * 20..31			12			CIE 'absolute' XYZ values for surround (in which Y is in cd/m2)		XYZNumber
 * 32..35			4			illuminant type			as described in measurementType
 */
public class ViewingConditionsType extends AbstractTagType
{
	public static final int SIGNATURE = 0x76696577;
	
	private	Signature		signature_;
	private	XYZNumber		illuminantXYZ_;
	private	XYZNumber		surroundXYZ_;
	private	UInt32Number	illuminantType_;

	public ViewingConditionsType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"view");
	}

	public ViewingConditionsType(byte[] byteArray) throws ICCProfileException
	{
		fromByteArray(byteArray, 0, 0);
	}

	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > byteArray.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		if (byteArray.length - offset < 36)
			throw new ICCProfileException("byte array not enough for ViewingConditionsType",ICCProfileException.WrongSizeException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x76696577)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.illuminantXYZ_ = new XYZNumber(byteArray, offset + 8);
		this.surroundXYZ_ = new XYZNumber(byteArray, offset + 20);
		this.illuminantType_ = new UInt32Number(byteArray, offset + 32);
	}

	public byte[] toByteArray() throws ICCProfileException
	{
    if (this.illuminantXYZ_==null)
      throw new ICCProfileException("ViewingConditionsType.toByteArray():data not set",
        ICCProfileException.InvalidDataValueException);
        
		int len = 36;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.illuminantXYZ_);
		ICCUtils.appendByteArray(all, 20, this.surroundXYZ_);
		ICCUtils.appendByteArray(all, 32, this.illuminantType_);

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
	public XYZNumber getIlluminantXYZ()
	{
		return this.illuminantXYZ_;
	}

	public void setIlluminantXYZ(XYZNumber xyznumber)
	{
		this.illuminantXYZ_ = xyznumber;
	}

	public XYZNumber getSurroundXYZ()
	{
		return this.surroundXYZ_;
	}

	public void setSurroundXYZ(XYZNumber xyznumber)
	{
		this.surroundXYZ_ = xyznumber;
	}

	public UInt32Number getIlluminantType()
	{
		return this.illuminantType_;
	}

	public void setIlluminantType(int n)
	{
		this.illuminantType_ = new UInt32Number(n);
	}

	public void setIlluminantType(UInt32Number n)
	{
		this.illuminantType_ = n;
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
			sb.append("<viewingConditionsType sig=\"view\">");
		else
			sb.append("<viewingConditionsType name=\""+name+"\" sig=\"view\">");
		sb.append(signature_.toXmlString());
		sb.append(illuminantXYZ_.toXmlString());
		sb.append(surroundXYZ_.toXmlString());
		sb.append(illuminantType_.toXmlString());
		sb.append("</viewingConditionsType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
