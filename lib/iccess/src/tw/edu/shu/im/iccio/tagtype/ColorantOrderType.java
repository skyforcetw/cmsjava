package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt8Number;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * ColorantOrderType is an optional tag type for laydown order.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * This is an optional tag which specifies the laydown order in which colorants will be printed on an n-colorant
 * device. The laydown order may be the same as the channel generation order listed in the colorantTableTag or
 * the channel order of a colour space such as CMYK, in which case this tag is not needed.
 * 
 * Data elements:
 * Position       Bytes        Content                           Type
 * 0..3				4			'clro'  (636c726f)    
 * 4..7				4			reseved 0
 * 8..11			4			Count of colorants(n)						UInt32Number
 * 12				1			Number of the colorant to be printed first	UInt8Number
 * 13..(n+11)		n-1			rest colorants								UInt8Number
 */
public class ColorantOrderType extends AbstractTagType
{
	public static final int SIGNATURE = 0x636c726f;
	
	private	Signature	signature_;
	private	UInt32Number	numColorants_;		//count of colorants
	private	UInt8Number[]	colorants_;			//array of colorants

	public ColorantOrderType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"clro");
	}
	
	public ColorantOrderType(byte[] bytes) throws ICCProfileException
	{
		fromByteArray(bytes, 0, 0);
	}

	public void fromByteArray(byte[] bytes, int offset, int len) throws ICCProfileException
	{
		if (bytes == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len >= bytes.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(bytes, offset);
		if (this.signature_.intValue() != 0x636c726f)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.numColorants_ = new UInt32Number(bytes, offset + 8);
		int n = this.numColorants_.intValue();
		if (n <= 0)
			throw new ICCProfileException("empty ColorantOrderType", ICCProfileException.InvalidDataValueException);
		
		this.colorants_ = new UInt8Number[n];
		if (bytes.length - offset < 12 + n)
			throw new ICCProfileException("ColorantOrderType.ctor: not enough bytes in array", ICCProfileException.WrongSizeException);

		int idx = offset + 12;
		for (int i=0; i<n; i++)
		{
			this.colorants_[i] = new UInt8Number(bytes, idx++);
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (numColorants_==null || colorants_==null)
			throw new ICCProfileException("data not set",ICCProfileException.InvalidDataValueException);

		int len = 12 + colorants_.length;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.numColorants_);
		for (int i=0; i<colorants_.length; i++)
		{
			all[12+i] = colorants_[i].byteValue();
		}
		
		return all;
	}
	
	public int size()
	{
		assert(colorants_!=null);
		return colorants_.length + 12;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	
	public UInt32Number getColorantCount()
	{
		return this.numColorants_;
	}
	
	public void setColorantCount(int numColorants)
	{
		if (numColorants <= 0)
		{
			return;
		}
		this.numColorants_ = new UInt32Number(numColorants);
		//this.colorants_ = new UInt8Number[numColorants];
	}
	
	public UInt8Number[] getColorants()
	{
		return this.colorants_;
	}
	
	public void setColorants(byte[] colorants) throws ICCProfileException
	{
		if (this.colorants_.length != colorants.length)
		{
			this.numColorants_ = new UInt32Number(colorants.length);
			this.colorants_ = new UInt8Number[colorants.length];
			for (int i=0; i<colorants.length; i++)
			{
				this.colorants_[i] = new UInt8Number(colorants, i);
			}
		}
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
			sb.append("<colorantOrderType sig=\"clro\">");
		else
			sb.append("<colorantOrderType name=\""+name+"\" sig=\"clro\">");
		sb.append(signature_.toXmlString());
		sb.append(numColorants_.toXmlString("number_of_colorants"));
		sb.append("<array name=\"colorants\" dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<colorants_.length; i++)
		{
			sb.append(colorants_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("</colorantOrderType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
