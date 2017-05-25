package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * SignatureType is a tag type that contains a 4-byte signature of data type Signature.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The signatureType contains a four-byte sequence, such as those defined in Table 22. Sequences of less than
 * four characters are padded at the end with spaces, 20h. Typically this type is used for registered tags that can
 * be displayed on many development systems as a sequence of four characters.
 * <code>
 * 0..3		4	'sig ' (73696720h) type signature
 * 4..7		4	reserved, must be set to 0
 * 8..11	4	four-byte signature
 * </code>
 */
public class SignatureType extends AbstractTagType
{
	public static final int SIGNATURE = 0x73696720;
	
	private	Signature	signature_;
	private	Signature	sigValue_;

	public SignatureType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"sig ");
	}

	public SignatureType(int signature)
	{
		this.signature_ = new Signature(SIGNATURE);
		this.sigValue_ = new Signature(signature);
	}
	
	public SignatureType(String signature) throws ICCProfileException
	{
		this.signature_ = new Signature("sig ");
		setSignatureData(signature);
	}

	public SignatureType(byte[] byteArray) throws ICCProfileException
	{
		fromByteArray(byteArray, 0, 0);
	}

	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > byteArray.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);
		
		if (byteArray.length - offset < 12)
			throw new ICCProfileException("byte array wrong size", ICCProfileException.WrongSizeException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x73696720)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.sigValue_ = new Signature(byteArray, offset + 8);
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.sigValue_ == null)
			throw new ICCProfileException("data not set", ICCProfileException.InvalidDataValueException);

		byte[] bar = new byte[12];
		ICCUtils.appendByteArray(bar, 0, this.signature_);
		ICCUtils.appendByteArray(bar, 8, this.sigValue_);

		return bar;
	}
	
	public int size()
	{
		return 12;
	}

	/**
	 * The getSignature() method always returns the signature of this class.
	 * See getSignatureData() method.
	 */
	public Signature getSignature()
	{
		return this.signature_;
	}

	/**
	 * Returns the Signature object that this object contains, rather than the signature of this class.
	 */
	public Signature getSignatureData()
	{
		return this.sigValue_;
	}

	/**
	 * Set the signature object that this class maintains.
	 * @param signature - instance of a new Signature
	 */
	public void setSignatureData(String signature) throws ICCProfileException
	{
		this.sigValue_ = new Signature(signature);
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
			sb.append("<signatureType sig=\"sig \">");
		else
			sb.append("<signatureType name=\""+name+"\" sig=\"sig \">");
		sb.append(signature_.toXmlString());
		sb.append(sigValue_.toXmlString());
		sb.append("</signatureType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
