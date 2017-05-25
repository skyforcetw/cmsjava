package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.Streamable;
import tw.edu.shu.im.iccio.ICCProfileException;
import java.math.BigInteger;

/**
 * UInt64Number data type is a 64-bit long integer.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-28
 * 
 * Because Java does not have unsigned integers, the Java long int cannot 
 * represent an unsigned 64-bit long integer.  The java.math.BigInteger
 * is used in this class to represent unsigned long integer. That's why
 * it is not directly derived from the UnsignedInteger abstract type as
 * other unsigned integer types.
 */
public class UInt64Number implements Streamable
{
	public static final int SIZE = 8;

	private BigInteger value_;

	public UInt64Number()
	{
		this.value_ = BigInteger.valueOf(0L);
	}
	
	public UInt64Number(long val)
	{
		setValue(val);
	}

	public UInt64Number(String sval) throws ICCProfileException
	{
		setValue(sval);
	}

	public UInt64Number(UInt64Number copy)
	{
		this.value_ = copy.value_;
	}

	public UInt64Number(byte[] asByteArray) throws ICCProfileException
	{
		fromByteArray(asByteArray, 0, SIZE);
	}
	
	public UInt64Number(byte[] asByteArray, int offset) throws ICCProfileException
	{
		fromByteArray(asByteArray, offset, SIZE);
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

		byte[] newArray = new byte[len];
		for (int i=0; i<newArray.length; i++)
		{
			newArray[i] = asByteArray[offset+i];
		}

		this.value_ = new BigInteger(newArray);
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		byte[] a = this.value_.toByteArray();
		if (a.length > 8)
		{
			throw new ICCProfileException("UInt64Number got a number larger than a 8-byte long can hold,value="+value_.toString(),ICCProfileException.WrongSizeException);
		}
		byte[] ar = new byte[8];
		for (int i=7,j=a.length-1;j>=0;i--,j--)
		{
			ar[i] = a[j];
		}
		return ar;
	}

	public long longValue()
	{
		if (value_ == null) return 0L;
		return value_.longValue();
	}
	
	public int intValue()
	{
		if (value_ == null) return 0;
		return value_.intValue();
	}
	
	public int size()
	{
		return this.SIZE;
	}
	
	/**
	 * This method allows specifying a unsigned long with negative long integer.
	 */
	public void setValue(long val)
	{
		if (val < 0)
		{
			BigInteger s = new BigInteger("18446744073709551616");	//2^64
			BigInteger v = new BigInteger(String.valueOf(val));		//negative val
			this.value_ = s.add(v);
		}
		else
			this.value_ = BigInteger.valueOf(val);
	}

	/**
	 * This method allows specifying a unsigned long with negative int integer.
	 */
	public void setValue(int val)
	{
		if (val < 0)
		{
			long v = 4294967296L + val;
			this.value_ = BigInteger.valueOf(v);
		}
		else
			this.value_ = BigInteger.valueOf((long)val);
	}

	/**
	 * Set the unsigned long number with a string. Negative value such as "-12345" will cause an exception.
	 */
	public void setValue(String sval) throws ICCProfileException
	{
		if (sval.trim().startsWith("-"))
			throw new ICCProfileException("negative number not allowed", ICCProfileException.OverflowException);
		this.value_ = new BigInteger(sval);
	}

	public void setValue(String sval, int radix) throws ICCProfileException
	{
		if (sval.trim().startsWith("-"))
			throw new ICCProfileException("negative number not allowed", ICCProfileException.OverflowException);
		this.value_ = new BigInteger(sval, radix);
	}

	public String toString()
	{
		return this.value_.toString();
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
			sb.append("<uInt64Number>");
		else
			sb.append("<uInt64Number name=\""+name+"\">");
		sb.append(toString());
		sb.append("</uInt64Number>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
