package tw.edu.shu.im.iccio;

/**
 * Three-component matrix-based input profile.
 */
public class ICCInputProfileMatrix extends ICCProfile
{
	public ICCInputProfileMatrix()
	{
		super();
	}

	public ICCInputProfileMatrix(ICCInputProfileLut copy) throws ICCProfileException
	{
		super(copy);
	}

	public ICCInputProfileMatrix(ICCProfileHeader header, ICCProfileTagTable tagTable)
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
				"rXYZ","redMatrixColumnTag",
				"gXYZ","greenMatrixColumnTag",
				"bXYZ","blueMatrixColumnTag",
				"rTRC","redTRCTag",
				"gTRC","greenTRCTag",
				"bTRC","blueTRCTag"
			};
		return requiredTags;
	}
}
