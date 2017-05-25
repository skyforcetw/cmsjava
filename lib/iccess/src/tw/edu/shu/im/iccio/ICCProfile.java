package tw.edu.shu.im.iccio;

import tw.edu.shu.im.iccio.tagtype.AbstractTagType;
import tw.edu.shu.im.iccio.tagtype.S15Fixed16ArrayType;
import tw.edu.shu.im.iccio.tagtype.XYZType;
//import tw.edu.shu.im.iccio.tagtype.TextType;
import tw.edu.shu.im.iccio.tagtype.Tags;
import tw.edu.shu.im.iccio.tagtype.MultiLocalizedUnicodeType;
import tw.edu.shu.im.iccio.tagtype.TextDescriptionType;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.datatype.S15Fixed16Number;
import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.XYZNumber;

import java.util.logging.Logger;

/**
 * ICCProfile is the bag for a whole ICC profile data object including the
 * header, the tag table with or without the tagged data elements.
 *
 * There are three ways to create a ICCProfile object:
 * <ul><li>Construct an empty ICCProfile object for creating a new profile.
 * <li>Construct an ICCProfile from another ICCProfile object (copy).
 * <li>Construct an ICCProfile object with a header and a tag table.
 * </ul>
 * To create an ICCProfile object from a ICC file, use ICCProfileReader.
 * @see ICCProfileReader for details.
 *
 * To save an ICCProfile into a disk file, create an ICCFileWriter
 * object and then call the save method, for example:
 * <code>
 *	ICCProfile myprofile = new ICCProfile();
 *	//create the profile here
 *	//... ...
 *	ICCFileWriter writer = new ICCFileWriter("myprofile.icc");
 *	myprofile.save(writer);
 *	writer.close();
 */
public class ICCProfile implements Saveable
{
	private static Logger logger = Logger.getLogger(ICCProfile.class.getName());

	protected	ICCProfileHeader	iccHeader_;		//ICC file header object
	protected	ICCProfileTagTable	iccTagTable_;	//ICC Profile file tag table (index)

	/**
	 * Construct an empty ICCProfile object.
	 */
	public ICCProfile()
	{
	}

	/**
	 * Clone a ICCProfile object.
	 * @param copy ICCProfile object to deep copy into this one.
	 */
	public ICCProfile(ICCProfile copy) throws ICCProfileException
	{
		logger.finest("Enter ICCProfile(copy)");
		if (copy.iccHeader_ != null)
			this.iccHeader_ = new ICCProfileHeader(copy.iccHeader_);
		if (copy.iccTagTable_ != null)
			this.iccTagTable_ = new ICCProfileTagTable(copy.iccTagTable_);
		logger.finest("Leave ICCProfile(copy)");
	}

	/**
	 * Construct an ICCProfile object by available header and tag table with datasets.
	 * @param header - ICCProfileHeader object
	 * @param tagTable - ICCProfileTagTable object
	 */
	public ICCProfile(ICCProfileHeader header, ICCProfileTagTable tagTable)
	{
		logger.finest("Enter ICCProfile(ICCProfileHeader, ICCProfileTagTable)");
		setHeader(header);
		setTagTable(tagTable);
		logger.finest("Leave ICCProfile(ICCProfileHeader, ICCProfileTagTable)");
	}

	/**
	 * Getter to return current ICCProfileHeader object
	 */
	public ICCProfileHeader getHeader()
	{
		return this.iccHeader_;
	}

	/**
	 * Setter to assign an ICCProfileHeader object to this profile.
	 */
	public void setHeader(ICCProfileHeader header)
	{
		this.iccHeader_ = header;
	}

	/**
	 * Getter to return current ICCProfileTagTable object.
	 */
	public ICCProfileTagTable getTagTable()
	{
		return this.iccTagTable_;
	}

	/**
	 * Setter to assign an ICCProfileTagTable to this profile.
	 */
	public void setTagTable(ICCProfileTagTable tagTable)
	{
		this.iccTagTable_ = tagTable;
	}

	/**
	 * Output the whole icc profile dataset into an icc profile output stream.
	 * If the file exists, an exception will be thrown. If the required tag types
	 * are not all assigned, an exception will be thrown. This is based on profile spec v4.2.0.
	 * For earlier versions, it's better to disable validation.
	 * After the header and tag table, the tagged data chunks are saved with
	 * a 4-byte boundary padding.  Each data block is saved with first locating
	 * the file pointer according to the offset specified in the tag entry.
	 * Therefore, it is possible to overwrite a data block if two entries
	 * point to the same data block.
	 *
	 * @param writer - this should be an ICCFileWriter object.
	 * @param needValidate - true if validation required, otherwise false
	 */
	public void save(ICCFileOutput writer, boolean needValidate) throws ICCProfileException
	{
		logger.finest("Enter toFile(ICCFileWriter)");
		//validate the tags
		if (needValidate)
			validate();
		//logger.finest("this.iccHeader_:"+this.iccHeader_.size());
		this.iccHeader_.save(writer);
		this.iccTagTable_.save(writer);
		int n = this.iccTagTable_.getTagCount();
		for (int i = 0; i < n; i++)
		{
			ICCProfileTagEntry tag = this.iccTagTable_.getTag(i);
			AbstractTagType dt = tag.getData();
			byte[] data = dt.toByteArray();
			int offset = tag.getOffset();
			writer.seek( offset );
			writer.writeWithPadding(data, 4);
			//System.out.println("Data size="+data.length+", aligned="+writer.location());
		}
		int size = (int)writer.location();	//assume that no ICC profile is larger than an int can hold
		writer.seek(0);
		UInt32Number profilesize = new UInt32Number(size);
//		System.out.println("size final="+size);
		writer.write(profilesize.toByteArray());
		logger.finest("Leave toFile(ICCFileWriter)");
	}

	/**
	 * Save the profile without validation of required tags.
	 * This is appropriate for profiles loaded from earlier versions of profile spec.
	 * @param writer - ICCFileWriter object to write profile to.
	 */
	public void save(ICCFileOutput writer) throws ICCProfileException
	{
		save(writer, false);
	}

	/**
	 * Convert the whole profile into an XML document.
	 * This may produce too large a string in the memory.
	 * It may be necessary later to save the XML fragment in a StringWriter or text file directly.
	 * @param name - name attribute of the ICCProfile element
	 * @return XML document of this profile as a String.
	 */
	public String toXmlString(String name)
	{
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
		sb.append("<ICCProfile");
		if (name != null && name.length() > 0)
			sb.append(" name=\""+name+"\"");
		sb.append(">\n");
		sb.append(iccHeader_.toXmlString());
		sb.append(iccTagTable_.toXmlString());
		sb.append("<data>");
		for (int i=0; i<iccTagTable_.getTagCount(); i++)
		{
			AbstractTagType dt = iccTagTable_.getTag(i).getData();
			if (dt == null)
			{
				//System.err.println("Tag data is null");
				sb.append("<unknown-tag name=\""+String.valueOf(i)+"\"/>");
			}
			else
				sb.append(dt.toXmlString(String.valueOf(i)));
		}
		sb.append("</data>");
		sb.append("\n</ICCProfile>\n");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

	/**
	 * Make a StringTable by collecting all signatures of the tag table entries.
	 * @return a StringTable containing the signature strings of tag table entries.
	 */
	protected StringTable makeTagTableStrings() throws ICCProfileException
	{
		StringTable st = new StringTable();
		for (int i=0; i<iccTagTable_.getTagCount(); i++)
		{
			Signature sig = this.iccTagTable_.getTag(i).getSignature();
			st.addString(sig.getSignature());
		}
		return st;
	}

	/**
	 * Return the required tag type strings for this profile class.
	 * Note: This is actually not sufficient for profile validation, the real test should be invoked from derived classes!
	 * Override this method in the derived classes of ICCProfile for proper validation.
	 * @return String array of required tag types.
	 */
	protected String[] requiredTagStrings()
	{
		String[] requiredTags = new String[]
			{
				"desc","profileDescriptionTag",
				"wtpt","mediaWhitePointTag",
				"cprt","copyrightTag",
				"chad","chromaticAdaptationTag"
			};
		return requiredTags;
	}

	/**
	 * Validate this ICCProfile instance against the specification.
	 * It checks the header for completeness, and whether or not the
	 * required tags are included and correct.
	 *
	 * The four type tags required by all ICC profiles are:
	 * profileDescriptionTag "desc"
	 * mediaWhitePointTag "wtpt"
	 * copyrightTag "cprt"
	 * chromaticAdaptationTag "chad"
	 *
	 * @exception if validation fails for lacking a tagtype
	 */
	public void validate() throws ICCProfileException
	{
		if (this.iccHeader_ == null)
			throw new ICCProfileException("ICCProfile.validate(): Header not set",ICCProfileException.DataEmptyException);

		if (this.iccTagTable_ == null)
			throw new ICCProfileException("ICCProfile.validate(): TagTable not set",ICCProfileException.DataEmptyException);

		String[] rtags = requiredTagStrings();

		StringTable st = makeTagTableStrings();
		boolean hasError = false;
		StringBuffer error = new StringBuffer();
		for (int i=0; i<rtags.length; i+=2)
		{
			if (!st.contains(rtags[i]))
			{
				if (i > 0) error.append("; ");
				error.append(rtags[i+1]);
				hasError = true;
			}
		}
		if (hasError)
			throw new ICCProfileException(error.toString(), ICCProfileException.RequiredTagMissingException);
	}

	/**
	 * Append a tag to the current profile. The tag should be one of the definitions in the Tags class.
	 * The tagtype data must be an instantiated object of a AbstractTagType-derived class.
	 * @param tag - tag code defined in Tags class
	 * @param tagtype - object of a class derived from AbstractTagType
	 */
	public void addTagTypeEntry(int tag, AbstractTagType tagtype) throws ICCProfileException
	{
		ICCProfileTagEntry entry = new ICCProfileTagEntry(tag, tagtype);
		if (this.iccTagTable_ == null)
			this.iccTagTable_ = new ICCProfileTagTable();
		this.iccTagTable_.addTag(entry);
	}

	/**
	 * Update or add (if not existent) a tag of MultiLocalizedUnicodeType.
	 * The copyrightTag and profileDescriptionTag can use this method, for example:
	 *		MultiLocalizedUnicodeType mut = new MultiLocalizedUnicodeType();
	 *		byte[] unitext = ICCUtils.enUnicode("My copyright information");
	 *		mut.addText("EN","US",unitext);
	 *		profile.setMultiTextTag(Tags.copyrightTag, mut);
	 * @param tag - tag code defined in the Tags class
	 * @param texts - a MultiLocalizedUnicodeType object containing multiple texts of different languages
	 */
	public void setMultiTextTag(int tag, MultiLocalizedUnicodeType texts) throws ICCProfileException
	{
		if (this.iccTagTable_ == null)
			this.iccTagTable_ = new ICCProfileTagTable();
		for (int i=0; i<this.iccTagTable_.getTagCount(); i++)
		{
			Signature sig = this.iccTagTable_.getTag(i).getSignature();
			if (sig.intValue()==tag)
			{
				//found it, replace the text
				ICCProfileTagEntry entry = this.iccTagTable_.getTag(i);
				entry.setData(texts);
				return;
			}
		}
		addTagTypeEntry(tag, texts);
	}

	/**
	 * Set the mediaWhitePoint for the current profile.
	 * @param ciex,ciey,ciez - floating point values.
	 */
	public void setWhitePoint(double ciex, double ciey, double ciez) throws ICCProfileException
	{
		if (this.iccTagTable_ == null)
			this.iccTagTable_ = new ICCProfileTagTable();
		XYZNumber num = new XYZNumber(ciex, ciey, ciez);
		for (int i=0; i<this.iccTagTable_.getTagCount(); i++)
		{
			Signature sig = this.iccTagTable_.getTag(i).getSignature();
			if (sig.intValue()==Tags.mediaWhitePointTag)
			{
				//found it, replace the text
				ICCProfileTagEntry entry = this.iccTagTable_.getTag(i);
				XYZType xyz = new XYZType();
				xyz.addXYZNumber(num);
				entry.setData(xyz);
				return;
			}
		}
		XYZType xyz = new XYZType();
		xyz.addXYZNumber(num);
		addTagTypeEntry(Tags.mediaWhitePointTag, xyz);
	}

	/**
	 * Assign chromaticAdaptationTag to the profile.
	 * @param value - array of floating point values.
	 */
	public void setChromaticAdaptation(double[] value) throws ICCProfileException
	{
		if (this.iccTagTable_ == null)
			this.iccTagTable_ = new ICCProfileTagTable();
		S15Fixed16ArrayType sfa = new S15Fixed16ArrayType();
		sfa.addNumbers(value);
		for (int i=0; i<this.iccTagTable_.getTagCount(); i++)
		{
			Signature sig = this.iccTagTable_.getTag(i).getSignature();
			if (sig.intValue()==Tags.chromaticAdaptationTag)
			{
				//found it, replace the text
				ICCProfileTagEntry entry = this.iccTagTable_.getTag(i);
				entry.setData(sfa);
				return;
			}
		}
		addTagTypeEntry(Tags.chromaticAdaptationTag, sfa);
	}
}

