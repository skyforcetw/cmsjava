package tw.edu.shu.im.iccio;

/**
 * NamedColor Profile.
 */
public class ICCNamedColorProfile extends ICCProfile
{
	public ICCNamedColorProfile()
	{
		super();
	}

	public ICCNamedColorProfile(ICCInputProfileLut copy) throws ICCProfileException
	{
		super(copy);
	}

	public ICCNamedColorProfile(ICCProfileHeader header, ICCProfileTagTable tagTable)
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
				"ncl2","namedColor2Tag"
			};
		return requiredTags;
	}
}
