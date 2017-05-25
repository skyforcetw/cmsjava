package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * ICC Profile type codes for profile flags.
 *
 * The profile flag can be embeded or independent.
 */
public class ProfileFlag extends UInt32Number
{
	public static final int EMBED_FLAG = 1; ///value_ & 1 == 1 if embedded
	public static final int IND_FLAG = 2; ///value_ & 2 == 2 if profile cannot be used independtly from the embedded color data
	
	public ProfileFlag()
	{
	}

	public ProfileFlag(byte[] byteArray) throws ICCProfileException
	{
		super(byteArray);
	}
	
	public ProfileFlag(byte[] byteArray, int offset) throws ICCProfileException
	{
		super(byteArray, offset);
	}

	public ProfileFlag(ProfileFlag copy)
	{
		super(copy);
	}
	
	public ProfileFlag(int value)
	{
		super(value);
	}
	
	public void setEmbeded()
	{
		setValue(intValue() | EMBED_FLAG);
	}

	public void setIndependent()
	{
		setValue(intValue() | EMBED_FLAG | IND_FLAG);
	}

	public boolean isEmbedded()
	{
		return (intValue() & EMBED_FLAG) == EMBED_FLAG;
	}

	public boolean isIndependent()
	{
		return (intValue() & IND_FLAG) == IND_FLAG;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		if (isEmbedded()) sb.append("Embedded,"); else sb.append("Not Embedded,");
		if (isIndependent()) sb.append("Independent"); else sb.append("Dependent");
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
			sb.append("<profileFlag>");
		else
			sb.append("<profileFlag name=\""+name+"\">");
		String ebs = isEmbedded()?"True":"False";
		sb.append("<flag name=\"embedded\" value=\""+ebs+"\"/>");
		String ids = isIndependent()?"True":"False";
		sb.append("<flag name=\"Independent\" value=\""+ids+"\"/>");
		sb.append("</profileFlag>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
