package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * ICC Profile type codes for render intent.
 *
 * The allowed render intents are perceptual, media relative, saturation and
 * absolute.
 */
public class RenderIntent extends UInt32Number
{
	public static final int PERCEPTUAL = 0;
	public static final int MEDIA_RELATIVE = 1;
	public static final int SATURATION = 2;
	public static final int ABSOLUTE = 3;
	
	public RenderIntent()
	{
	}

	public RenderIntent(byte[] byteArray) throws ICCProfileException
	{
		super(byteArray);
	}
	
	public RenderIntent(byte[] byteArray, int offset) throws ICCProfileException
	{
		super(byteArray, offset);
	}
	
	public RenderIntent(RenderIntent copy)
	{
		super(copy);
	}
	
	public RenderIntent(int value)
	{
		super(value);
	}
	
	public void setPerceptual()
	{
		setValue(PERCEPTUAL);
	}

	public void setMediaRelative()
	{
		setValue(MEDIA_RELATIVE);
	}

	public void setSaturation()
	{
		setValue(SATURATION);
	}

	public void setAbsolute()
	{
		setValue(ABSOLUTE);
	}

	public boolean isPerceptual()
	{
		return intValue() == PERCEPTUAL;
	}

	public boolean isMediaRelative()
	{
		return intValue() == MEDIA_RELATIVE;
	}

	public boolean isSaturation()
	{
		return intValue() == SATURATION;
	}

	public boolean isAbsolute()
	{
		return intValue() == ABSOLUTE;
	}

	public String toString()
	{
		if (isPerceptual()) return "Perceptual";
		if (isSaturation()) return "Saturation";
		if (isAbsolute()) return "Absolute";
		if (isMediaRelative()) return "Media Relative";
		return "Unknown";
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
			sb.append("<renderIntent>");
		else
			sb.append("<renderIntent name=\""+name+"\">");
		sb.append(toString());
		sb.append("</renderIntent>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
