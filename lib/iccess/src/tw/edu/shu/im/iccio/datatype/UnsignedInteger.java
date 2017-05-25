package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.Streamable;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * UnsignedInteger is an abstract class for most unsigned integer types that share similar operations.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The UnsignedInteger class implements the Streamable interface for all unsigned integer classes 
 * except UInt64Number which is implemented based on java.math.BigInteger.
 * 
 * @see UInt8Number
 * @see UInt16Number
 * @see UInt32Number
 */
public abstract class UnsignedInteger implements Streamable
{
	/**
	 * Load an unsigned integer value from a given byte array.  It is treated as a big-endian integer.
	 * The loaded integer will be assigned to the specific UIntxxNumber class instance by calling its setValue(long) method.
	 * If this method is not called from a proper derived class then an exception will be thrown.
	 * 
	 * @param asByteArray - the byte array that contains the wanted integer, it can contain more bytes than the integer itself.
	 * @param offset - the position of the first byte of the integer in the byte array.
	 * @param len - number of bytes for this integer, 1 for byte, 2 for short, 4 for int.
	 */
	public void fromByteArray(byte[] asByteArray, int offset, int len) throws ICCProfileException
	{
		if (asByteArray == null)
			throw new ICCProfileException("UnsignedInteger.fromByteArray():byte array null", ICCProfileException.NullPointerException);

		if (offset < 0)
			throw new ICCProfileException("UnsignedInteger.fromByteArray():offset < 0", ICCProfileException.IndexOutOfBoundsException);

		if (len <= 0)
			throw new ICCProfileException("UnsignedInteger.fromByteArray():bad argument: len<=0", ICCProfileException.IllegalArgumentException);

		if (len != size())
			throw new ICCProfileException("UnsignedInteger.fromByteArray():len parameter is not equal to SIZE", ICCProfileException.WrongSizeException);

		if (asByteArray.length < offset + len)
			throw new ICCProfileException("UnsignedInteger.fromByteArray():offset outside byte array", ICCProfileException.IndexOutOfBoundsException);

		int n = len - 1;
		long value = (long)(asByteArray[offset+n] & 0xff);
		if (n > 0)
		{
			int bits = 8;
			while (--n >= 0)
			{
				value |= (long)((asByteArray[offset+n] & 0xff) << bits);
				bits += 8;
			}
		}

		setValue(value);
	}

	/**
	 * Convert the number into a byte array in a big-endian order. This is suitable for
	 * UInt8Number, UInt16Number, UInt32Number.
	 * 
	 * If this method is not called from a proper derived class then an exception will be thrown.
	 * 
	 * @return byte[] as long as the data type.
	 */
	public byte[] toByteArray() throws ICCProfileException
	{
		int n = size();
		if (n <= 0)
		{
			throw new ICCProfileException("UnsignedInteger.toByteArray():invalid size(), check derived class for size() and SIZE", ICCProfileException.WrongSizeException);
		}
		long value = longValue();	//longValue() should be implemented in the derived classes
		if (value < 0)
		{
			throw new ICCProfileException("invalid longValue() return, check derived class", ICCProfileException.InvalidDataValueException);
		}
		byte[] ba = new byte[n];
		if (value == 0)
		{
			return ba;		//no need to convert, array of zeros
		}
		ba[--n] = (byte)value;		//value_ & 0xFF
		if (n <= 0)
		{
			return ba;		//one byte only, done
		}
		int bits = 8;
		for (--n; n>0; --n)
		{
			ba[n] = (byte)(value >> bits);
			bits += 8;
		}
		ba[0] = (byte)(value >>> bits);	//keep highest bit for unsigned
		return ba;
	}

	/**
	 * This method must be implemented/overriden in all derived classes. It returns an integer value casted to the long type.
	 */
	public abstract long longValue();

	/**
	 * This method must be implemented/overriden in all derived classes. It accepts a long integer and cast to the particular type.
	 * It hopefully checks exceptional cases where the value is invalid such as out of range.
	 */
	public abstract void setValue(long value) throws ICCProfileException;

	/**
	 * This method must be implemented/overriden in all derived classes. It returns the specific SIZE, the number of bytes the integer occupies.
	 */
	public abstract int size();


	/**
	 * Return XML element of this object.
	 * @param name - attribute name on element
	 * @return XML fragment as a string
	 */
	public abstract String toXmlString(String name);

	public abstract String toXmlString();

}
