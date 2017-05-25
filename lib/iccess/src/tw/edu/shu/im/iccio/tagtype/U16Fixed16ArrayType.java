package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.U16Fixed16Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * U16Fixed16ArrayType is a tag type for array of U16Fixed16Number data type.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * This type represents an array of generic 4-byte/32-bit quantity. The number of values is determined from the
 * size of the tag.
 * 
 * 0..3		4	'uf32' (75663332h) type signature
 * 4..7		4	reserved, must be set to 0
 * 8..end		an array of u16Fixed16Number values
 */
public class U16Fixed16ArrayType extends AbstractTagType
{
	public static final int SIGNATURE = 0x75663332;
	
	private	Signature		signature_;
	private	U16Fixed16Number[]	array_;

	public U16Fixed16ArrayType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"uf32");
	}

	public U16Fixed16ArrayType(byte[] byteArray) throws ICCProfileException
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
		if (this.signature_.intValue() != 0x75663332)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		if (len <= 0)
			len = (byteArray.length - offset - 8) / U16Fixed16Number.SIZE;
		if (len <= 0)
			throw new ICCProfileException("array empty", ICCProfileException.WrongSizeException);

		this.array_ = new U16Fixed16Number[len];
		int idx = 8;
		for (int i=0; i<len; i++)
		{
			this.array_[i] = new U16Fixed16Number(byteArray, idx);
			idx += U16Fixed16Number.SIZE;
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.array_==null)
			throw new ICCProfileException("array not set",ICCProfileException.InvalidDataValueException);

		int len = 8 + this.array_.length * U16Fixed16Number.SIZE;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		int idx = 8;
		for (int i=0; i<this.array_.length; i++)
		{
			ICCUtils.appendByteArray(all, idx, this.array_[i]);
			idx += U16Fixed16Number.SIZE;
		}

		return all;
	}
	
	public int size()
	{
		assert(this.array_!=null);
		return 8 + this.array_.length * U16Fixed16Number.SIZE;
	}
	
	public Signature getSignature()
	{
		return this.signature_;
	}
	public int getArraySize()
	{
		return this.array_.length;
	}

	public U16Fixed16Number[] getArray()
	{
		return this.array_;
	}

	public void addNumber(U16Fixed16Number val)
	{
		if (this.array_==null)
			this.array_ = new U16Fixed16Number[1];
		else
		{
			U16Fixed16Number[] bac = this.array_;
			this.array_ = new U16Fixed16Number[bac.length+1];
			for (int i=0; i<bac.length; i++)
				this.array_[i] = bac[i];
		}
		this.array_[array_.length-1] = val;
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
			sb.append("<u16Fixed16ArrayType sig=\"uf32\">");
		else
			sb.append("<u16Fixed16ArrayType name=\""+name+"\" sig=\"uf32\">");
		sb.append(signature_.toXmlString());
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<array_.length; i++)
		{
			sb.append(array_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("</u16Fixed16ArrayType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
