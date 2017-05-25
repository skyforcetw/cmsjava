package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.Streamable;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * Abstract tag type is the base class that all tag types should extend.
 *
 * It implements the Streamable interface for byte array and data element conversion.
 * The fromByteArray method parses a byte array (usually from disk file) into
 * concrete data elements in a derived class, and toByteArray method serialises
 * the data elements into a byte array for output.
 */
public abstract class AbstractTagType implements Streamable
{
	public abstract void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException;

	public abstract byte[] toByteArray() throws ICCProfileException;

	public abstract String toXmlString(String name);

	public abstract String toXmlString();

	public abstract int size();
}
