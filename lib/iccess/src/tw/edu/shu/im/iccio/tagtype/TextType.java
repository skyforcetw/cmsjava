package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

import java.io.UnsupportedEncodingException;

/**
 * TextType is a tag type with simple ASCII text.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The textType is a simple text structure that contains a 7-bit ASCII text string. The length of the string is obtained
 * by subtracting 8 from the element size portion of the tag itself. This string must be terminated with a 00h byte.
 * <code>
 * 0..3		4	'text' (74657874h) type signature
 * 4..7		4	reserved, must be set to 0
 * 8..end		a string of (element size - 8) 7-bit ASCII characters
 * </code>
 */
public class TextType extends AbstractTagType
{
	public static final int SIGNATURE = 0x74657874;
	
	private	Signature	signature_;
	private	byte[]		asciiBytes_;

	/**
	 * Construct a TextType object with empty text.
	 */
	public TextType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"text");
	}

	/**
	 * Construct a TextType object with a given text string.
	 * @param text - text string to set
	 */
	public TextType(String text) throws ICCProfileException
	{
		this.signature_ = new Signature("text");
		setText(text);
	}

	/**
	 * Construct a TextType object with a string to fit in a given size
	 * and padd with zeros or truncate the string as necessary.
	 * @param text - text string to set
	 * @param length - number of bytes to keep as fixed length
	 */
	public TextType(String text, int length) throws ICCProfileException
	{
		this.signature_ = new Signature("text");
		setText(text, length);
	}

	/**
	 * Construct a TextType object from a byte array starting at a position with a given number of bytes.
	 * @param byteArray - byte array to use
	 * @param offset - starting position in the byte array
	 * @param len - number of bytes to use
	 */
	public TextType(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		fromByteArray(byteArray, offset, len);
	}

	/**
	 * Construct a TextType object from a byte array.
	 * @param byteArray - a byte array that contains a whole TextType string.
	 */
	public TextType(byte[] byteArray) throws ICCProfileException
	{
		fromByteArray(byteArray, 0, 0);
	}

	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > byteArray.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x74657874)
			throw new ICCProfileException("TextType.fromByteArray(): incorrect signature:"+Integer.toHexString(signature_.intValue()), ICCProfileException.IncorrectSignatureException);

		if (len <= 0)
			len = byteArray.length - offset - 8;
		if (len <= 0)
			throw new ICCProfileException("array empty", ICCProfileException.WrongSizeException);
		this.asciiBytes_ = new byte[len];
		System.arraycopy(byteArray, offset + 8, this.asciiBytes_, 0, len);
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.asciiBytes_==null)
			throw new ICCProfileException("data not set", ICCProfileException.InvalidDataValueException);

		byte[] all = new byte[8 + this.asciiBytes_.length];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		System.arraycopy(this.asciiBytes_, 0, all, 8, this.asciiBytes_.length);

		return all;
	}
	
	public int size()
	{
		assert(this.asciiBytes_!=null);
		return 8 + this.asciiBytes_.length;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	/**
	 * Return the current text as a string.
	 * @return String of the text
	 * @exception UnsupportedEncodingException raised if text not ASCII.
	 */
	public String getText() throws ICCProfileException
	{
		try 
		{
			String s = new String(asciiBytes_, "ISO-8859-1");
			return s.trim();
		} 
		catch (UnsupportedEncodingException e) 
		{
			throw new ICCProfileException(e.getMessage(), ICCProfileException.UnsupportedEncodingException);
		}
	}

	/**
	 * Return the byte array to form this string.
	 */
	public byte[] getBytes()
	{
		return this.asciiBytes_;
	}

	/**
	 * Create a byte array large enough to hold the text.
	 * @param text - the string to set as the text.
	 */
	public void setText(String text)
	{
		this.asciiBytes_ = text.getBytes();
	}

	/**
	 * Assign the text from a whole byte array.
	 * @param textBytes - byte array containing the text
	 */
	public void setText(byte[] textBytes)
	{
		this.asciiBytes_ = textBytes;
	}

	/**
	 * Assign the text from part of a byte array.
	 * @param textBytes - byte array to copy text from
	 * @param offset - position the text starts in the byte array
	 * @param bytes - number of bytes to copy
	 */
	public void setText(byte[] textBytes, int offset, int bytes)
	{
		this.asciiBytes_ = new byte[bytes];
		//System.out.println(textBytes.length+","+offset+","+bytes);
		System.arraycopy(textBytes, offset, this.asciiBytes_, 0, bytes);
	}
	
	/**
	 * Return the number of bytes in the text string.
	 * @return int of the text length in bytes
	 */
	public int getLength()
	{
		if (this.asciiBytes_ != null)
			return this.asciiBytes_.length;
		return 0;
	}
	
	/**
	 * Create a byte array for exactly len number of bytes.
	 * If the text is shorter than the byte array, the rest blanks are zeros.
	 * If the text is longer, then it is truncated to fit the array.
	 * @param text - string to set as the text.
	 * @param len - number of bytes in the byte array.
	 */
	public void setText(String text, int len)
	{
		byte[] textbytes = text.getBytes();
		this.asciiBytes_ = new byte[len];
		int n = (textbytes.length > len) ? len : textbytes.length;
		System.arraycopy(textbytes,0,this.asciiBytes_,0,n);
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
			sb.append("<textType sig=\"text\">");
		else
			sb.append("<textType name=\""+name+"\" sig=\"text\">");
		sb.append(signature_.toXmlString());
		try
		{
			sb.append("<text><![CDATA[");
			sb.append(getText());
			sb.append("]]></text>");
		}
		catch (ICCProfileException e)
		{
			sb.append("<array dims=\"1\"><dim index=\"0\">");
			for (int i=0; i<asciiBytes_.length; i++)
			{
				if (i > 0)
					sb.append(',');
				sb.append(asciiBytes_[i]);
			}
			sb.append("</dim></array>");
		}
		sb.append("</textType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
