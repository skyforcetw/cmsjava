package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.datatype.UnsignedInteger;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * MultiLocalizedUnicodeType is a tag type to define Unicode strings.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 *
 * This tag structure contains a set of multilingual Unicode strings associated with a profile. Each string in the set
 * is stored in a separate record with the information about what language and region the string is for.
 * 
 * 0..3		4	'mluc' (0x6D6C7563) type signature
 * 4..7		4	reserved, must be set to 0
 * 8..11	4	number of names (n): the number of name records that follow.uInt32Number
 * 12..15	4	name record size: the length in bytes of each name record that follows. 
 *				Each name record currently consists of the field's first name language code to first name offset. uInt32Number
 * 16..17	2	first name language code: language code from ISO-639 uInt16Number
 * 18..19	2	first name country code: region code from ISO-3166 uInt16Number
 * 20-23	4	first name length: the length in bytes of the string uInt32Number
 * 24..27	4	first name offset: the offset from the start of the tag in bytes uInt32Number
 * 28..28+(12*(n-1))-1 (or 15+12*n)		12*(n-1)	if more than one name record, store them here
 * 28+(12*(n-1) (or(16+12*n))...end					Storage area of Unicode characters
 */
public class MultiLocalizedUnicodeType extends AbstractTagType
{
	public static final int SIGNATURE = 0x6D6C7563;
	public static final int DEFAULT_NAME_REC_SIZE = 12;	//default by spec v.4.2
	
	private	Signature		signature_;
	private	UInt32Number	numNames_;		//8, (n)
	private	UInt32Number	nameRecSize_;	//12 by default now, can be different in some profiles
	private	UInt16Number[]	nameLangCode_;		//16 n records, ISO-639.1 2-letter code
	private	UInt16Number[]	nameCountryCode_;	//n records, ISO-3166
	private	UInt32Number[]	nameLength_;	//n records, length in bytes of unicode strings
	private	UInt32Number[]	nameOffset_;	//n records relative to start of this tag
	private	byte[]			unicodes_;	//n UTF-16BE strings (not zero-term) of different languages

	public MultiLocalizedUnicodeType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"mluc");
		this.numNames_ = new UInt32Number(0);
		this.nameRecSize_ = new UInt32Number(DEFAULT_NAME_REC_SIZE);	//correct ?
	}

	public MultiLocalizedUnicodeType(byte[] byteArray) throws ICCProfileException
	{
		fromByteArray(byteArray, 0, 0);
	}

	public MultiLocalizedUnicodeType(byte[] byteArray, int offset) throws ICCProfileException
	{
		fromByteArray(byteArray, offset, 0);
	}

	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len >= byteArray.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x6D6C7563)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.numNames_ = new UInt32Number(byteArray, offset + 8);
		this.nameRecSize_ = new UInt32Number(byteArray, offset + 12);
		int n = this.numNames_.intValue();
		if (n <= 0)
			return;
		this.nameLangCode_ = new UInt16Number[n];
		this.nameCountryCode_ = new UInt16Number[n];
		this.nameLength_ = new UInt32Number[n];
		this.nameOffset_ = new UInt32Number[n];
		int idx = offset + 16;
		//TODO: the nameRecSize is not used here, this is not compliant with the spec v4.2, update it later
		for (int i=0; i<n; i++)
		{
			this.nameLangCode_[i] = new UInt16Number(byteArray, idx);
			idx += UInt16Number.SIZE;
			this.nameCountryCode_[i] = new UInt16Number(byteArray, idx);
			idx += UInt16Number.SIZE;
			this.nameLength_[i] = new UInt32Number(byteArray, idx);
			idx += UInt32Number.SIZE;
			this.nameOffset_[i] = new UInt32Number(byteArray, idx);
			idx += UInt32Number.SIZE;
		}
		int rem = byteArray.length - idx;
		//TODO: check boundary, rem should be equal to the sum of length of all strings
		this.unicodes_ = new byte[rem];
		System.arraycopy(byteArray, idx, this.unicodes_, 0, rem);
	}

  /**
   * Serialise the data elements into a byte array.
   * The toByteArray method of this class is a bit different in that it does not
   * raise any exceptions even the instance is empty.  The MultiLocalizedUnicodeType
   * allows an empty instance with only the 'header' (signature and 0 names).
   * @return byte array containing the serialised data elements.
   */
	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.numNames_ == null)
			throw new ICCProfileException("MultiLocalizedUnicodeType.toByteArray: data not set",ICCProfileException.InvalidDataValueException);

		int n = this.numNames_.intValue();
		int sz = DEFAULT_NAME_REC_SIZE;
		if (this.nameRecSize_ != null)
			sz = this.nameRecSize_.intValue();
		int len = 16 + n * sz + ((this.unicodes_==null)?0:this.unicodes_.length);
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.numNames_);
		if (this.nameRecSize_ != null)
			ICCUtils.appendByteArray(all, 12, this.nameRecSize_);
		if (n <= 0)
			return all;

		int idx = 16;
		for (int i=0; i<n; i++)
		{
			ICCUtils.appendByteArray(all, idx, this.nameLangCode_[i]);
			idx += UInt16Number.SIZE;
			ICCUtils.appendByteArray(all, idx, this.nameCountryCode_[i]);
			idx += UInt16Number.SIZE;
			ICCUtils.appendByteArray(all, idx, this.nameLength_[i]);
			idx += UInt32Number.SIZE;
			ICCUtils.appendByteArray(all, idx, this.nameOffset_[i]);
			idx += UInt32Number.SIZE;
		}
		System.arraycopy(this.unicodes_, 0, all, idx, this.unicodes_.length);

		return all;
	}
	
	public int size()
	{
		assert( this.numNames_ != null );
		int n = this.numNames_.intValue();
		int sz = this.nameRecSize_.intValue();
		return 16 + n * sz + ((this.unicodes_==null)?0:this.unicodes_.length);
	}
	
	public Signature getSignature()
	{
		return this.signature_;
	}

	public UInt32Number getNumNames()
	{
		return this.numNames_;
	}

	public UInt32Number getNameRecSize()
	{
		return this.nameRecSize_;
	}

	public UInt16Number[] getNameLangCode()
	{
		return this.nameLangCode_;
	}

	public UInt16Number[] getNameCountryCode()
	{
		return this.nameCountryCode_;
	}

	public UInt32Number[] getNameLength()
	{
		return this.nameLength_;
	}

	public UInt32Number[] getNameOffset()
	{
		return this.nameOffset_;
	}

	public byte[] getUnicodes()
	{
		return this.unicodes_;
	}

	public String getText(int which)
	{
		//TODO: return unicode string based on locale encoding
		return null;
	}

	private void adjustOffsets()
	{
		int n = numNames_.intValue();
		int sz = nameRecSize_.intValue();
		int offset = 16 + sz * n;
		for (int i=0; i<n; i++)
		{
			nameOffset_[i] = new UInt32Number(offset);
			offset += nameLength_[i].intValue();
		}
	}

	/**
	 * Add a text string to the tag type object.
	 * Use ISO-639.1 for language code, such as EN/ENG for English, ZH/CHI for Chinese.
	 * Use ISO-3166 for nation/region code, such as US, UK, CN for China, TW for Taiwan.
	 * The text is a string encoded as UTF-16BE.
	 * @param language - 2 character language code string following ISO-639.1
	 * @param country - 2 character code following ISO-3166
	 * @param textbytes - UTF-16BE encoded byte array
	 */
	public void addText(String language, String country, byte[] textbytes) throws ICCProfileException
	{
		int num = (numNames_==null)?0:numNames_.intValue();
		if (num < 0)
			throw new ICCProfileException("MultiLocalizedUnicodeType.addText: bad number of names");
		if (num == 0)
		{
			numNames_ = new UInt32Number(1);
			nameRecSize_ = new UInt32Number(DEFAULT_NAME_REC_SIZE);	//as default
			nameLangCode_ = new UInt16Number[1];
			nameCountryCode_ = new UInt16Number[1];
			nameLength_ = new UInt32Number[1];
			nameOffset_ = new UInt32Number[1];
		}
		else
		{
			numNames_ = new UInt32Number(num + 1);
			nameLangCode_ = ICCUtils.expand(nameLangCode_, 1);
			nameCountryCode_ = ICCUtils.expand(nameCountryCode_, 1);
			nameLength_ = ICCUtils.expand(nameLength_, 1);
			nameOffset_ = ICCUtils.expand(nameOffset_, 1);
		}
		nameLangCode_[num] = new UInt16Number(language.getBytes());
		nameCountryCode_[num] = new UInt16Number(country.getBytes());
		nameLength_[num] = new UInt32Number(textbytes.length);
		nameOffset_[num] = new UInt32Number(0);
		adjustOffsets();
		if (unicodes_ == null)
			unicodes_ = textbytes;
		else
		{
			byte[] bac = unicodes_;
			unicodes_ = new byte[bac.length + textbytes.length];
			System.arraycopy(bac, 0, unicodes_, 0, bac.length);
			System.arraycopy(textbytes, 0, unicodes_, bac.length, textbytes.length);
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
			sb.append("<multiLocalizedUnicodeType sig=\"mluc\">");
		else
			sb.append("<multiLocalizedUnicodeType name=\""+name+"\" sig=\"mluc\">");
		sb.append(signature_.toXmlString());
		sb.append(numNames_.toXmlString());
		sb.append(nameRecSize_.toXmlString());
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<nameLangCode_.length; i++)
		{
			sb.append(nameLangCode_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<nameCountryCode_.length; i++)
		{
			sb.append(nameCountryCode_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<nameLength_.length; i++)
		{
			sb.append(nameLength_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<nameOffset_.length; i++)
		{
			sb.append(nameOffset_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<unicodes_.length; i++)
		{
			sb.append(unicodes_[i]);
		}
		sb.append("</dim></array>");
		sb.append("</multiLocalizedUnicodeType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
