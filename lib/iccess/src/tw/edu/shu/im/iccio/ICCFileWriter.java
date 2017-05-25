package tw.edu.shu.im.iccio;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

/**
 * ICCFileWriter implements the ICCFileOutput interface for writing ICC profile data to a disk file.
 *
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-27
 * 
 * This class is used to write ICC profile data objects into a file of ICC Profile specification v4.
 * It does writing to the file using the java.io.RandomAccessFile.
 *
 * To save a ICCProfile object, create an ICCFileWriter object and pass to the ICCProfile.save
 * method as the argument.  The close() method must be called after saving the profile data.
 */
public class ICCFileWriter implements ICCFileOutput
{
	private static Logger logger = Logger.getLogger(ICCFileWriter.class.getName());

	private	RandomAccessFile	fileHandle_;	//file to write to

	/**
	 * Constructor to create a given file for writing ICC profile data.
	 * @param filename - file path and name for the ICC profile.
	 * @exception ICCProfileException
	 */
	public ICCFileWriter(String filename) throws ICCProfileException
	{
		logger.finest("Enter ICCFileWriter('"+filename+"')");
		try
		{
			File file = new File(filename);
			if (file.exists())
			{
				logger.severe("ICC file exists, can't overwrite");
				throw new ICCProfileException("IOException: File '"+filename+"' exists!");
			}
			this.fileHandle_ = new RandomAccessFile(filename,"rw");
		}
		catch (IOException e)
		{
			logger.severe("IOException creating file");
			throw new ICCProfileException("Profile file creation exception:"+e.getMessage());
		}
		logger.finest("Leave ICCFileWriter");
	}

	/**
	 * Return the current opened RandomAccessFile object.
	 */
	public RandomAccessFile getFile()
	{
		return this.fileHandle_;
	}

	/**
	 * Locate file write pointer at a particular position in file.
	 * @param offset - position relative to the beginning of file at 0.
	 * @exception throws ICCProfileException if IOException occurs.
	 */
	public void seek( int offset ) throws ICCProfileException
	{
		try
		{
			this.fileHandle_.seek( offset );
		}
		catch (IOException e)
		{
			throw new ICCProfileException("IOException with seek: "+e.getMessage());
		}
	}

	/**
	 * Get the current file pointer in file.
	 * @return file pointer offset in file as a long int.
	 */
	public long location() throws ICCProfileException
	{
		try
		{
			return this.fileHandle_.getFilePointer();
		}
		catch (IOException e)
		{
			throw new ICCProfileException("IOException with getFilePointer: "+e.getMessage());
		}
	}

	/**
	 * Write a whole byte array to the file.
	 * @param data - byte[] to save
	 * @exception ICCProfileException
	 */
	public void write(byte[] data) throws ICCProfileException
	{
		logger.finest("Enter ICCFileWriter.write(byte[])");
		if (this.fileHandle_ == null)
		{
			logger.warning("Profile not created");
			throw new ICCProfileException("Profile file not created");
		}
		try
		{
			this.fileHandle_.write(data, 0, data.length);
		}
		catch (IOException e)
		{
			logger.severe("IOException writing to file: "+e.getMessage());
			throw new ICCProfileException("IOException writing to file: "+e.getMessage());
		}
		logger.finest("Leave ICCFileWriter.write(byte[])");
	}

	/**
	 * Write a byte array to file with padding to align on a given boundary.
	 * For instance, writeWithPadding(data, 4) will align the data on a 4-byte boundary.
	 * @param data - byte array to write to file
	 * @param alignBytes - number of bytes as a alignment boundary
	 */
	public void writeWithPadding(byte[] data, int alignBytes) throws ICCProfileException
	{
		logger.finest("Enter writeWithPadding");
		if (this.fileHandle_ == null)
			throw new ICCProfileException("profile file not created",ICCProfileException.IOException);
		try
		{
			this.fileHandle_.write(data, 0, data.length);
			//System.out.println("Data block: "+data.length);
			int n = data.length % alignBytes;
			if (n > 0)
			{
				byte[] padding = new byte[alignBytes - n];
				this.fileHandle_.write(padding, 0, padding.length);
				//System.out.println("  Padded bytes: "+padding.length);
			}
		}
		catch (IOException e)
		{
			throw new ICCProfileException("profile write exception:"+e.getMessage());
		}
	}

	/**
	 * Write part of a byte array to the file.
	 * @param data - byte[] to save
	 * @param index - from which byte to start writing
	 * @param len - number of bytes to write to the file
	 * @exception ICCProfileException
	 */
	public void write(byte[] data, int index, int len) throws ICCProfileException
	{
		logger.finest("Enter ICCFileWriter.write(byte[],index,len)");
		if (this.fileHandle_ == null)
		{
			logger.warning("Profile not created");
			throw new ICCProfileException("Profile file not created");
		}
		if (index < 0 || index >= data.length)
		{
			logger.severe("ICCFileWriter.write() index outside range");
			throw new ICCProfileException("Write index outside of data range");
		}
		if (index + len > data.length)
		{
			logger.severe("ICCFileWriter.write() len+index outside range");
			throw new ICCProfileException("Write index+len outside data range");
		}
		try
		{
			this.fileHandle_.write(data, index, len);
		}
		catch (IOException e)
		{
			logger.severe("IOException writing to file: "+e.getMessage());
			throw new ICCProfileException("IOException writing to file: "+e.getMessage());
		}
		logger.finest("Leave ICCFileWriter.write(byte[],index,len)");
	}

	/**
	 * Write a single byte to the current file position.
	 * @param singleByte as a singed byte value ranging from -128 to 127.
	 */
	public void write(byte singleByte) throws ICCProfileException
	{
		try
		{
			this.fileHandle_.write(singleByte);
		}
		catch (IOException e)
		{
			throw new ICCProfileException("profile write exception:"+e.getMessage());
		}
	}

	/**
	 * Write a signed short integer to the current file position.
	 * @param int2 - 2-byte int as signed short value ranging from -32768 to 32767.
	 */
	public void write(short int2) throws ICCProfileException
	{
		try
		{
			this.fileHandle_.writeShort(int2);
		}
		catch (IOException e)
		{
			throw new ICCProfileException("profile write exception:"+e.getMessage());
		}
	}

	/**
	 * Write a signed int to the current file position.
	 * @param int4 - 4-byte signed int.
	 */
	public void write(int int4) throws ICCProfileException
	{
		try
		{
			this.fileHandle_.writeInt(int4);
		}
		catch (IOException e)
		{
			throw new ICCProfileException("IOException writing int4");
		}
	}

	/**
	 * Write a signed long integer to the current file position.
	 * @param int8 - 8-byte signed long int.
	 */
	public void write(long int8) throws ICCProfileException
	{
		try
		{
			this.fileHandle_.writeLong(int8);
		}
		catch (IOException e)
		{
			throw new ICCProfileException("IOException writing int8");
		}
	}
	
	/**
	 * Write a single precision floating point number to the current file position.
	 * @param float4 - 4-byte floating point number as Java type float.
	 */
	public void write(float float4) throws ICCProfileException
	{
		try
		{
			this.fileHandle_.writeFloat(float4);
		}
		catch (IOException e)
		{
			throw new ICCProfileException("IOException writing float4: "+e.getMessage());
		}
	}
	
	/**
	 * Write a double precision floating point number to the current file position.
	 * @param float8 - 8-byte floating point number as Java type double.
	 */
	public void write(double float8) throws ICCProfileException
	{
		try
		{
			this.fileHandle_.writeDouble(float8);
		}
		catch (IOException e)
		{
			throw new ICCProfileException("IOException writing float8: "+e.getMessage());
		}
	}

	/**
	 * Close the data file.
	 * This is required after saving the ICC profile header, tag table, and tagged data blocks.
	 */
	public void close()
	{
		try
		{
			this.fileHandle_.close();
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * Static method to save a ICCProfile data object directly into a file.
	 * @param profile - ICCProfile object to save
	 * @param filename - name of file to save the profile data to
	 * @exception ICCProfileException
	 */
/*	public static void saveProfile(ICCProfile profile, String filename) throws ICCProfileException
	{
		logger.finest("Enter ICCFileWriter.saveProfile");

		ICCFileWriter writer = new ICCFileWriter(filename);
		writer.writeProfile(profile);

		logger.finest("Leave ICCFileWriter.saveProfile");
	}
*/
	/**
	 * Write the profile into the file.
	 * @param profile - ICCProfile object to save
	 */
/*	public void writeProfile(ICCProfile profile) throws ICCProfileException
	{
		logger.info("Enter writeProfile(ICCProfile)");
		if (this.fileHandle_ == null)
		{
			logger.severe("Profile file not created");
			throw new ICCProfileException("Profile file not created");
		}
		if (profile == null)
		{
			logger.severe("null param with ICCFileWriter.writeProfile()");
			return;
		}
		profile.getHeader().save(this);
		profile.getTagTable().save(this);
		//TODO: data elements here?
		logger.info("Leave writeProfile()");
	}
*/
}
