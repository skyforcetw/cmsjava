package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * UInt16Number is a Big-endian unsigned 16-bit integer (unsigned short).
 *  
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 *
 * 
 */
public class UInt16Number extends UnsignedInteger
{
	public static final int SIZE = 2;

	public static final int MIN_VALUE = 0;
	public static final int MAX_VALUE = 65535;

    private int value_;

	public UInt16Number()
	{
	}

	public UInt16Number(short val)
	{
		setValue(val);
	}

	public UInt16Number(String sval) throws ICCProfileException
	{
		setValue(sval);
	}

	public UInt16Number(byte[] asByteArray) throws ICCProfileException
	{
		fromByteArray(asByteArray, 0, SIZE);
	}

	public UInt16Number(byte[] asByteArray, int offset) throws ICCProfileException
	{
		fromByteArray(asByteArray, offset, SIZE);
	}

	public UInt16Number(int value)
	{
		this.value_ = value;
	}

	public UInt16Number(UInt16Number copy)
	{
		this.value_ = copy.value_;
	}

	public short shortValue()
	{
		return (short)this.value_;
	}

	public int intValue()
	{
		return this.value_;
	}

	public long longValue()
	{
		return (long)this.value_;
	}

	/**
	 * This method allows specifying unsigned short integer with a negative short value.
	 * For example, hex 0xFFFF (in byte[2])  means -1 as short value, but 65535 as int value. We can then use the short value -1 to mean 65535.
	 */
	public void setValue(short val)
	{
		if (val < 0)
		{
			this.value_ = 65536 + val;
		}
		else
			this.value_ = (int)val;
	}

	/**
	 * As setValue(short), this method can also set unsigned value by a negative value.
	 * If the result value is still negative or greater than the maximum value, an exception is thrown.
	 */
	public void setValue(int value) throws ICCProfileException
	{
		if (value < 0)
			value = 65536 + value;

		if (value < MIN_VALUE || value > MAX_VALUE)
			throw new ICCProfileException("value not in range", ICCProfileException.OverflowException);

		this.value_ = value;
	}

	public void setValue(long value) throws ICCProfileException
	{
		if (value < 0)
			value = 65536 + value;

		if (value < MIN_VALUE || value > MAX_VALUE)
			throw new ICCProfileException("value not in range", ICCProfileException.OverflowException);

		this.value_ = (int)value;
	}

	public void setValue(String sval) throws ICCProfileException
	{
		try
		{
			int v = Integer.parseInt(sval);
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
			int v = Integer.parseInt(sval, radix);
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
			sb.append("<uInt16Number>");
		else
			sb.append("<uInt16Number name=\""+name+"\">");
		sb.append(toString());
		sb.append("</uInt16Number>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
