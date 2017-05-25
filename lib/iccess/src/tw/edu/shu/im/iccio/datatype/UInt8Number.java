package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * UInt8Number is a generic unsigned 1-byte/8-bit quantity.
 *  
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 *
 * 
 */
public class UInt8Number extends UnsignedInteger
{
	public static final int SIZE = 1;

	public static final int MIN_VALUE = 0;
	public static final int MAX_VALUE = 255;

	private short value_;

	public UInt8Number()
	{
	}

	public UInt8Number(byte val)
	{
		setValue(val);
	}

	public UInt8Number(short val) throws ICCProfileException
	{
		setValue(val);
	}

	public UInt8Number(int val) throws ICCProfileException
	{
		setValue(val);
	}

	public UInt8Number(String sval) throws ICCProfileException
	{
		setValue(sval);
	}

	public UInt8Number(byte[] asByteArray) throws ICCProfileException
	{
		fromByteArray(asByteArray, 0, SIZE);
	}

	public UInt8Number(byte[] asByteArray, int offset) throws ICCProfileException
	{
		fromByteArray(asByteArray, offset, SIZE);
	}

	public UInt8Number(UInt8Number copy)
	{
		this.value_ = copy.value_;
	}

	public byte byteValue()
	{
		return (byte)this.value_;
	}

	public short shortValue()
	{
		return (short)this.value_;
	}

	public int intValue()
	{
		return (int)this.value_;
	}

	public long longValue()
	{
		return (long)this.value_;
	}

	/**
	 * This method allows specifying unsigned byte with a singed byte value.
	 */
	public void setValue(byte value)
	{
		if (value < 0)
		{
			this.value_ = (short)(256 + value);
		}
		else
			this.value_ = (short)value;
	}

	public void setValue(short val) throws ICCProfileException
	{
		if (val < 0) val = (short)(256 + val);

		if (val < MIN_VALUE || val > MAX_VALUE)
			throw new ICCProfileException("number out of range", ICCProfileException.OverflowException);

		this.value_ = val;
	}

	public void setValue(int val) throws ICCProfileException
	{
		if (val < 0) val = 256 + val;

		if (val < MIN_VALUE || val > MAX_VALUE)
			throw new ICCProfileException("number out of range", ICCProfileException.OverflowException);

		this.value_ = (short)val;
	}

	public void setValue(long val) throws ICCProfileException
	{
		if (val < 0) val = 256 + val;

		if (val < MIN_VALUE || val > MAX_VALUE)
			throw new ICCProfileException("number out of range", ICCProfileException.OverflowException);

		this.value_ = (short)val;
	}

	public void setValue(String sval) throws ICCProfileException
	{
		try
		{
			short v = Short.parseShort(sval);
			setValue(v);
		}
		catch (NumberFormatException e)
		{
			throw new ICCProfileException("number format exception", ICCProfileException.NumberFormatException);
		}
	}

	public void setValue(String sval, int radix) throws ICCProfileException
	{
		try
		{
			short v = Short.parseShort(sval, radix);
			setValue(v);
		}
		catch (NumberFormatException e)
		{
			throw new ICCProfileException("number format exception", ICCProfileException.NumberFormatException);
		}
	}

	public int size()
	{
		return this.SIZE;
	}

	public String toString()
	{
		return String.valueOf(this.value_);
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
			sb.append("<uInt8Number>");
		else
			sb.append("<uInt8Number name=\""+name+"\">");
		sb.append(toString());
		sb.append("</uInt8Number>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
