package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.DateTimeNumber;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

import java.util.Calendar;

/**
 * DateTimeType is a tag type with a date time number.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 *
 * This dateTimeType is a 12-byte value representation of the time and date.
 *
 * 0..3 	4 	'dtim' (6474696Dh) type signature
 * 4..7 	4 	reserved, must be set to 0
 * 8..19 	12 	date and time dateTimeNumber
 */
public class DateTimeType extends AbstractTagType
{
	public static final int SIGNATURE = 0x6474696D;
	
	private	Signature	signature_;
	private	DateTimeNumber	dateTime_;

	public DateTimeType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"dtim");
	}

	public DateTimeType(DateTimeNumber dt)
	{
		setDateTime(dt);
	}
	
	public DateTimeType(Calendar d)
	{
		setDateTime(d);
	}
	
	public DateTimeType(byte[] byteArray) throws ICCProfileException
	{
		fromByteArray(byteArray, 0, 0);
	}

	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > byteArray.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);
		
		if (byteArray.length - offset < 20)
			throw new ICCProfileException("DateTimeType incorrect size", ICCProfileException.WrongSizeException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x6474696D)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.dateTime_ = new DateTimeNumber(byteArray, offset + 8);
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.dateTime_ == null)
			throw new ICCProfileException("data not set", ICCProfileException.InvalidDataValueException);
		
		byte[] all = new byte[20];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.dateTime_);

		return all;
	}
	
	public int size()
	{
		return 20;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}

	public DateTimeNumber getDateTime()
	{
		return this.dateTime_;
	}
	
	public void setDateTime(DateTimeNumber dt)
	{
		this.dateTime_ = dt;
	}
	
	public void setDateTime(Calendar d)
	{
		this.dateTime_ = new DateTimeNumber(d);
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
			sb.append("<dateTimeType sig=\"dtim\">");
		else
			sb.append("<dateTimeType name=\""+name+"\" sig=\"dtim\">");
		sb.append(signature_.toXmlString());
		sb.append(dateTime_.toXmlString());
		sb.append("</dateTimeType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
