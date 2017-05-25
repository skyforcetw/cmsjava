package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * The ICC Profile type codes for profile or device classes.
 *
 * A device class can be a scanner, monitor, printer type as well as some other
 * abstract device classes like link, color conversion, abstract and named color.
 * A ProfileClass instance is a character-coded integer, a 4-byte integer with
 * each byte of it representing a character ASCII value.
 */
public class ProfileClass extends UInt32Number
{
	public static final int INPUT_DEVICE = 0x73636E72; ///'scnr' for scanner device
	public static final int DISPLAY_DEVICE = 0x6D6E7472; ///'mntr' for monitor device
	public static final int OUTPUT_DEVICE = 0x70727472; ///'prtr' for printer device
	public static final int DEVICE_LINK = 0x6C696E6B; ///'link' for devicelink class
	public static final int COLOR_CONV = 0x73706163; ///'spac' for color conversion class
	public static final int ABSTRACT = 0x61627374; ///'abst' for abstract class
	public static final int NAMED_COLOR = 0x6E6D636C; ///'nmcl' for named color class

	public ProfileClass()
	{
	}

	public ProfileClass(byte[] byteArray) throws ICCProfileException
	{
		super(byteArray);
	}
	
	public ProfileClass(byte[] byteArray, int offset) throws ICCProfileException
	{
		super(byteArray, offset);
	}

	public ProfileClass(int value)
	{
		super(value);
	}
	
	public ProfileClass(ProfileClass copy)
	{
		super(copy);
	}
	
	public void setInputDevice()
	{
		setValue(INPUT_DEVICE);
	}

	public void setDisplayDevice()
	{
		setValue(DISPLAY_DEVICE);
	}

	public void setOutputDevice()
	{
		setValue(OUTPUT_DEVICE);
	}

	public void setDeviceLink()
	{
		setValue(DEVICE_LINK);
	}

	public void setColorConversion()
	{
		setValue(COLOR_CONV);
	}

	public void setAbstract()
	{
		setValue(ABSTRACT);
	}

	public void setNamedColor()
	{
		setValue(NAMED_COLOR);
	}

	public boolean isInputDevice()
	{
		return intValue() == INPUT_DEVICE;
	}

	public boolean isDisplayDevice()
	{
		return intValue() == DISPLAY_DEVICE;
	}

	public boolean isOutputDevice()
	{
		return intValue() == OUTPUT_DEVICE;
	}

	public boolean isDeviceLink()
	{
		return intValue() == DEVICE_LINK;
	}

	public boolean isColorConversion()
	{
		return intValue() == COLOR_CONV;
	}

	public boolean isAbstract()
	{
		return intValue() == ABSTRACT;
	}
	
	public boolean isNamedColor()
	{
		return intValue() == NAMED_COLOR;
	}
	
	public String toString()
	{
		if (isInputDevice()) return "scnr";
		if (isDisplayDevice()) return "mntr";
		if (isOutputDevice()) return "prtr";
		if (isDeviceLink()) return "link";
		if (isColorConversion()) return "spac";
		if (isAbstract()) return "abst";
		if (isNamedColor()) return "nmcl";
		return "None";
	}


	/**
	 * Return XML element of this object.
	 * @param name - attribute name on element
	 * @return XML fragment as a string
	 */
	public String toXmlString(String name)
	{
		StringBuffer sb = new StringBuffer();
		if (name==null || name.length()<1)
			sb.append("<profileClass>");
		else
			sb.append("<profileClass name=\""+name+"\">");
		sb.append(toString());
		sb.append("</profileClass>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
