package tw.edu.shu.im.iccio;

/**
 * Streamable is an interface for data types that need to convert data values from and to byte arrays.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * Streamable interface is used by classes that need to convert a data element to a byte array and vice versa.
 * The byte array can be loaded from a ICC profile disk file and converted to a particular data element
 * with the corresponding class that implements this interface, or can be saved to a profile disk file.
 * 
 * Interface methods:
 * fromByteArray(byte[], int offset, int len) - used to create a data element from a byte array.
 * toByteArray() - used to convert the data element to a byte array.
 */
public interface Streamable
{
	/**
	 * Parse a byte array and sort out the data elements required by the implemented class.
	 * @param asByteArray - the byte array to parse
	 * @param offset - from which byte to start with
	 * @param len - the number of bytes to parse.
	 */
	public void fromByteArray(byte[] asByteArray, int offset, int len) throws ICCProfileException;

	/**
	 * Convert the data elements contained in the implemented class to make a byte array.
	 * @return byte[] as the data elements.
	 */
	public byte[] toByteArray() throws ICCProfileException;

	/**
	 * Produce an XML fragment for the data object.
	 * @param name - attribute name for the element, useful when the element is a member of a struct. Not used if name is null.
	 * @return XML element as a String.
	 */
	public abstract String toXmlString(String name);

	/**
	 * Produce an XML fragment for the data object.
	 * @return XML element as a String.
	 */
	public abstract String toXmlString();
}
