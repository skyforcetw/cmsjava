package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * UInt16ArrayType is a tag type for array of UInt16Number type of data.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * This type represents an array of generic 2-byte/16-bit quantity. The number of values is determined from the
 * size of the tag.
 * 
 * 0..3		4	'ui16' (75693136h) type signature
 * 4..7		4	 reserved, must be set to 0
 * 8..end		an array of unsigned 16-bit integers
 */
public class UInt16ArrayType extends AbstractTagType
{
	public static final int SIGNATURE = 0x75693136;
	
	private	Signature		signature_;
	private	UInt16Number[]		array_;

	public UInt16ArrayType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"ui16");
	}

	public UInt16ArrayType(byte[] byteArray) throws ICCProfileException
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
		if (this.signature_.intValue() != 0x75693136)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		if (len <= 0)
			len = (byteArray.length - offset - 8) / UInt16Number.SIZE;
		if (len <= 0)
			throw new ICCProfileException("array empty", ICCProfileException.WrongSizeException);

		this.array_ = new UInt16Number[len];
		int idx = offset + 8;
		for (int i=0; i<len; i++)
		{
			this.array_[i] = new UInt16Number(byteArray, idx);
			idx += UInt16Number.SIZE;
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.array_==null)
			throw new ICCProfileException("array not set",ICCProfileException.InvalidDataValueException);

		int len = 8 + this.array_.length * UInt16Number.SIZE;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		int idx = 8;
		for (int i=0; i<this.array_.length; i++)
		{
			ICCUtils.appendByteArray(all, idx, this.array_[i]);
			idx += UInt16Number.SIZE;
		}

		return all;
	}
	
	public int size()
	{
		assert(this.array_!=null);
		return 8 + this.array_.length * UInt16Number.SIZE;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	public int getArraySize()
	{
		return this.array_.length;
	}

	public UInt16Number[] getArray()
	{
		return this.array_;
	}

	public void addNumber(UInt16Number val)
	{
		if (this.array_==null)
			this.array_ = new UInt16Number[1];
		else
		{
			UInt16Number[] bac = this.array_;
			this.array_ = new UInt16Number[bac.length+1];
			for (int i=0; i<bac.length; i++)
				this.array_[i] = bac[i];
		}
		this.array_[array_.length-1] = val;
	}


	/**
	 * Add an array of byte values to the byte array type.
	 */
	public void addNumbers(int[] vals) throws ICCProfileException
	{
		int len = vals.length;
		int idx = 0;
		if (this.array_==null)
			this.array_ = new UInt16Number[len];
		else
		{
			UInt16Number[] bac = this.array_;
			idx = bac.length;
			this.array_ = new UInt16Number[bac.length+len];
			for (int i=0; i<bac.length; i++)
				this.array_[i] = bac[i];
		}
		for (int i=0; i<len; i++)
			this.array_[idx++] = new UInt16Number(vals[i]);
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
			sb.append("<uInt16ArrayType sig=\"ui16\">");
		else
			sb.append("<uInt16ArrayType name=\""+name+"\" sig=\"ui16\">");
		sb.append(signature_.toXmlString());
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<array_.length; i++)
		{
			//sb.append(array_[i].toXmlString());
			if (i > 0) sb.append(", ");
			sb.append(array_[i]);
		}
		sb.append("</dim></array>");
		sb.append("</uInt16ArrayType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
