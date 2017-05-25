package tw.edu.shu.im.iccio;

/**
 * Abstract Profile.
 */
public class ICCAbstractProfile extends ICCProfile
{
	public ICCAbstractProfile()
	{
		super();
	}

	public ICCAbstractProfile(ICCInputProfileLut copy) throws ICCProfileException
	{
		super(copy);
	}

	public ICCAbstractProfile(ICCProfileHeader header, ICCProfileTagTable tagTable)
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
				"A2B0","AToB0Tag"
			};
		return requiredTags;
	}
}
