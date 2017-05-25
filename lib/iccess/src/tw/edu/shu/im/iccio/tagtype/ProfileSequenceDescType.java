package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * ProfileSequenceDescType is a tag type for profile sequence descriptions.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * This type is an array of structures, each of which contains information from the header fields and tags from the
 * original profiles which were combined to create the final profile. The order of the structures is the order in which
 * the profiles were combined and includes a structure for the final profile. This provides a description of the
 * profile sequence from source to destination, typically used with the DeviceLink profile.
 * <code>
 * 0..3		4	'pseq' (70736571h) type signature
 * 4..7		4	reserved, must be set to 0
 * 8..11	4	count value specifying number of description structures in the array
 * 12..end		variable count profile description structures - see Profile Description structure below:
 * </code>
 * Profile Description structure
 * @see ProfileDesc class<code>
 * 0..3		4	Device manufacturer signature (from corresponding profile's header)
 * 4..7		4	Device model signature (from corresponding profile's header)
 * 8..15	8	Device attributes (from corresponding profile's header)
 * 16..19	4	Device technology information such as CRT, Dye Sublimation, etc. (corresponding to profile's technology signature)
 * 20..m		variable displayable description of device manufacturer (profile's deviceMfgDescTag)
 * m+1..n		variable displayable description of device model (profile's deviceModelDescTag)
 * </code>
 *
 */
public class ProfileSequenceDescType extends AbstractTagType
{
	public static final int SIGNATURE = 0x70736571;
	
	private	Signature	signature_;
	private	UInt32Number	count_;
	private	ProfileDesc[]	pds_;

	public ProfileSequenceDescType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"pseq");
	}

	public ProfileSequenceDescType(byte[] byteArray) throws ICCProfileException
	{
		fromByteArray(byteArray, 0, 0);
	}

	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > byteArray.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x70736571)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.count_ = new UInt32Number(byteArray, offset + 8);
		int count = this.count_.intValue();
		this.pds_ = new ProfileDesc[count];
		int idx = offset + 12;
		for (int i=0; i<count; i++)
		{
			ProfileDesc pd = new ProfileDesc(byteArray, idx);
			this.pds_[i] = pd;
			idx += pd.size();
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.count_==null || this.pds_==null)
			throw new ICCProfileException("data not set", ICCProfileException.InvalidDataValueException);

		int n = this.count_.intValue();
		if (n != this.pds_.length)
		{
			n = this.pds_.length;
			this.count_ = new UInt32Number(n);
		}
		int len = 12;
		for (int i=0; i<n; i++)
			len += this.pds_[i].size();
		
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.count_);

		int x = 12;
		for (int i=0; i<this.pds_.length; i++)
		{
			byte[] desc = this.pds_[i].toByteArray();
			System.arraycopy(desc, 0, all, x, desc.length);
			x += desc.length;
		}

		return all;
	}
	
	public int size()
	{
		assert(this.count_!=null);
		int n = this.count_.intValue();
		int len = 12;
		for (int i=0; i<n; i++)
			len += this.pds_[i].size();
		return len;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	public int getCount()
	{
		return this.pds_.length;
	}
	public void setCount(int count)
	{
		this.count_ = new UInt32Number(count);
	}
	public ProfileDesc[] getProfileDescs()
	{
		return this.pds_;
	}
	
	/**
	 * Set the profile descriptions from an array of ProfileDesc objects.
	 * If the count is null or not set properly, it is adjusted to show the number of profile descriptions.
	 * @param profiledescs - array of ProfileDesc objects
	 */
	public void setProfileDesc(ProfileDesc[] profiledescs)
	{
		if (this.count_==null || this.count_.intValue()!=profiledescs.length)
		{
			this.count_ = new UInt32Number(profiledescs.length);
		}
		this.pds_ = profiledescs;
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
			sb.append("<profileSequenceDescType sig=\"pseq\">");
		else
			sb.append("<profileSequenceDescType name=\""+name+"\" sig=\"pseq\">");
		sb.append(signature_.toXmlString());
		sb.append(count_.toXmlString());
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<pds_.length; i++)
		{
			sb.append(pds_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("</profileSequenceDescType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
