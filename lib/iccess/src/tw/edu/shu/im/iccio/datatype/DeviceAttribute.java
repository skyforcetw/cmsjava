package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * ICC Profile type codes for device attributes.
 *
 * Each attribute is represented by a certain bit pattern.
 * The whole device attribute is represented by a long integer with certain
 * bits set to match the corresponding attributes.
 */
public class DeviceAttribute extends UInt64Number
{
	public static final long TRANSPARENT = 1; ///value_ & 1 == 1 if transparent, 0 if reflective
	public static final long MATTE = 2; ///value & 2 == 2 if matte, glossy else
	public static final long NEGATIVE = 4; ///value & 4 == 4 if negative, 0 if positive media polarity
	public static final long BLACK_WHITE = 8; ///value_ & 8 == 8 if B and W, color if 0

	public DeviceAttribute()
	{
	}

	public DeviceAttribute(byte[] byteArray) throws ICCProfileException
	{
		super(byteArray);
	}
	
	public DeviceAttribute(byte[] byteArray, int offset) throws ICCProfileException
	{
		super(byteArray, offset);
	}

	public DeviceAttribute(DeviceAttribute copy)
	{
		super(copy);
	}
	
	public DeviceAttribute(long value)
	{
		super(value);
	}
	
	public void setTransparent()
	{
		setValue(longValue() | TRANSPARENT);
	}

	public void setReflective()
	{
		setValue(longValue() & ~TRANSPARENT);
	}

	public void setMatte()
	{
		setValue(longValue() | MATTE);
	}

	public void setGlossy()
	{
		setValue(longValue() & ~MATTE);
	}

	public void setNegative()
	{
		setValue(longValue() | NEGATIVE);
	}

	public void setPositive()
	{
		setValue(longValue() & ~NEGATIVE);
	}

	public void setBlackWhite()
	{
		setValue(longValue() | BLACK_WHITE);
	}

	public void setColor()
	{
		setValue(longValue() & ~BLACK_WHITE);
	}

	public boolean isTransparent()
	{
		return (longValue() & TRANSPARENT) == TRANSPARENT;
	}

	public boolean isReflective()
	{
		return (longValue() & TRANSPARENT) == 0;
	}

	public boolean isMatte()
	{
		return (longValue() & MATTE) == MATTE;
	}

	public boolean isGlossy()
	{
		return (longValue() & MATTE) == 0;
	}

	public boolean isNegative()
	{
		return (longValue() & NEGATIVE) == NEGATIVE;
	}

	public boolean isPositive()
	{
		return (longValue() & NEGATIVE) == 0;
	}

	public boolean isBlackWhite()
	{
		return (longValue() & BLACK_WHITE) == BLACK_WHITE;
	}

	public boolean isColor()
	{
		return (longValue() & BLACK_WHITE) == 0;
	}

	/**
	 * This will return a string for the whole attribute for display or testing purpose.
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if (isTransparent()) sb.append("Transparent,"); else sb.append("Reflective,");
		if (isMatte()) sb.append("Matte,"); else sb.append("Glossy,");
		if (isNegative()) sb.append("Negative,"); else sb.append("Positive,");
		if (isColor()) sb.append("Color"); else sb.append("Black & White");
		return sb.toString();
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
			sb.append("<deviceAttribute>");
		else
			sb.append("<deviceAttribute name=\""+name+"\">");
		String vs = isTransparent()?"1":"0";
		String s = isTransparent()?"Transparent":"Reflective";
		sb.append("<field bit=\"0\" value=\""+vs+"\">"+s+"</field>");
		s = isMatte()?"Matte":"Glossy";
		vs = isMatte()?"1":"0";
		sb.append("<field bit=\"1\" value=\""+vs+"\">"+s+"</field>");
		s = isNegative()?"Negative":"Positive";
		vs = isNegative()?"1":"0";
		sb.append("<field bit=\"2\" value=\""+vs+"\">"+s+"</field>");
		s = isColor()?"Color":"Black White";
		vs = isColor()?"0":"1";
		sb.append("<field bit=\"3\" value=\""+vs+"\">"+s+"</field>");
		sb.append("</deviceAttribute>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
