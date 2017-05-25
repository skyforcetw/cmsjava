package tw.edu.shu.im.iccio;

/**
 * General exception class for the ICC profile I/O classes.
 *
 * @author Ted Wen
 * @version 0.1
 * @update 2006-11-03
 *
 * The ICCProfileException wraps all kinds of exceptions into one class
 * so as to eliminate the trouble of catching various kinds of exceptions.
 *
 * The included exception types are represented as constant int values.
 * It is better to represent these values as enum constants, but that
 * relies on the JDK version 1.5.  The current implementation can compile
 * with version 1.4.
 */
public class ICCProfileException extends Exception
{
	public static final int NullPointerException = 1;		///wrap the java.lang.NullPointerException, usually occurs when call method on null object
	public static final int IndexOutOfBoundsException = 2;		///java.lang.IndexOutOfBoundsException for access of arrays
	public static final int IllegalArgumentException = 3;		///java.lang.IllegalArgumentException for wrong parameters passed to a method
	public static final int NumberFormatException = 4;		///java.lang.NumberFormatException for converting numbers from strings or vice versa
	public static final int IOException = 5;			///java.io.IOException for open/create files, reading and writing
	public static final int UnsupportedEncodingException = 6;	///java.lang.UnsupportedEncodingException for converting string from byte array with wrong encoding.

	public static final int WrongSizeException = 50;		///given size != real size like UInt8Number should have a len of 1
	public static final int DataEmptyException = 51;		///byte[] returns null, etc
	public static final int OverflowException = 52;			///data value overflow
	public static final int InvalidDataValueException = 53;		///return bad data value such negative for unsigned integer
	public static final int IncorrectSignatureException = 54;	///signature is invalid
	public static final int RequiredTagMissingException = 55;	///missing a tag type in a profile class for output

	private	final int type_;

	/**
	 * Construct ICCProfileException object with a exception message.
	 * @param msg - string of message
	 */
	public ICCProfileException(String msg)
	{
		super(msg);
		this.type_ = 0;
	}

	/**
	 * Construct ICCProfileException with a message and a exception type.
	 * @param msg - message string
	 * @param type - type of exception of the static int numbers in this class.
	 */
	public ICCProfileException(String msg, final int type)
	{
		super(msg);
		this.type_ = type;
	}

	/**
	 * Return the current exception type number, or 0 if not assigned.
	 * @return exception type as int
	 */
	public int getType()
	{
		return this.type_;
	}
}
