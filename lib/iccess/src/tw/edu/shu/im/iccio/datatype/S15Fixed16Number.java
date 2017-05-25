package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.Streamable;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * S15Fixed16Number is a fixed signed 4-byte/32-bit quantity which has 16 fractional bits
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * 
 * Number					Encoding
 * -32768.0					80000000h
 * 0						00000000h
 * 1.0						00010000h
 * 32767+(65535/65536)		7FFFFFFFh
 */
public class S15Fixed16Number implements Streamable
{
    public static final int SIZE = 4;

	public static final double MIN_VALUE = -32768.0;
	public static final double MID_VALUE = 0.0;
	public static final double MAX_VALUE = 32767.9999847412109375;

	protected static final double USHORT_DIVISOR = 65536.0;

	private	int			whole_;
	private	short		sign_;	//1 or -1, useful when whole_ is 0, so use this instead of whole_ for sign
	private	UInt16Number	fract_;
    
    public S15Fixed16Number()
    {
		//TODO: should all these numbers default to zero instead of null pointers?
    }

	public S15Fixed16Number(S15Fixed16Number copy)
	{
		this.whole_ = copy.whole_;
		this.sign_ = copy.sign_;
		this.fract_ = new UInt16Number(copy.fract_);
	}
    
    public S15Fixed16Number(byte[] asByteArray) throws ICCProfileException
    {
        fromByteArray(asByteArray, 0, SIZE);
    }
    
	public S15Fixed16Number(byte[] asByteArray, int offset) throws ICCProfileException
	{
		fromByteArray(asByteArray, offset, SIZE);
	}

    public S15Fixed16Number(double value) throws ICCProfileException
    {
        parseValue(value);
    }

	/**
	 * The value should be in the range of -32768.0 .. 32767.9999999
	 */
	public void parseValue(double value) throws ICCProfileException
	{
		//System.out.print(value);
		if (value > MAX_VALUE || value < MIN_VALUE)
		{
			throw new ICCProfileException("parseValue("+value+") value out of range of S15Fixed16Number",ICCProfileException.OverflowException);
		}
		sign_ = (value < 0)?(short)-1:(short)1;
		value = Math.abs(value);
		long i = (long)value;
		double f = value - i;
		this.whole_ = (int)i;
		this.fract_ = new UInt16Number((int)(f * USHORT_DIVISOR));
		//System.out.println("=> "+sign_+","+i+","+fract_);
	}
    
    public double doubleValue()
    {
		int fract = (this.fract_==null)?0:fract_.intValue();
		double x = (double)sign_ * ((double)whole_ + (double)fract / USHORT_DIVISOR);
		//System.out.println("doubleValue: "+sign_+","+whole_+","+fract+"->"+x);
	    return x;
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

		this.sign_ = ((asByteArray[offset] & 0x80) == 0x80)?(short)-1:(short)1;
		this.whole_ = (int)((asByteArray[offset] & 0x7F) << 8 | asByteArray[offset+1] & 0xff);
		this.fract_ = new UInt16Number(asByteArray, offset+2);
    }

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.fract_ == null)
			throw new ICCProfileException("data not set", ICCProfileException.InvalidDataValueException);

		byte[] ba = new byte[size()];
		if (ba.length != 4)
			throw new ICCProfileException("size wrong", ICCProfileException.WrongSizeException);
		ba[0] = (byte)(whole_ >> 8);
		if (this.sign_ < 0)
		   ba[0] |= 0x80;
		ba[1] = (byte)whole_;
		int fract = fract_.intValue();
		ba[2] = (byte)(fract >> 8);
		ba[3] = (byte)(fract & 0xff);

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
		sb.append("<s15Fixed16Number");
		if (name!=null && name.length()>0)
			sb.append(" name=\""+name+"\"");
		sb.append(" integral=\""+String.valueOf(whole_)+"\"");
		sb.append(" fraction=\""+fract_.toString()+"\"");
		sb.append(">");
		sb.append(toString());
		sb.append("</s15Fixed16Number>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
