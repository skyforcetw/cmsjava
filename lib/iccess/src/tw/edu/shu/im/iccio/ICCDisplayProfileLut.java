package tw.edu.shu.im.iccio;

/**
 * N-component LUT-based display profile.
 */
public class ICCDisplayProfileLut extends ICCProfile
{
	public ICCDisplayProfileLut()
	{
		super();
	}

	public ICCDisplayProfileLut(ICCInputProfileLut copy) throws ICCProfileException
	{
		super(copy);
	}

	public ICCDisplayProfileLut(ICCProfileHeader header, ICCProfileTagTable tagTable)
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
				"A2B0","AToB0Tag",
				"B2A0","BToA0Tag"
			};
		return requiredTags;
	}
}
