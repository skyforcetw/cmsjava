package tw.edu.shu.im.iccio;

import tw.edu.shu.im.iccio.datatype.UInt32Number;

import java.util.logging.*;
import java.util.ArrayList;

/**
 * ICCProfileTagTable wraps the ICC Profile tag table.
 *
 * A Tag Table contains a list of tag records each contains three 32-bit integers.
 * In an ICC profile file, the tag table starts immediately after the header which 128 bytes
 * in size.  The first 4 bytes specify how many tag entries there are in the table.
 * To create an ICC profile from scratch, it is necessary to create an empty ICCProfileTagTable
 * and add the number of tag entries which are created as ICCProfileTagEntry objects.
 * To load a tag table from an ICC profile file, use the ICCProfileReader to read the
 * whole profile or only the tag table as required.
 */
public class ICCProfileTagTable implements Saveable
{
	private static Logger logger = Logger.getLogger(ICCProfileTagTable.class.getName());

	private ArrayList	tags_;	//list of tags of ICCProfileTagEntry class
	private	boolean		hasAddress_;	//tag entry has valid addresses

	/**
	 * Empty constructor
	 */
	public ICCProfileTagTable()
	{
		logger.finest("Enter ICCProfileTagTable() constructor");
		tags_ = new ArrayList();
	}

	/**
	 * Clone a ICCProfileTagTable object.
	 * @copy - ICCProfileTagTable object to shallow copy into this one.
	 */
	public ICCProfileTagTable(ICCProfileTagTable copy)
	{
		logger.finest("Enter ICCProfileTagTable(copy) constructor");
		this.tags_ = new ArrayList(copy.tags_.size());
		for (int i=0; i<this.tags_.size(); i++)
		{
			this.tags_.add(copy.tags_.get(i));
		}
		this.hasAddress_ = copy.hasAddress_;
		logger.finest("Leave ICCProfileTagTable(copy) constructor");
	}

	/**
	 * Construct an ICCProfileTagTable object from a byte array.
	 * @param filename - ICC Profile filename
	 */
	public ICCProfileTagTable(byte[] byteArray) throws ICCProfileException
	{
		logger.finest("Enter ICCProfileTagTable(byte[]) constructor");
		fromByteArray(byteArray, 0, 0);
		logger.finest("Leave ICCProfileTagTable(byte[]) constructor");
	}

	/**
	 * Follow the ICCDataObject interface method. It parses the byte array into a list of tag entries. Each tag entry 
	 * will be an object of ICCProfileTagEntry class.  The byte array contains tag entries only starting from the first entry.
	 * @param byteArray - byte array containing the tag table without the preceeding count number
	 * @offset, len - not used really
	 * @exception ICCProfileException
	 */
	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		logger.fine("Enter ICCProfileTagTable.fromByteArray(byte["+byteArray.length+"],"+offset+","+len+")");
		if (len > 0)
		{
			if (len % ICCProfileTagEntry.SIZE != 0)
			{
				logger.severe("len for byteArray is not a multiple of ICCProfileTagEntry.SIZE");
				throw new ICCProfileException("byteArray.length is not a multiple of ICCProfileTagEntry.SIZE");
			}
		}
		else if (byteArray.length % ICCProfileTagEntry.SIZE != 0)
		{
			logger.severe("byteArray.length("+byteArray.length+") is not a multiple of ICCProfileTagEntry.SIZE("+ICCProfileTagEntry.SIZE+")");
			throw new ICCProfileException("byteArray.length is not a multiple of ICCProfileTagEntry.SIZE");
		}
		int ncount = byteArray.length / ICCProfileTagEntry.SIZE;
		if (ncount <= 0)
		{
			logger.severe("number of tags <= 0");
			throw new ICCProfileException("Count=0 while ICCFileTagFable.fromByteArray()");
		}
		this.tags_ = new ArrayList(ncount);
		for (int i=0; i < ncount; i++)
		{
			logger.finest("create ICCProfileTagEntry(byteArray, "+offset+")");
			ICCProfileTagEntry tag = new ICCProfileTagEntry(byteArray, offset);
			logger.finest("\tEntry "+i+": offset="+tag.getOffset()+",size="+tag.getSize()); //TODO
			this.tags_.add((Object)tag);
			offset += ICCProfileTagEntry.SIZE;
			if (offset > byteArray.length)
			{
				logger.severe("offset("+offset+") > byteArray.length("+byteArray.length+")");
				throw new ICCProfileException("ICCProfileTagTable.fromByteArray() byte array too small");
			}
		}
		this.hasAddress_ = true;
		logger.fine("Leave ICCProfileTagTable.fromByteArray(...)");
	}

	/**
	 * Implements the Saveable interface to write data elements to a byte stream.
	 * This method will write the whole tag table (only the tag entries without the proceeding count number) to the stream.
	 * @param dos - DataOutputStream to write to.
	 * @exception - ICCProfileException
	 */
	public void save(ICCFileOutput out) throws ICCProfileException
	{
		logger.finest("Enter ICCProfileTagTable.save(out)");
		//need compute absolute addresses of data blocks before writing
		if (tags_ == null || tags_.size() < 1)
			throw new ICCProfileException("No tag table entries",ICCProfileException.InvalidDataValueException);
		
		if (!hasAddress_)
			computeAddress();
		
		logger.finest("tag table to stream count="+tags_.size());
		// save number of entries preceeding the tag table.
		UInt32Number entries = new UInt32Number(tags_.size());
		byte[] eb = entries.toByteArray();
		out.write(eb);

		for (int i=0; i<this.tags_.size(); i++)
		{
			ICCProfileTagEntry tag = (ICCProfileTagEntry)(this.tags_.get(i));
			byte[] tagbytes = tag.toByteArray();
			out.write(tagbytes);
		}
		logger.finest("Leave ICCProfileTagTable.save(out)");
	}

	/**
	 * Compute the address of each tag entry if the tag entries are appended for writing.
	 */
	public void computeAddress()
	{
		logger.finest("Enter computeAddress()");
		int addr = ICCProfileHeader.ICC_PROFILE_HEADER_SIZE + UInt32Number.SIZE + this.tags_.size() * ICCProfileTagEntry.SIZE;

		for (int i=0; i<this.tags_.size(); i++)
		{
			ICCProfileTagEntry e = (ICCProfileTagEntry)this.tags_.get(i);
			e.setOffset(addr);
			addr += e.getAlignedSize();
		}
		logger.finest("Leave computeAddress()");
	}

	public int getTagCount()
	{
		return this.tags_.size();
	}
/* //this method is useless as tag size can't be set
	public void setTagCount(int tagCount)
	{
		this.tags_ = new ArrayList(tagCount);
	}
*/
	public void addTag(ICCProfileTagEntry tag)
	{
		logger.finest("Enter addTag(tag)");
		if (this.tags_ == null)
		{
			this.tags_ = new ArrayList();
		}
		this.tags_.add((Object)tag);
		logger.finest("Leave addTag(tag), tags_.size="+this.tags_.size());
	}

	public void addTag(int signature, int offset, int size) throws ICCProfileException
	{
		logger.finest("Enter addTag("+signature+","+offset+","+size+")");
		ICCProfileTagEntry tag = new ICCProfileTagEntry(signature, offset, size);
		this.tags_.add((Object)tag);
		logger.finest("Leave addTag(,,),tags.size="+tags_.size());
	}

	public void clear()
	{
		this.tags_ = new ArrayList();
	}

	/**
	 * Return the same as getTagCount().
	 */
	public int size()
	{
		return this.tags_.size();
	}

	/**
	 * Returns a ICCProfileTagEntry reference by index.
	 * @param index - which tag entry to retrieve
	 * @return ICCProfileTagEntry object or null if index is out of bound.
	 */
	public ICCProfileTagEntry getTag(int index)
	{
		if (index < this.tags_.size())
		{
			return (ICCProfileTagEntry)(this.tags_.get(index));
		}
		return null;
	}

	/**
	 * Return number of tags in the first line and every tag elements in the following lines.
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(this.tags_.size());sb.append("\r\n");
		for (int i=0; i<this.tags_.size(); i++) 
		{
			ICCProfileTagEntry tag = (ICCProfileTagEntry)(this.tags_.get(i));
			sb.append(tag.toString());
			sb.append("\r\n");
		}
		return sb.toString();
	}
		
	/**
	 * Convert the Tag table into an XML fragment.
	 * @param name - name attribute of the tag entry element
	 * @return XML document of this profile as a String.
	 */
	public String toXmlString(String name)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<tagtable");
		if (name != null && name.length() > 0)
			sb.append(" name=\""+name+"\">");
		else
			sb.append(">");
		for (int i=0; i<tags_.size(); i++)
		{
			ICCProfileTagEntry tag = (ICCProfileTagEntry)(tags_.get(i));
			sb.append(tag.toXmlString(String.valueOf(i)));
		}
		sb.append("</tagtable>");
		return sb.toString();
	}
	
	public String toXmlString()
	{
		return toXmlString(null);
	}
}
