package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.ICCProfileException;

import java.io.UnsupportedEncodingException;

/**
 * Signature contains a 4-letter string represented as a 4-byte integer.
 */
public class Signature extends UInt32Number
{
	/**
	 * Construct an empty Signature instance.
	 */
	public Signature()
	{
	}

	/**
	 * Construct a Signature instance from another Signature instance.
	 * @param copy - the Signature instance to clone from.
	 */
	public Signature(Signature copy)
	{
		super(copy);
	}
	
	public Signature(int value)
	{
		super(value);
	}

	public Signature(long value) throws ICCProfileException
	{
		super(value);
	}
	
	/**
	 * Construct an ICCBaseTag object.
	 * @param byteArray - byte array containing the tag value.
	 */
	public Signature(byte[] byteArray) throws ICCProfileException
	{
		super(byteArray);
	}
	
	public Signature(byte[] byteArray, int offset) throws ICCProfileException
	{
		super(byteArray, offset);
	}
	
	public Signature(String asString) throws ICCProfileException
	{
		setSignature(asString);
	}

	/**
 	 * Set the current signature as the the given string.
	 * If the string is less than 4 characters long, it will be padded with spaces.
	 * If the string is longer than 4 characters, it will be truncated to keep the first 4 characters.
	 */
	public void setSignature(String sig) throws ICCProfileException
	{
		while (sig.length() < size())
			sig = sig + " ";
		byte[] ba = sig.getBytes();
		fromByteArray(ba, 0, size());
	}

	/**
	 * This method returns a char string of the signature. It's different from toString() which returns the string of the number.
	 * @return string of the signature like "curv".
	 */
	public String getSignature() throws ICCProfileException
	{
		try {
			byte[] ba = toByteArray();
			String s = new String(ba, "ISO-8859-1");
			return s.trim();
		} catch (UnsupportedEncodingException e) {
			throw new ICCProfileException(e.getMessage(), ICCProfileException.UnsupportedEncodingException);
		}
	}


	/**
	 * Return XML element of this object.
	 * @param name - attribute name on element
	 * @return XML fragment as a string
	 */
	public String toXmlString(String name)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<signature");
		if (name!=null && name.length() > 0)
			sb.append(" name=\""+name+"\"");
		sb.append(" hex=\""+Integer.toHexString(intValue())+"\">");
		try
		{
			sb.append(getSignature());
		}
		catch (ICCProfileException e)
		{
			sb.append("None");
		}
		sb.append("</signature>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}

