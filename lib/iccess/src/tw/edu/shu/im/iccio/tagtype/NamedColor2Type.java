package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.datatype.S15Fixed16Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * NamedColor2Type is a tag type for named colours.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The namedColor2Type is a count value and array of structures that provide colour coordinates for 7-bit ASCII colour names.
 * <code>
 * 0..3			4	'ncl2' (6E636C32h) type signature
 * 4..7			4	reserved, must be set to 0
 * 8..11		4	vendor specific flag (least-significant 16 bits reserved for ICC use)
 * 12..15		4	count of named colours (n) uInt32Number
 * 16..19		4	number of device coordinates (m) for each named colour uInt32Number
 * 20..51		32	prefix for each colour name (32-byte field including null termination) 7-bit ASCII
 * 52..83		32	suffix for each colour name (32-byte field including null termination) 7-bit ASCII
 * 84..115		32	first colour root name (32-byte field including null termination) 7-bit ASCII
 * 116..121		6	first named colour¡¯s PCS coordinates. The encoding is the same as the encodings for the PCS colour 
 *					spaces as described in 6.3.4.2 and 10.8.Only 16-bit L*a*b*, encoded using legacy 16-bit PCS Lab encoding, 
 *					and XYZ are allowed. PCS values shall be relative colorimetric. uInt16Number[3]
 * 122..121+(m*2) m*2	first named colour¡¯s device coordinates. For each coordinate, 0000h represents the minimum value for 
 *						the device coordinate and FFFFh represents the maximum value for the device coordinate. The number of 
 *						coordinates is given by the "number of device coordinates" field. If the "number of device coordinates" 
 *						field is 0, this field is not given. uInt16Number[...]
 * 122+(m*2)..end (n-1)*(38+m*2)	if n > 1 the remaining n-1 colours are described in a manner consistent with the first named colour, 
 *									see byte offsets 84..121+(m*2).
 * </code>
 */
public class NamedColor2Type extends AbstractTagType
{
	public static final int SIGNATURE = 0x6E636C32;
	
	private	Signature			signature_;		//0
	private	UInt32Number		vendorFlag_;	//8
	private	UInt32Number		namedColors_;	//12
	private	UInt32Number		numDeviceCoords_;	//16
	private	TextType			prefix_;		//20	32 bytes
	private	TextType			suffix_;		//52	32 bytes
	private	TextType[]			rootNames_;		//84	32 bytes each, n items
	private	UInt16Number[][]	pcsCoords_;		//116	3 UInt16Number (6 bytes), n items
	private	UInt16Number[][]	deviceCoords_;	//122	m UInt16Number,(m*2 bytes), n items

	public NamedColor2Type()
	{
		this.signature_ = new Signature(SIGNATURE);	//"ncl2");
	}

	public NamedColor2Type(byte[] byteArray) throws ICCProfileException
	{
		fromByteArray(byteArray, 0, 0);
	}

	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len >= byteArray.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x6E636C32)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.vendorFlag_ = new UInt32Number(byteArray, offset + 8);
		this.namedColors_ = new UInt32Number(byteArray, offset + 12);
		this.numDeviceCoords_ = new UInt32Number(byteArray, offset + 16);
		this.prefix_ = new TextType();
    this.prefix_.setText(byteArray, offset + 20, 32);
		this.suffix_ = new TextType();
    this.suffix_.setText(byteArray, offset + 52, 32);
		int n = this.namedColors_.intValue();
		int m = this.numDeviceCoords_.intValue();
		this.rootNames_ = new TextType[n];
		this.pcsCoords_ = new UInt16Number[n][3];
		this.deviceCoords_ = new UInt16Number[n][m];
		int idx = offset + 84;
		for (int i=0; i<n; i++)
		{
			this.rootNames_[i] = new TextType();
      this.rootNames_[i].setText(byteArray, idx, 32);
			idx += 32;
			for (int k=0; k<3; k++)
			{
				this.pcsCoords_[i][k] = new UInt16Number(byteArray, idx);
				idx += UInt16Number.SIZE;
			}
			for (int k=0; k<m; k++)
			{
				this.deviceCoords_[i][k] = new UInt16Number(byteArray, idx);
				idx += UInt16Number.SIZE;
			}
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.vendorFlag_==null)
			throw new ICCProfileException("data not set",ICCProfileException.InvalidDataValueException);
		int n = this.namedColors_.intValue();
		int m = this.numDeviceCoords_.intValue();
		int len = 84 + n * (38 + m * 2);
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.vendorFlag_);
		ICCUtils.appendByteArray(all, 12, this.namedColors_);
		ICCUtils.appendByteArray(all, 16, this.numDeviceCoords_);
		byte[] pre = this.prefix_.getBytes();
		System.arraycopy(pre, 0, all, 20, pre.length);
		byte[] suf = this.suffix_.getBytes();
		System.arraycopy(suf, 0, all, 52, suf.length);

		int idx = 84;
		for (int i=0; i<n; i++)
		{
      byte[] b = this.rootNames_[i].getBytes();
      System.arraycopy(b, 0, all, idx, b.length);
			idx += 32;
			for (int k=0; k<3; k++)
			{
				ICCUtils.appendByteArray(all, idx, this.pcsCoords_[i][k]);
				idx += UInt16Number.SIZE;
			}
			for (int k=0; k<m; k++)
			{
				ICCUtils.appendByteArray(all, idx, this.deviceCoords_[i][k]);
				idx += UInt16Number.SIZE;
			}
		}

		return all;
	}
	
	public int size()
	{
		assert(this.namedColors_ != null);
		int n = this.namedColors_.intValue();
		int m = this.numDeviceCoords_.intValue();
		return 84 + n * (38 + m * 2);
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	public UInt32Number getVendorFlag()
	{
		return this.vendorFlag_;
	}
	public void setVendorFlag(int flag)
	{
		this.vendorFlag_ = new UInt32Number(flag);
	}
	public UInt32Number getNamedColors()
	{
		return this.namedColors_;
	}
/*
	public void setNamedColors(int colors)
	{
		this.namedColors_ = new UInt32Number(colors);
	}
*/
	public UInt32Number getNumDeviceCoords()
	{
		return this.numDeviceCoords_;
	}
/*
	public void setNumDeviceCoords(int coords)
	{
		this.numDeviceCoords_ = new UInt32Number(coords);
	}
*/
	public TextType getPrefix()
	{
		return this.prefix_;
	}
	public void setPrefix(String prefix) throws ICCProfileException
	{
		this.prefix_ = new TextType(prefix, 32);
	}
	public TextType getSuffix()
	{
		return this.suffix_;
	}
	public void setSuffix(String suffix) throws ICCProfileException
	{
		this.suffix_ = new TextType(suffix, 32);
	}
	public TextType[] getRootNames()
	{
		return this.rootNames_;
	}
	public UInt16Number[][] getPcsCoords()
	{
		return this.pcsCoords_;
	}
	public UInt16Number[][] getDeviceCoords()
	{
		return this.deviceCoords_;
	}

	/**
	 * Add a parameter (or whatever name for this) to the NamedColorType object.
	 * It contains a root name, 3 PCS coordinate values (0..65535), and m
	 * device coordinate values (0..65535) where m is the number of device coordinates.
	 * Note: the number of named colors and device coordinates must not be assigned
	 * using setNamedColors and setNumDeviceCoords methods before calling this
	 * method.  The numbers of colors will be incremented automatically, and the
	 * number of device coordinates is determined by the deviceCoords argument of this method.
	 * Therefore, the following calls of this method should have the deviceCoords argument
	 * with the same number of values in the array.
	 * 
	 * @param rootName - root name of the color type as a string
	 * @param pcsCoords - array of three unsigned short values for PCS
	 * @param deviceCoords - array of m unsigned short values for device coordinates
	 */
	public void addParameter(String rootName, int[] pcsCoords, int[]deviceCoords) throws ICCProfileException
	{
		if (numDeviceCoords_==null || numDeviceCoords_.intValue()!=deviceCoords.length)
			this.numDeviceCoords_ = new UInt32Number(deviceCoords.length);
		int m = this.numDeviceCoords_.intValue();
		if (this.rootNames_==null)
		{
      this.namedColors_ = new UInt32Number(1);
			this.rootNames_ = new TextType[1];
			this.pcsCoords_ = new UInt16Number[1][3];
			this.deviceCoords_ = new UInt16Number[1][m];
		} 
		else
		{
			TextType[] b1 = this.rootNames_;
			UInt16Number[][] b2 = this.pcsCoords_;
			UInt16Number[][] b3 = this.deviceCoords_;
			int n = b1.length + 1;
			this.namedColors_ = new UInt32Number(n);
			this.rootNames_ = new TextType[n];
			this.pcsCoords_ = new UInt16Number[n][3];
			this.deviceCoords_ = new UInt16Number[n][m];
			for (int i=0; i<b1.length; i++)
			{
				this.rootNames_[i] = b1[i];
				for (int k=0; k<3; k++)
					this.pcsCoords_[i][k] = b2[i][k];
				for (int k=0; k<m; k++)
					this.deviceCoords_[i][k] = b3[i][k];
			}
		}
		int last = this.rootNames_.length - 1;
		this.rootNames_[last] = new TextType(rootName, 32);
		for (int k=0; k<3; k++)
			this.pcsCoords_[last][k] = new UInt16Number(pcsCoords[k]);
		for (int k=0; k<m; k++)
			this.deviceCoords_[last][k] = new UInt16Number(deviceCoords[k]);
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
			sb.append("<namedColor2Type sig=\"ncl2\">");
		else
			sb.append("<namedColor2Type name=\""+name+"\" sig=\"ncl2\">");
		sb.append(signature_.toXmlString());
		sb.append(vendorFlag_.toXmlString());
		sb.append(namedColors_.toXmlString());
		sb.append(numDeviceCoords_.toXmlString());
		sb.append(prefix_.toXmlString());
		sb.append(suffix_.toXmlString());
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<rootNames_.length; i++)
		{
			sb.append(rootNames_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("<array dims=\"2\"><dim index=\"0\">");
		for (int i=0; i<pcsCoords_.length; i++)
		{
			sb.append("<dim index=\""+i+"\">");
			for (int j=0; j<pcsCoords_[i].length; j++)
			{
				sb.append(pcsCoords_[i][j].toXmlString());
			}
			sb.append("</dim>");
		}
		sb.append("</dim></array>");
		sb.append("<array dims=\"2\"><dim index=\"0\">");
		for (int i=0; i<deviceCoords_.length; i++)
		{
			sb.append("<dim index=\""+i+"\">");
			for (int j=0; j<deviceCoords_[i].length; j++)
			{
				sb.append(deviceCoords_[i][j].toXmlString());
			}
			sb.append("</dim>");
		}
		sb.append("</dim></array>");
		sb.append("</namedColor2Type>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
