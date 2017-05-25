package tw.edu.shu.im.iccio;

/**
 * DeviceLink Profile.
 */
public class ICCDeviceLinkProfile extends ICCProfile
{
	public ICCDeviceLinkProfile()
	{
		super();
	}

	public ICCDeviceLinkProfile(ICCInputProfileLut copy) throws ICCProfileException
	{
		super(copy);
	}

	public ICCDeviceLinkProfile(ICCProfileHeader header, ICCProfileTagTable tagTable)
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
				"A2B0","AToB0Tag",
				"pseq","profileSequenceDescTag",
				"clrt","colorantTableTag",
				"clot","colorantTableOutTag",
				"cprt","copyrightTag"
			};
		return requiredTags;
	}
}
