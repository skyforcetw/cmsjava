package tw.edu.shu.im.iccio;

/**
 * ICCProfileOutput is an interface for ICCDataType-derived objects to save 
 * data elements to. The implementation is ICCProfileWriter class.
 */
public interface ICCProfileOutput
{
	public void seek(int offset) throws ICCProfileException;	///Move file pointer to the given position for reading or writing.
	public void write(byte[] data) throws ICCProfileException;	///Write the given byte array to the current position in file.
	public void write(byte[] data, int offset, int len) throws ICCProfileException;	///Write a number of bytes to file from a byte array starting at a given position.
	public void writeWithPadding(byte[] data, int alignBytes) throws ICCProfileException;	///Write a byte array to file and append zero bytes to align on the specified bounday.
}

