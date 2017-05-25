package tw.edu.shu.im.iccio;

import tw.edu.shu.im.iccio.datatype.UInt32Number;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Logger;

/**
 * ICCProfileReader reads ICC Profile data file and parses the content to make an ICCProfile object.
 * @author Ted Wen
 * @version 0.1
 * @update 2006-11-03
 *
 * It can be used to read the whole ICC profile from a disk file, or to read only the header
 * or tag table.
 * A static method is provided for a quick load and construction of an ICCProfile object.
 * To read only the header, tag table, and selected data chunks, an instance of this
 * class must be constructed first.
 * @see ICCProfileHeader for header structure.
 * @see ICCProfileTagTable for tag table structure.
 * @see ICCProfileTagEntry for the structure of a tag record.
 *
 * <ul>
 *	<li>Load a ICC profile from a file without keeping the instance of ICCProfileReader:
 *	<code>	ICCProfile profile = ICCProfileReader.loadProfile("example.icc");</code>
 *  <li>Create an instance of ICCProfileReader for reading piece by piece:
 * <code>
 *		ICCProfileReader reader = new ICCProfileReader("example.icc");
 *		ICCProfileHeader header = reader.readProfileHeader();
 *		ICCProfileTagTable tags = reader.readProfileTagTable();
 *		for (int i=0; i<tags.getEntryCount(); i++) {
 *			ICCProfileTagEntry tag = tags.getTag(i);
 *			byte[] data = reader.readTagData(tag);
 *		}
 *		reader.close();
 * </code>
 *
 * This class uses the java.io.RandomAccessFile for file I/O, so the file can only be
 * a disk file rather than a network stream.
 */
public class ICCProfileReader
{
	private static Logger logger = Logger.getLogger(ICCProfileReader.class.getName());

	private	ICCProfile			profile_;		//ICCProfile object loaded from file
	private	String				filename_;
	private	RandomAccessFile	fileHandle_;

	/**
	 * Construct an ICCProfileReader object by loading the data from the given file.
	 * @param filename - ICC Profile filename.
	 */
	public ICCProfileReader(String filename) throws ICCProfileException
	{
		logger.finest("Enter ICCProfileReader("+filename+")");
		this.filename_ = filename;
		//open the ICC profile file
		try
		{
			fileHandle_ = new RandomAccessFile(filename, "r");
		}
		catch (IOException e)
		{
			logger.severe("IOException opening '"+filename+"':"+e.getMessage());
			throw new ICCProfileException("IOException opening '"+filename+"':"+e.getMessage(), ICCProfileException.IOException);
		}
		logger.finest("Leave ICCProfileReader("+filename+")");
	}

	/**
	 * Getter to return the loaded profile object. This is only available after a successful loading.
	 */
	public ICCProfile getProfile()
	{
		return this.profile_;
	}

	/**
	 * Getter to return the current filename.
	 */
	public String getFilename()
	{
		return this.filename_;
	}

	/**
	 * Getter to return the current RandomAccessFile handle.
	 */
	public RandomAccessFile getFile()
	{
		return this.fileHandle_;
	}

	/**
	 * Close the reader.
	 * It is recommended to always close the file after use.
	 */
	public void close()
	{
		try
		{
			this.fileHandle_.close();
			this.fileHandle_ = null;
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * Static method to load an ICC profile from a disk file.
	 * 
	 * @param filename - Path and name of the file which is an ICC profile.
	 * @return ICCProfile object containing the loaded ICC profile object.
	 * @exception ICCProfileException
	 */
	public static ICCProfile loadProfile(String filename) throws ICCProfileException
	{
		ICCProfileReader reader = new ICCProfileReader(filename);
		reader.readProfile();
		reader.close();
		return reader.getProfile();
	}

	/**
	 * Read ICC profile header. The file must first be opened in the constructor.
	 * @return ICCProfileHeader object read from the profile file.
	 * @exception ICCProfileException
	 */
	public ICCProfileHeader readProfileHeader() throws ICCProfileException
	{
		if (this.fileHandle_ == null)
		{
			throw new ICCProfileException("Profile file not open yet.");
		}
		logger.finest("Enter readProfileHeader()");
		// allocate memory for ICCProfileHeader parsing
		byte[] byteArray = new byte[ICCProfileHeader.ICC_PROFILE_HEADER_SIZE];
		if (byteArray == null || byteArray.length < ICCProfileHeader.ICC_PROFILE_HEADER_SIZE)
		{
			logger.severe("out-of-memory while allocating header");
			throw new ICCProfileException("ICCProfileReader.load failed to allocate memory to read profile header");
		}
		// read header bytes from file
		try
		{
			this.fileHandle_.seek(0);
			int n = this.fileHandle_.read(byteArray);
			if (n != ICCProfileHeader.ICC_PROFILE_HEADER_SIZE)
			{
				logger.severe("invalid header size");
				throw new ICCProfileException("ICCProfileReader.load exception: invalid header size:"+n);
			}
		}
		catch (IOException e)
		{
			logger.severe("IOException while reading profile header");
			throw new ICCProfileException("IOException reading header: "+e.getMessage());
		}
		// parse header into ICCFileHeader object
		ICCProfileHeader header = new ICCProfileHeader(byteArray);
		//logger.finest("ICCProfileHeader loaded");
		logger.finest("Leave readProfileHeader()");
		return header;
	}

	/**
	 * Read the Tag table into ICCProfileTagTable object. The file must first be opened in the constructor.
	 * If the file is not open, an ICCProfileException is thrown with "Profile file not open yet".
	 * @return ICCProfileTagTable object read from the profile file.
	 * @exception ICCProfileException
	 */
	public ICCProfileTagTable readProfileTagTable() throws ICCProfileException
	{
		if (this.fileHandle_ == null)
		{
			throw new ICCProfileException("Profile file not open yet.");
		}
		logger.finest("Enter readTagTable()");
		// read number of tags in the tag table, a 4-byte int in front of the tag table
		byte[] byteArray = new byte[UInt32Number.SIZE];
		try
		{
			this.fileHandle_.seek( ICCProfileHeader.ICC_PROFILE_HEADER_SIZE );
			int n = this.fileHandle_.read(byteArray);
			if (n != UInt32Number.SIZE)
			{
				logger.severe("invalid size of the number of tags");
				throw new ICCProfileException("ICCProfileReader.load exception: invalid size of the number of tags");
			}
		}
		catch (IOException e)
		{
			logger.severe("IOException reading tag table size");
			throw new ICCProfileException("IOException reading tag table size int");
		}
		// make it an UInt32Number for the big-endian int
		UInt32Number ec = new UInt32Number(byteArray);
		// total number of tag entries in the tag table
		int tagTabEntryCount = ec.intValue();
		// compute the total bytes of the tag table
		int tagTabSize = tagTabEntryCount * ICCProfileTagEntry.SIZE;
		// read the whole tag table into byte array
		byteArray = new byte[tagTabSize];
		if (byteArray == null || byteArray.length < tagTabSize)
		{
			logger.severe("out-of-memory while allocating tag table");
			throw new ICCProfileException("ICCProfileReader.load caused out-of-memory exception while allocating tag table");
		}
		try
		{
			int n = this.fileHandle_.read(byteArray);
			if (n != tagTabSize)
			{
				logger.severe("invalid size of the tag table");
				throw new ICCProfileException("ICCProfileReader.load caused exception: invalid size of tag table");
			}
		}
		catch (IOException e)
		{
			logger.severe("IOException reading tag table");
			throw new ICCProfileException("IOException reading tag table:"+e.getMessage());
		}
		//parse the tag table into ICCFileTagTable object
		ICCProfileTagTable iccTagTable = new ICCProfileTagTable(byteArray);
		logger.finest("Leave readTagTable()");
		return iccTagTable;
	}

	/**
	 * Read the whole profile from the open random access file.
	 * The returned ICCProfile object contains the header, tag table and the tagged data elements.
	 * Call getProfile() later to get the instance of ICCProfile created in this method.
	 * @return ICCProfile - object containing the ICC profile header, tag table and data elements.
	 * @exception ICCProfileException
	 */
	public ICCProfile readProfile() throws ICCProfileException
	{
		logger.finest("Enter readProfile()");

		if (this.fileHandle_ != null)
		{
			ICCProfileHeader header = readProfileHeader();
			int version = header.getProfileVersion();	//UPDATE: for version compatibility
			ICCProfileTagTable tagTable = readProfileTagTable();
			try
			{
				for (int i=0; i<tagTable.getTagCount(); i++)
				{
					ICCProfileTagEntry entry = tagTable.getTag(i);
					int offset = entry.getOffset();
					//int len = ICCProfileTagEntry.alignDataSize(entry.getSize());
					int len = entry.getSize();	//actual size without padding
					byte[] chunk = new byte[len];
					this.fileHandle_.seek( offset );
					this.fileHandle_.read(chunk);
					entry.setData(ICCTagFactory.createTagData(chunk, version));
					//System.out.println("Chunk size="+entry.getData());
					logger.finest("Tag "+i+", data size="+chunk.length);
				}
			}
			catch (IOException e)
			{
				throw new ICCProfileException("profile read exception:"+e.getMessage());
			}
			this.profile_ = new ICCProfile(header, tagTable);
			logger.finest("Leave readProfile()");
			return this.profile_;
		}
		else
		{
			logger.warning("Call readProfile before opening a file");
			throw new ICCProfileException("Profile file not open yet");
		}
		//logger.finest("Leave readProfile() with error");
	}

	/**
	 * Read a tagged data block from the open file.
	 * @param tag - ICCProfileTagEntry object specifying what and where to read the tagged data.
	 * @return byte array of the tagged data object.
	 * @exception ICCProfileException
	 */
	public byte[] readTagData(ICCProfileTagEntry tag) throws ICCProfileException
	{
		logger.finest("Enter readData(tag)");
		if (this.fileHandle_ == null)
		{
			logger.warning("call readData before opening file");
			throw new ICCProfileException("Profile file not open yet.",ICCProfileException.IOException);
		}
		if (tag == null) 
		{
			logger.warning("call readTagData with null tag entry");
			return null;
		}
		int offset = tag.getOffset();
		int len = tag.getSize();	//ICCProfileTagEntry.alignDataSize(tag.getSize()); //need align for writing only
		byte[] data = new byte[len];
		try
		{
			this.fileHandle_.seek( offset );
			this.fileHandle_.read( data );
		}
		catch (IOException e)
		{
			throw new ICCProfileException("profile read exception:"+e.getMessage(),ICCProfileException.IOException);
		}
		logger.finest("Leave readData()");
		return data;
	}
}
