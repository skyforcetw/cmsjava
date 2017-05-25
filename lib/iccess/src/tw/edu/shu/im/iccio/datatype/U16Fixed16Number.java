package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.Streamable;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * U16Fixed16Number is a fixed unsigned 4-byte/32-bit quantity which has 16 fractional bits.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * 
 * 
 * Number				Encoding
 * 0					00000000h
 * 1.0					00010000h
 * 65535+(65535/65536)	FFFFFFFFh
 */
public class U16Fixed16Number implements Streamable
{
    public static final int SIZE = 4;

	public static final double MIN_VALUE = 0.0;
	public static final double MID_VALUE = 1.0;
	public static final double MAX_VALUE = 65535.9999847412109375;

	protected static final double USHORT_DIVISOR = 65536.0;

	private	UInt16Number	whole_;
	private	UInt16Number	fract_;
    
	public U16Fixed16Number()
	{
	}

    public U16Fixed16Number(byte[] asByteArray) throws ICCProfileException
    {
        fromByteArray(asByteArray, 0, SIZE);
    }

	public U16Fixed16Number(byte[] asByteArray, int offset) throws ICCProfileException
	{
		fromByteArray(asByteArray, offset, SIZE);
	}
    
    public U16Fixed16Number(double value) throws ICCProfileException
    {
        parseValue(value);
    }

	public U16Fixed16Number(U16Fixed16Number copy)
	{
		this.whole_ = new UInt16Number(copy.whole_);
		this.fract_ = new UInt16Number(copy.fract_);
	}

	public void parseValue(double value) throws ICCProfileException
	{
		long i = (long)value;
		double f = value - i;
		if (i < 0 || i >= (int)USHORT_DIVISOR || f < 0.0 || f >= 1.0)
		{
			throw new ICCProfileException("parseValue("+value+") value out of range of U16Fixed16Number",ICCProfileException.OverflowException);
		}
		this.whole_ = new UInt16Number((int)i);
		this.fract_ = new UInt16Number((int)(f * USHORT_DIVISOR));
	}

	public double doubleValue()
	{
		int whole = (this.whole_==null)?0:whole_.intValue();
		int fract = (this.fract_==null)?0:fract_.intValue();
		return (double) whole + (double) fract / USHORT_DIVISOR;
	}
    
	public void fromByteArray(byte[] asByteArray, int offset, int len) throws ICCProfileException
    {
		if (asByteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);

		if (offset < 0)
			throw new ICCProfileException("offset < 0", ICCProfileException.IndexOutOfBoundsException);

		if (len != SIZE)
			throw new ICCProfileException("len parameter is not equal to SIZE", ICCProfileException.WrongSizeException);

		if (asByteArray.length < offset+len)
			throw new ICCProfileException("offset outside byte array", ICCProfileException.IndexOutOfBoundsException);

		this.whole_ = new UInt16Number(asByteArray, offset);
		this.fract_ = new UInt16Number(asByteArray, offset + UInt16Number.SIZE);
    }
    
	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.whole_ == null || this.fract_ == null)
			throw new ICCProfileException("data not set", ICCProfileException.InvalidDataValueException);

		byte[] ba = new byte[size()];
		if (ba.length != 4)
			throw new ICCProfileException("size wrong", ICCProfileException.WrongSizeException);
		int whole = (this.whole_==null)?0:whole_.intValue();
		int fract = (this.fract_==null)?0:fract_.intValue();
		ba[0] = (byte)(whole >> 8);
		ba[1] = (byte)whole;
		ba[2] = (byte)(fract >> 8);
		ba[3] = (byte)fract;
		return ba;
	}

	public int size()
	{
		return this.SIZE;
	}

	/**
	 * Make a string of this fixed-point real number.
	 * @return the number as a string.
	 */
	public String toString()
	{
		return String.valueOf(doubleValue());
	}


	/**
	 * Return XML element of this object.
	 * @param name - attribute name on element
	 * @return XML fragment as a string
	 */
	public String toXmlString(String name)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<u16Fixed16Number");
		if (name!=null && name.length()>0)
			sb.append(" name=\""+name+"\"");
		sb.append(" integral=\""+whole_.toString()+"\"");
		sb.append(" fraction=\""+fract_.toString()+"\"");
		sb.append(">");
		sb.append(toString());
		sb.append("</u16Fixed16Number>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
