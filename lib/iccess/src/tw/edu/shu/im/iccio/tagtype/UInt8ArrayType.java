package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt8Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * UInt8ArrayType is a tag type for array of UInt8Number data.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * This type represents an array of generic 1-byte/8-bit quantity. The number of values is determined from the size
 * of the tag.
 * 
 * 0..3		4	'ui08' (75693038h) type signature
 * 4..7		4	reserved, must be set to 0
 * 8..end		an array of unsigned 8-bit integers
 */
public class UInt8ArrayType extends AbstractTagType
{
	public static final int SIGNATURE = 0x75693038;
	
	private	Signature		signature_;
	private	UInt8Number[]	array_;

	public UInt8ArrayType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"ui08");
	}

	public UInt8ArrayType(byte[] byteArray) throws ICCProfileException
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
		if (this.signature_.intValue() != 0x75693038)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		if (len <= 0)
			len = (byteArray.length - offset - 8) / UInt8Number.SIZE;
		if (len <= 0)
			throw new ICCProfileException("array empty", ICCProfileException.WrongSizeException);

		this.array_ = new UInt8Number[len];
		int idx = offset + 8;
		for (int i=0; i<len; i++)
		{
			this.array_[i] = new UInt8Number(byteArray, idx);
			idx += UInt8Number.SIZE;
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.array_==null)
			throw new ICCProfileException("array not set",ICCProfileException.InvalidDataValueException);

		int len = 8 + this.array_.length * UInt8Number.SIZE;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		int idx = 8;
		for (int i=0; i<this.array_.length; i++)
		{
			all[idx++] = this.array_[i].byteValue();
		}

		return all;
	}
	
	public int size()
	{
		assert(this.array_!=null);
		return 8 + this.array_.length * UInt8Number.SIZE;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	public int getArraySize()
	{
		return this.array_.length;
	}

	public UInt8Number[] getArray()
	{
		return this.array_;
	}

	public void addNumber(UInt8Number val)
	{
		if (this.array_==null)
			this.array_ = new UInt8Number[1];
		else
		{
			UInt8Number[] bac = this.array_;
			this.array_ = new UInt8Number[bac.length+1];
			for (int i=0; i<bac.length; i++)
				this.array_[i] = bac[i];
		}
		this.array_[array_.length-1] = val;
	}

	/**
	 * Add an array of byte values to the byte array type.
	 */
	public void addNumbers(byte[] vals) throws ICCProfileException
	{
		int len = vals.length;
		int idx = 0;
		if (this.array_==null)
			this.array_ = new UInt8Number[len];
		else
		{
			UInt8Number[] bac = this.array_;
			idx = bac.length;
			this.array_ = new UInt8Number[bac.length+len];
			for (int i=0; i<bac.length; i++)
				this.array_[i] = bac[i];
		}
		for (int i=0; i<len; i++)
			this.array_[idx++] = new UInt8Number(vals[i]);
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
			sb.append("<uInt8ArrayType sig=\"ui08\">");
		else
			sb.append("<uInt8ArrayType name=\""+name+"\" sig=\"ui08\">");
		sb.append(signature_.toXmlString());
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<array_.length; i++)
		{
			//sb.append(array_[i].toXmlString());
			if (i > 0) sb.append(", ");
			sb.append(array_[i]);
		}
		sb.append("</dim></array>");
		sb.append("</uInt8ArrayType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
