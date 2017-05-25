package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.Streamable;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * U1Fixed15Number is a fixed unsigned 2-byte/16-bit quantity, which has 15 fractional bits
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * 
 * Number				Encoding
 * 0					0000h
 * 1.0					8000h
 * 1+(32767/32768)		FFFFh
 */
public class U1Fixed15Number implements Streamable
{
	public static final int SIZE = 2;

	public static final double MIN_VALUE = 0.;
	public static final double MID_VALUE = 1.0;
	public static final double MAX_VALUE = 1.999969482421875;
 
	protected static final double SHORT_DIVISOR = 32768.0;

	private	short	whole_;
	private	short	fract_;
    
	public U1Fixed15Number()
	{
	}

    public U1Fixed15Number(byte[] asByteArray) throws ICCProfileException
    {
        fromByteArray(asByteArray, 0, SIZE);
    }
    
	public U1Fixed15Number(byte[] asByteArray, int offset) throws ICCProfileException
	{
		fromByteArray(asByteArray, offset, SIZE);
	}
    
	public U1Fixed15Number(double value) throws ICCProfileException
    {
        parseValue(value);
    }
    
	public U1Fixed15Number(U1Fixed15Number copy)
	{
		this.whole_ = copy.whole_;
		this.fract_ = copy.fract_;
	}

	public void parseValue(double value) throws ICCProfileException
	{
		long i = (long)value;
		double f = value - i;
		if (i > 1 || i < 0 || f >= 1.0 || f < 0)
		{
			throw new ICCProfileException("parseValue("+value+") value out of range of U1Fixed15Number",ICCProfileException.OverflowException);
		}
		this.whole_ = (short)i;
		this.fract_ = (short) (f * SHORT_DIVISOR);
	}

    public double doubleValue()
    {
        return (double)this.whole_ + (double)this.fract_ / SHORT_DIVISOR;
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

		this.whole_ = (short)(((asByteArray[offset] & 0x80) == 0x80)?1:0);
        this.fract_ = (short)((asByteArray[offset] & 0x7f) << 8 | asByteArray[offset+1] & 0xff);
    }
    
	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.whole_ < 0 || this.fract_ < 0)
			throw new ICCProfileException("negative value", ICCProfileException.InvalidDataValueException);

		byte[] ba = new byte[size()];
		if (ba.length != 2)
			throw new ICCProfileException("size wrong", ICCProfileException.WrongSizeException);

		ba[0] = (byte)(fract_ >> 8);
		ba[1] = (byte)fract_;
		if (whole_ > 0)
		{
			ba[0] |= 0x80;
		}

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
		sb.append("<u1Fixed15Number");
		if (name!=null && name.length()>0)
			sb.append(" name=\""+name+"\"");
		sb.append(" integral=\""+String.valueOf(whole_)+"\"");
		sb.append(" fraction=\""+String.valueOf(fract_)+"\"");
		sb.append(">");
		sb.append(toString());
		sb.append("</u1Fixed15Number>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
