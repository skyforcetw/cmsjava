package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * The ICC Profile type codes for platforms.
 *
 * There're currently four platforms specified: Apple, Microsoft, Sun, and
 * Silicon Graphics.  Each is represented by a tag value that maps the 4-letter
 * word.
 */
public class Platform extends UInt32Number
{
	public static final int APPLE = 0x4150504C; //'APPL'
	public static final int MICROSOFT = 0x4D534654; //'MSFT'
	public static final int SILICONGRAPHICS = 0x53474920; //'SGI '
	public static final int SUN = 0x53554E57; // 'SUNW'
	
	public Platform()
	{
	}

	public Platform(byte[] byteArray) throws ICCProfileException
	{
		super(byteArray);
	}
	
	public Platform(byte[] byteArray, int offset) throws ICCProfileException
	{
		super(byteArray, offset);
	}
	
	public Platform(Platform copy)
	{
		super(copy);
	}
	
	public Platform(int code)
	{
		super(code);
	}
	
	public void setApple()
	{
		setValue(APPLE);
	}

	public void setMicrosoft()
	{
		setValue(MICROSOFT);
	}

	public void setSiliconGraphics()
	{
		setValue(SILICONGRAPHICS);
	}

	public void setSun()
	{
		setValue(SUN);
	}

	public boolean isMicrosoft()
	{
		return intValue() == MICROSOFT;
	}

	public boolean isApple()
	{
		return intValue() == APPLE;
	}

	public boolean isSiliconGraphics()
	{
		return intValue() == SILICONGRAPHICS;
	}

	public boolean isSun()
	{
		return intValue() == SUN;
	}

	public String toString()
	{
		if (isMicrosoft()) return "MSFT";
		if (isApple()) return "APPL";
		if (isSun()) return "SUNW";
		if (isSiliconGraphics()) return "SGI ";
		return "NONE";
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
			sb.append("<platform>");
		else
			sb.append("<platform name=\""+name+"\">");
		sb.append(toString());
		sb.append("</platform>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
