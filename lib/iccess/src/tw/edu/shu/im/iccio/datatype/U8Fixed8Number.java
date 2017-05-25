package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.Streamable;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * U8Fixed8Number is a fixed unsigned 2-byte/16-bit quantity which has 8 fractional bitsas.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * 
 * Number				Encoding
 * 0					0000h
 * 1.0					0100h
 * 255+(255/256)		FFFFh
 */
public class U8Fixed8Number implements Streamable
{
	public static final int SIZE = 2;

	public static final double MIN_VALUE = 0.;
	public static final double MID_VALUE = 1.0;
	public static final double MAX_VALUE = 255.99609375;

	protected static final double BYTE_DIVISOR = 256.0;

	private	UInt8Number	whole_;
	private	UInt8Number	fract_;
    
	public U8Fixed8Number()
	{
	}

    public U8Fixed8Number(byte[] asByteArray) throws ICCProfileException
    {
        fromByteArray(asByteArray, 0, SIZE);
    }

	public U8Fixed8Number(byte[] asByteArray, int offset) throws ICCProfileException
	{
		fromByteArray(asByteArray, offset, SIZE);
	}
    
    public U8Fixed8Number(double value) throws ICCProfileException
    {
        parseValue(value);
    }

	public U8Fixed8Number(U8Fixed8Number copy)
	{
		this.whole_ = new UInt8Number(copy.whole_);
		this.fract_ = new UInt8Number(copy.fract_);
	}

	public void parseValue(double value) throws ICCProfileException
	{
		long i = (long)value;
		double f = value - i;
		if (i > 255 || f > 1.0)
		{
			throw new ICCProfileException("double value ("+value+") too large for U8Fixed8Number",ICCProfileException.OverflowException);
		}
		this.whole_ = new UInt8Number((short)i);
		this.fract_ = new UInt8Number((short)(f * BYTE_DIVISOR));
	}

    public double doubleValue()
    {
		int whole = (this.whole_==null)?0:whole_.intValue();
		int fract = (this.fract_==null)?0:fract_.intValue();
		return (double) whole + (double) fract / BYTE_DIVISOR;
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

		this.whole_ = new UInt8Number(asByteArray, offset);
		this.fract_ = new UInt8Number(asByteArray, offset + UInt8Number.SIZE);
	}
    
	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.whole_ == null || this.fract_ == null)
			throw new ICCProfileException("data not set", ICCProfileException.InvalidDataValueException);

		byte[] ba = new byte[size()];
		if (ba.length != 2)
			throw new ICCProfileException("size wrong", ICCProfileException.WrongSizeException);
		ba[0] = (byte)whole_.intValue();
		ba[1] = (byte)fract_.intValue();

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
		sb.append("<u8Fixed8Number");
		if (name!=null && name.length()>0)
			sb.append(" name=\""+name+"\"");
		sb.append(" integral=\""+whole_.toString()+"\"");
		sb.append(" fraction=\""+fract_.toString()+"\"");
		sb.append(">");
		sb.append(toString());
		sb.append("</u8Fixed8Number>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
