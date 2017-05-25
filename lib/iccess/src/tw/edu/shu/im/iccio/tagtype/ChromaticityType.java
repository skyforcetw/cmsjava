package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.ICCProfileException;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.datatype.U16Fixed16Number;

/**
 * ChromaticityType is a tag type for basic chromaticity data.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The chromaticity tag type provides basic chromaticity data and type of phosphors or colorants of a monitor to
 * applications and utilities.
 *
 * Data elements:
 * <code>
 * Position       Bytes        Content                           Type
 * 0..3            4           'chrm'   (0x6368726D)
 * 4..7            4           reseved 0
 * 8..9            2           number of device channels (n)     UInt16Number
 * 10..11          2           encoded value of phosphor or colorant type
 * 12..19          8           CIE xy coord of channel 1         U16Fixed16Number[2]
 * 20..end       (n-1)*8       CIE xy coord for other n channels U16Fixed16Number[...]
 * </code>
 */
public class ChromaticityType extends AbstractTagType
{
	public static final int SIGNATURE = 0x6368726D;
	
	private Signature		signature_;
	private	UInt16Number		deviceChannels_;	//number of device channels
	private	ColorantCode		colorantCode_;	//encoded value of phosphor or colorant type
	private	U16Fixed16Number[]	ciexyCoords_;	//CIE xy coord values of all channels

	/**
	 * Construct an empty ChromaticityType object for new dataset.
	 */
	public ChromaticityType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"chrm"
	}
	
	/**
	 * Construct a Chromaticity object from a byte array.
	 * @param bytes - byte array containing a Chromaticity type of dataset.
	 */
	public ChromaticityType(byte[] bytes) throws ICCProfileException
	{
		fromByteArray(bytes, 0, 0);
	}

	/**
	 * Parse a byte array to make a ChromaticityType data object.
	 * @param bytes - byte array containing the ChromaticityType data values.
	 * @param offset - starting position of data in the array.
	 * @param len - not used in this type because the actual size is determined in the dataset.
	 */
	public void fromByteArray(byte[] bytes, int offset, int len) throws ICCProfileException
	{
		if (bytes == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset > bytes.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(bytes, offset);
		if (this.signature_.intValue()!=0x6368726D)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);
		
		this.deviceChannels_ = new UInt16Number(bytes, offset + 8);
		this.colorantCode_ = new ColorantCode(bytes, offset + 10);

		int xyValues = this.deviceChannels_.intValue();
		int idx = offset + 12;
		if (bytes.length < idx + 8 * xyValues)
			throw new ICCProfileException("not enough bytes to construct Chromaticity object",ICCProfileException.WrongSizeException);

		this.ciexyCoords_ = new U16Fixed16Number[xyValues * 2];
		for (int i=0, k=0; i<this.deviceChannels_.intValue(); i++)
		{
			this.ciexyCoords_[k++] = new U16Fixed16Number(bytes, idx);	//for cie x
			idx += U16Fixed16Number.SIZE;
			this.ciexyCoords_[k++] = new U16Fixed16Number(bytes, idx);	//for cie y
			idx += U16Fixed16Number.SIZE;
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.deviceChannels_==null || this.colorantCode_==null || ciexyCoords_==null)
			throw new ICCProfileException("data value not set",ICCProfileException.InvalidDataValueException);

		int n = this.deviceChannels_.intValue();
		int len = 12 + n * 8;
		byte[] all = new byte[12 + n * 8];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.deviceChannels_);
		ICCUtils.appendByteArray(all, 10, this.colorantCode_);
		int k = 12;
		for (int i=0; i<n*2; i++)
		{
			ICCUtils.appendByteArray(all, k, this.ciexyCoords_[i]);
			k += U16Fixed16Number.SIZE;
		}
		
		return all;
	}
	
	public int size()
	{
		int n = this.deviceChannels_.intValue();
		return n * 8 + 12;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	
	public UInt16Number getDeviceChannels()
	{
		return this.deviceChannels_;
	}
	
	public void setChannelCount(UInt16Number channels)
	{
		this.deviceChannels_ = channels;
	}
	
	public ColorantCode getColorantCode()
	{
		return this.colorantCode_;
	}
	
	public void setColorantCode(ColorantCode code)
	{
		this.colorantCode_ = code;
	}
	
	public U16Fixed16Number[] getCIEXYCoords()
	{
		return this.ciexyCoords_;
	}

	public void setCIEXYCoords(U16Fixed16Number[] ciexycoords)
	{
		this.ciexyCoords_ = ciexycoords;
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
			sb.append("<chromaticityType sig=\"chrm\">");
		else
			sb.append("<chromaticityType name=\""+name+"\" sig=\"chrm\">");
		sb.append(signature_.toXmlString());
		sb.append(deviceChannels_.toXmlString("device_channels"));
		sb.append(colorantCode_.toXmlString("colorant_code"));
		sb.append("<array name=\"CIE XY coords\" dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<ciexyCoords_.length; i++)
		{
			sb.append(ciexyCoords_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("</chromaticityType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
