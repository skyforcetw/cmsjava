package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * ColorantTableType is a tag type to define a set of colorants with names and PCS values.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * This is identify the colorants used in the profile by a unique name and set of XYZ or L*a*b*
 * values to give the colorant an unambiguous value. The first colorant listed is the colorant of the first device
 * channel of a lut tag. The second colorant listed is the colorant of the second device channel of a lut tag, and so on.
 * This tag is optional.
 * 
 * Data elements:
 * Position       Bytes        Content                           Type
 * 0..3				4			'clrt'      (0x636c7274)
 * 4..7				4			reseved 0
 * 8..11			4			Count of colorants(n)						UInt32Number
 * 12..43			32			1st colorant name(asciiz,0 padded)			ASCII
 * 44..49			6			PCS values of 1st colorant					UInt16Number[3]
 * 50..(49+38(n-1))	38(n-1)		other colorants if n>1
 */
public class ColorantTableType extends AbstractTagType
{
	public static final int SIGNATURE = 0x636c7274;
	
	private	Signature		signature_;
	private	UInt32Number		numColorants_;		//count of colorants
	private	TextType[]		colorantNames_;		//array of colorant names as 38-char ASCII strings each
	private	UInt16Number[][]	pcsValues_;		//array of PCS values of these colorants

	/**
	 * Construct an empty ColorantTableType object for building new table.
	 */
	public ColorantTableType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"clrt");
	}
	
	/**
	 * Construct a ColorantTableType from a byte array that contains the table.
	 * @param bytes - the byte array
	 */
	public ColorantTableType(byte[] bytes) throws ICCProfileException
	{
		fromByteArray(bytes, 0, 0);
	}

	public void fromByteArray(byte[] bytes, int offset, int len) throws ICCProfileException
	{
		if (bytes == null)
			throw new ICCProfileException("ColorantTableType.fromByteArray:byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > bytes.length)
			throw new ICCProfileException("ColorantTableType.fromByteArray:index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(bytes, offset);
		if (this.signature_.intValue() != 0x636c7274)
			throw new ICCProfileException("ColorantTableType.fromByteArray: signature not correct!");

		this.numColorants_ = new UInt32Number(bytes, offset + 8);
		int count = this.numColorants_.intValue();
		if (count <= 0)
			throw new ICCProfileException("ColorantTableType.fromByteArray: Count of Colorants is 0");
		if (bytes.length < count * 38 + offset + 12)
			throw new ICCProfileException("ColorantTableType.fromByteArray: not enough bytes in array");

		this.colorantNames_ = new TextType[count];
		this.pcsValues_ = new UInt16Number[count][3];
		int idx = offset + 12;
		for (int i=0; i<count; i++)
		{
			this.colorantNames_[i] = new TextType();
			this.colorantNames_[i].setText(bytes, idx, 32);
			idx += 32;
			for (int k=0; k<3; k++)
			{
				this.pcsValues_[i][k] = new UInt16Number(bytes, idx);
				idx += 2;
			}
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (numColorants_==null || colorantNames_==null || pcsValues_==null)
			throw new ICCProfileException("ColorantTableType.toByteArray:data not set",ICCProfileException.InvalidDataValueException);

		int n = this.numColorants_.intValue();
		int len = 12 + 38 * n;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.numColorants_);
		int idx = 12;
		for (int i=0; i<n; i++)
		{
			byte[] namebytes = this.colorantNames_[i].getBytes();
			if (namebytes.length != 32)
				System.err.println("ColorantTableType.toByteArray(): colorant name not 32 bytes");
			System.arraycopy(namebytes, 0, all, idx, 32);
			idx += 32;
			for (int k=0; k<3; k++)
			{
				ICCUtils.appendByteArray(all, idx, this.pcsValues_[i][k]);
				idx += 2;
			}
		}

		return all;
	}
	
	public int size()
	{
		int n = this.numColorants_.intValue();
		return 38 * n + 12;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	
	/**
	 * Return the number of colorants in this table.
	 * @return int of coolorant count
	 */
	public UInt32Number getColorantCount()
	{
		return this.numColorants_;
	}
	
	/**
	 * Return the name string for the given colorant.
	 * @param which - index of the colorant, 0-based.
	 * @return string of the colorant name.
	 */
	public String getColorantName(int which)
	{
		if (which >= 0 && which < this.colorantNames_.length)
		{
			String name = null;
			try
			{
				name = this.colorantNames_[which].getText().trim();
			}
			catch (ICCProfileException e)
			{
			}
			return name;
		}
		return null;
	}
	
	/**
	 * Return the three colorant PCS values in a int[].
	 * @param which - index of the colorant, 0-based.
	 * @return int[3] containing x,y,z values of PCS for the colorant.
	 */
	public int[] getColorantPCS(int which)
	{
		if (which >= 0 && which < this.pcsValues_.length)
		{
			int[] ret = new int[3];
			ret[0] = this.pcsValues_[which][0].intValue();
			ret[1] = this.pcsValues_[which][1].intValue();
			ret[2] = this.pcsValues_[which][2].intValue();
			return ret;
		}
		return null;
	}
	
	/**
	 * Set a colorant at a particular position. If the index is out of bounds, an exception is thrown.
	 * @param which - index or position in the colorant table to insert this colorant into.
	 * @param name - string of the colorant name
	 * @param x, y, z - PCS values as int.
	 */
	public void setColorant(int which, String name, int x, int y, int z) throws ICCProfileException
	{
		if (which < 0 || which > this.colorantNames_.length)
			throw new ICCProfileException("index out of bounds",ICCProfileException.IndexOutOfBoundsException);
		this.colorantNames_[which] = new TextType(name, 32);
		this.pcsValues_[which][0] = new UInt16Number(x);
		this.pcsValues_[which][1] = new UInt16Number(y);
		this.pcsValues_[which][2] = new UInt16Number(z);
	}
	
	/**
	 * Add a new colorant to the table. This can be used after creating an empty ColorantTableType object.
	 * @param name - colorant name string to set
	 * @param x - PCS x or L value for the colorant
	 * @param y - PCS y or a* value for the colorant
	 * @param z - PCS z or b* value for the colorant
	 */
	public void addColorant(String name, int x, int y, int z) throws ICCProfileException
	{
		if (name == null || name.equals(""))
			throw new ICCProfileException("bad colorant name",ICCProfileException.IllegalArgumentException);
		int count = this.numColorants_.intValue();
		int newcount = count + 1;
		this.numColorants_ = new UInt32Number(newcount);
		if (this.colorantNames_ != null)
		{
			TextType[] backNames = this.colorantNames_;
			UInt16Number[][] backPCSs = this.pcsValues_;
			this.colorantNames_ = new TextType[newcount];
			this.pcsValues_ = new UInt16Number[newcount][3];
			for (int i=0; i<count; i++)
			{
				this.colorantNames_[i] = backNames[i];
				for (int k=0; k<3; k++)
				{
					this.pcsValues_[i][k] = backPCSs[i][k];
				}
			}
		}
		else
		{
			this.colorantNames_ = new TextType[newcount];
			this.pcsValues_ = new UInt16Number[newcount][3];
		}
		this.colorantNames_[count] = new TextType(name, 32);
		this.pcsValues_[count][0] = new UInt16Number(x);
		this.pcsValues_[count][1] = new UInt16Number(y);
		this.pcsValues_[count][2] = new UInt16Number(z);
	}

	/**
	 * Return the names of the colorants as a TextType array.
	 */
	public TextType[] getColorantNames()
	{
		return this.colorantNames_;
	}

	/**
	 * Return the PCS values for all the colorants as a 2-D array of UInt16Numbers.
	 */
	public UInt16Number[][] getPCSValues()
	{
		return this.pcsValues_;
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
			sb.append("<colorantTableType sig=\"clrt\">");
		else
			sb.append("<colorantTableType name=\""+name+"\" sig=\"clrt\">");
		sb.append(signature_.toXmlString());
		sb.append(numColorants_.toXmlString("number_of_colorants"));
		sb.append("<array name=\"colorant names\" dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<colorantNames_.length; i++)
		{
			sb.append(colorantNames_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("<array name=\"PCS values\" dims=\"2\"><dim index=\"0\">");
		for (int i=0; i<pcsValues_.length; i++)
		{
			sb.append("<dim index=\""+i+"\">");
			for (int j=0; j<pcsValues_[i].length; j++)
			{
				sb.append(pcsValues_[i][j].toXmlString());
			}
			sb.append("</dim>");
		}
		sb.append("</dim></array>");
		sb.append("</colorantTableType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
