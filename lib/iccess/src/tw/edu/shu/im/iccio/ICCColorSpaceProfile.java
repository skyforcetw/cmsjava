package tw.edu.shu.im.iccio;

/**
 * ColorSpace conversion Profile.
 */
public class ICCColorSpaceProfile extends ICCProfile
{
	public ICCColorSpaceProfile()
	{
		super();
	}

	public ICCColorSpaceProfile(ICCInputProfileLut copy) throws ICCProfileException
	{
		super(copy);
	}

	public ICCColorSpaceProfile(ICCProfileHeader header, ICCProfileTagTable tagTable)
	{
		super(header, tagTable);
	}

	/**
	 * Return the required tag type strings for this profile class.
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
				"chad","chromaticAdaptationTag",
				"B2A0","BToA0Tag",
				"A2B0","AToB0Tag"
			};
		return requiredTags;
	}
}
