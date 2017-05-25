package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.S15Fixed16Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * S15Fixed16ArrayType is a tag type for an array of 32-bit fixed point real number.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * This type represents an array of generic 4-byte/32-bit fixed point quantity. The number of values is determined
 * from the size of the tag.
 *
 * 0..3		4	'sf32' (73663332h) type signature
 * 4..7		4	reserved, must be set to 0
 * 8..end		an array of s15Fixed16Number values
 */
public class S15Fixed16ArrayType extends AbstractTagType
{
	public static final int SIGNATURE = 0x73663332;
	
	private	Signature		signature_;
	private	S15Fixed16Number[]	array_;

	public S15Fixed16ArrayType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"sf32");
	}

	public S15Fixed16ArrayType(byte[] byteArray) throws ICCProfileException
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
		if (this.signature_.intValue() != 0x73663332)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		int count = (byteArray.length - offset - 8) / S15Fixed16Number.SIZE;
		if (count <= 0)
			throw new ICCProfileException("array empty", ICCProfileException.WrongSizeException);

		this.array_ = new S15Fixed16Number[count];
		int idx = offset + 8;
		for (int i=0; i<count; i++)
		{
			this.array_[i] = new S15Fixed16Number(byteArray, idx);
			idx += S15Fixed16Number.SIZE;
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.array_==null)
			throw new ICCProfileException("array not set",ICCProfileException.InvalidDataValueException);

		int len = 8 + this.array_.length * S15Fixed16Number.SIZE;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		int idx = 8;
		for (int i=0; i<this.array_.length; i++)
		{
			ICCUtils.appendByteArray(all, idx, this.array_[i]);
			idx += S15Fixed16Number.SIZE;
		}

		return all;
	}
	
	public int size()
	{
		assert(this.array_!=null);
		return 8 + this.array_.length * S15Fixed16Number.SIZE;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	public int getArraySize()
	{
		return this.array_.length;
	}

	public S15Fixed16Number[] getArray()
	{
		return this.array_;
	}

	/**
	 * Add a single S15Fixed16Number value.
	 */
	public void addNumber(S15Fixed16Number val)
	{
		if (this.array_==null)
			this.array_ = new S15Fixed16Number[1];
		else
		{
			S15Fixed16Number[] bac = this.array_;
			this.array_ = new S15Fixed16Number[bac.length+1];
			for (int i=0; i<bac.length; i++)
				this.array_[i] = bac[i];
		}
		this.array_[array_.length-1] = val;
	}

	/**
	 * Add an array of double values.
	 */
	public void addNumbers(double[] vals) throws ICCProfileException
	{
		int idx = 0;
		if (this.array_ == null)
		{
			this.array_ = new S15Fixed16Number[vals.length];
		}
		else
		{
			S15Fixed16Number[] bac = this.array_;
			idx = bac.length;
			this.array_ = new S15Fixed16Number[bac.length+vals.length];
			for (int i=0; i<bac.length; i++)
				this.array_[i] = bac[i];
		}
		for (int i=0; i<vals.length; i++)
		{
			this.array_[idx++] = new S15Fixed16Number(vals[i]);
		}
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
			sb.append("<s15Fixed16ArrayType sig=\"sf32\">");
		else
			sb.append("<s15Fixed16ArrayType name=\""+name+"\" sig=\"sf32\">");
		sb.append(signature_.toXmlString());
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<array_.length; i++)
		{
			sb.append(array_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("</s15Fixed16ArrayType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
