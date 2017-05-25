package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * UInt32Number is a 32-bit (4-byte) unsigned integer as big-endian.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * 
 */
public class UInt32Number extends UnsignedInteger
{
	public static final int SIZE = 4;

	public static final int MIN_VALUE = 0;
	public static final long MAX_VALUE = 4294967295L;

	private long value_;

	public UInt32Number()
	{
	}

	public UInt32Number(int value)
	{
		setValue(value);
	}

	public UInt32Number(long value) throws ICCProfileException
	{
		setValue(value);
	}

	public UInt32Number(String svalue) throws ICCProfileException
	{
		setValue(svalue);
	}

	public UInt32Number(UInt32Number copy)
	{
		this.value_ = copy.value_;
	}

	public UInt32Number(byte[] asByteArray) throws ICCProfileException
	{
		fromByteArray(asByteArray, 0, SIZE);
	}

	public UInt32Number(byte[] asByteArray, int offset) throws ICCProfileException
	{
		fromByteArray(asByteArray, offset, SIZE);
	}

	public long longValue()
	{
		return this.value_;
	}

	public int intValue()
	{
		return (int)this.value_;
	}

	/**
	 * Java int ranges from ?2,147,483,648 to +2,147,483,647. To allow assignment of unsigned int, use negative int instead.
	 */
	public void setValue(int value)
	{
		if (value < 0)
		{
			this.value_ = 4294967296L + value;
		}
		else
			this.value_ = value;
	}

	public void setValue(long value) throws ICCProfileException
	{
		if (value < 0)
			value = 4294967296L + value;

		if (value < MIN_VALUE || value > MAX_VALUE)
			throw new ICCProfileException("number out of range:"+value, ICCProfileException.OverflowException);

		this.value_ = value;
	}

	public void setValue(String svalue) throws ICCProfileException
	{
		try
		{
			long v = Long.parseLong(svalue);
			setValue(v);
		}
		catch (NumberFormatException e)
		{
			throw new ICCProfileException("number format exception:"+svalue, ICCProfileException.NumberFormatException);
		}
	}

	public void setValue(String svalue, int radix) throws ICCProfileException
	{
		try
		{
			long v = Long.parseLong(svalue, radix);
			setValue(v);
		}
		catch (NumberFormatException e)
		{
			throw new ICCProfileException("number format exception"+svalue, ICCProfileException.NumberFormatException);
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
			sb.append("<uInt32Number>");
		else
			sb.append("<uInt32Number name=\""+name+"\">");
		sb.append(toString());
		sb.append("</uInt32Number>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
