package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt8Number;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

import java.io.UnsupportedEncodingException;

/**
 * The textDescriptionType is a complex structure that contains three types of text
 * description structures: 7 bit ASCII, Unicode and ScriptCode. Since no single
 * standard method for specifying localizable character sets exists across the major
 * platform vendors, including all three provides access for the major operating
 * systems. The 7 bit ASCII description is to be an invariant, nonlocalizable name
 * for consistent reference. It is preferred that both the Unicode and ScriptCode
 * structures be properly localized.
 *
 * ICC Profile version: 2.x - 3.4
 *
 * This type is used frequently in profileDescriptionTag on ICC profile version 2.x,3.x
 * But in version 4.2.0.0, it disappeared, or replaced by MultiLocalizedUnicodeType.
 *
 * ByteOffset Content Encoded as...
 * 0-3	'desc'(64657363h) type descriptor
 * 4-7	reserved, must be set to 0
 * 8-11	7 bit ASCII invariant description count,including terminating null (descriptionlength)uInt32Number
 * 12 - n-1 	7 bit ASCII invariant description
 * n - n+3 	Unicode language code uInt32Number
 * n+4 - n+7 	Unicode localizable description count(description length)uInt32Number
 * n+8 - m-1	Unicode localizable description
 * m - m+1 	ScriptCode code uInt16Number
 * m+2 		Localizable Macintosh description count(description length)uInt8Number
 * m+3 - m+69	Localizable Macintosh description
 *
 * <p>
 * Note: It has been found that textDescriptionType can contain misaligned data
 * (see clause 4.1 for the definition of aligned). Because the Unicode language
 * code and Unicode count immediately follow the ASCII description, their
 * alignment is not correct if the ASCII count is not a multiple of four. The
 * ScriptCode code is misaligned when the ASCII count is odd. Profile reading and
 * writing software must be written carefully in order to handle these alignment
 * problems.
 * </p>
 */
public class TextDescriptionType extends AbstractTagType
{
	public static final int SIGNATURE = 0x64657363;

	private	Signature		signature_;
	private	UInt32Number		asciizCount_;	//8, byte count including terminate 0
	private	UInt8Number[]		asciizData_;	//12
	private	UInt32Number		unicode_;	//unicode language code
	private	UInt32Number		unicodeCount_;	//count of unicode string
	private	UInt16Number[]		unicodeData_;
	private	UInt16Number		scriptCode_;	//ScriptCode code
	private	UInt8Number		scriptCount_;	//Macintosh ScriptCode desc count
	private	UInt8Number[]		scriptData_;	//Macintosh description
	
	public TextDescriptionType()
	{
		this.signature_ = new Signature(SIGNATURE);
		this.asciizCount_ = new UInt32Number(0);
		this.unicode_ = new UInt32Number(0);
		this.unicodeCount_ = new UInt32Number(0);
		this.scriptCode_ = new UInt16Number((short)0);
		this.scriptCount_ = new UInt8Number((byte)0);
	}
	
	public TextDescriptionType(byte[] byteArray) throws ICCProfileException
	{
		fromByteArray(byteArray, 0, 0);
	}
	
	/**
	 * Parse a byte array to make a TextDescriptionType data object.
	 * @param byteArray - byte array containing the bytes to form this object.
	 * @param offset - starting position in the array for the data object.
	 * @param len - not used here.
	 */
	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > byteArray.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x64657363)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.asciizCount_ = new UInt32Number(byteArray, offset+8);
		int nasciiz = this.asciizCount_.intValue();
		if (nasciiz + 12 > byteArray.length)
			throw new ICCProfileException("byte array size error",ICCProfileException.WrongSizeException);
		this.asciizData_ = new UInt8Number[nasciiz];
		int idx = offset + 12;
		for (int i=0; i<nasciiz; i++)
			this.asciizData_[i] = new UInt8Number(byteArray, idx++);
		this.unicode_ = new UInt32Number(byteArray, idx);
		idx += 4;
		this.unicodeCount_ = new UInt32Number(byteArray, idx);
		int nunicode = this.unicodeCount_.intValue();
		idx += 4;
		if (nunicode > 0)
		{
			this.unicodeData_ = new UInt16Number[nunicode];
			for (int i=0; i<nunicode; i++)
			{
				this.unicodeData_[i] = new UInt16Number(byteArray, idx);
				idx += 2;
			}
		}
		this.scriptCode_ = new UInt16Number(byteArray, idx);
		idx += 2;
		this.scriptCount_ = new UInt8Number(byteArray, idx);
		int nscript = this.scriptCount_.intValue();
		idx ++;
		//copy all rest data to scriptData_, which is 67 bytes long
		this.scriptData_ = new UInt8Number[67];
		for (int i=0; i<67; i++)
			if (idx < byteArray.length)
				this.scriptData_[i] = new UInt8Number(byteArray, idx++);
	}
	
	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.asciizCount_ == null)
			throw new ICCProfileException("data not set",ICCProfileException.InvalidDataValueException);
		int nascii = this.asciizCount_.intValue();
		int nunicode = this.unicodeCount_.intValue();
		int nscript = this.scriptCount_.intValue();
		int len = 23 + nascii + nunicode + 67;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.asciizCount_);
		int idx = 12;
		for (int i=0; i<this.asciizData_.length; i++)
			all[idx++] = this.asciizData_[i].byteValue();
		ICCUtils.appendByteArray(all, idx, this.unicode_); idx += 4;
		ICCUtils.appendByteArray(all, idx, this.unicodeCount_); idx += 4;
		for (int i=0; i<nunicode; i++)
		{
			ICCUtils.appendByteArray(all, idx, this.unicodeData_[i]);
			idx += 2;
		}
		ICCUtils.appendByteArray(all, idx, this.scriptCode_); idx += 2;
		ICCUtils.appendByteArray(all, idx, this.scriptCount_); idx ++;
		if (this.scriptData_ != null && this.scriptData_.length >= 67)
		{
			for (int i=0; i<67; i++)
				ICCUtils.appendByteArray(all, idx++, this.scriptData_[i]);
		}
		return all;
	}
	
	public int size()
	{
		assert(this.asciizCount_!=null);
		int nascii = this.asciizCount_.intValue();
		int nunicode = this.unicodeCount_.intValue();
		int len = 23 + nascii + nunicode + 67;
		return len;
	}
	
	public Signature getSignature()
	{
		return this.signature_;
	}
	
	public UInt32Number getAsciiCount()
	{
		return this.asciizCount_;
	}
	public UInt8Number[] getAsciiData()
	{
		return this.asciizData_;
	}
	public void setAsciiData(UInt8Number[] data)
	{
		this.asciizData_ = data;
		this.asciizCount_ = new UInt32Number(data.length);
	}
	public void setAsciiData(String text)
	{
		byte[] bytes = text.getBytes();
		this.asciizData_ = new UInt8Number[bytes.length];
		for (int i=0; i<bytes.length; i++)
			this.asciizData_[i] = new UInt8Number(bytes[i]);
		this.asciizCount_ = new UInt32Number(bytes.length);
	}
	public UInt32Number getUnicode()
	{
		return this.unicode_;
	}
	public void setUnicode(UInt32Number unicode)
	{
		this.unicode_ = unicode;
	}
	public UInt32Number getUnicodeCount()
	{
		return this.unicodeCount_;
	}
	public UInt16Number[] getUnicodeData()
	{
		return this.unicodeData_;
	}
	public void setUnicodeData(UInt16Number[] data)
	{
		this.unicodeData_ = data;
		this.unicodeCount_ = new UInt32Number(data.length);
	}
	public UInt16Number getScriptCode()
	{
		return this.scriptCode_;
	}
	public void setScriptCode(UInt16Number sc)
	{
		this.scriptCode_ = sc;
	}
	public UInt8Number getScriptCount()
	{
		return this.scriptCount_;
	}
	public UInt8Number[] getScriptData()
	{
		return this.scriptData_;
	}
	public void setScriptData(UInt8Number[] data)
	{
		this.scriptData_ = data;
		this.scriptCount_ = new UInt8Number((byte)data.length);
	}
	public String getAsciiString()
	{
		byte[] bs = new byte[this.asciizData_.length];
		for (int i=0; i<bs.length; i++)
			bs[i] = this.asciizData_[i].byteValue();
		try
		{
			String s = new String(bs, "US-ASCII");
			return s.trim();
		}
		catch (UnsupportedEncodingException e)
		{
			System.err.println(e.getMessage());
		}
		return "";
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
			sb.append("<textDescriptionType sig=\"desc\">");
		else
			sb.append("<textDescriptionType name=\""+name+"\" sig=\"desc\">");
		sb.append(signature_.toXmlString());
		sb.append("<ascii count=\""+asciizCount_+"\">"+getAsciiString()+"</ascii>");
		if (unicodeCount_.intValue()>0)
		{
			sb.append("<unicode code=\""+unicode_+"\" count=\""+unicodeCount_+"\">");
			for (int i=0; i<unicodeData_.length; i++)
			{
				if (i > 0)
					sb.append(',');
				sb.append(unicodeData_[i].toString());
			}
			sb.append("</unicode>");
		}
		if (scriptCount_.intValue()>0)
		{
			sb.append("<script code=\""+scriptCode_+"\" count=\""+scriptData_.length+"\">");
			for (int i=0; i<scriptData_.length; i++)
			{
				if (i > 0)
					sb.append(',');
				sb.append(scriptData_[i].toString());
			}
			sb.append("</script>");
		}
		sb.append("</textDescriptionType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
