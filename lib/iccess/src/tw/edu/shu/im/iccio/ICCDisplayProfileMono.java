package tw.edu.shu.im.iccio;

/**
 * Monochrome Display Profile.
 */
public class ICCDisplayProfileMono extends ICCProfile
{
	public ICCDisplayProfileMono()
	{
		super();
	}

	public ICCDisplayProfileMono(ICCInputProfileLut copy) throws ICCProfileException
	{
		super(copy);
	}

	public ICCDisplayProfileMono(ICCProfileHeader header, ICCProfileTagTable tagTable)
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
				"kTRC","grayTRCTag"
			};
		return requiredTags;
	}
}
