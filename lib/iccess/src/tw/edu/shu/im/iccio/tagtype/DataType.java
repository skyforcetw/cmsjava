package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * DataType is a tag type for either ASCII string or binary data.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The dataType is a simple data structure that contains either 7-bit ASCII or binary data, i.e. textType data or
 * transparent 8-bit bytes.
 *
 * Data elements:
 * Position       Bytes        Content                             Type
 * 0..3				4			'data'      (0x64617461)
 * 4..7				4			reseved 0
 * 8..11			4			data flag, 0 means ASCII,1 binary	UInt32Number
 * 12..end						data values as byte[] or string		[]
 *
 */
public class DataType extends AbstractTagType
{
	public static final int SIGNATURE = 0x64617461;
	
	private	Signature			signature_;
	private	UInt32Number			dataFlag_;	//data flag
	private	TextType			asciiString_;	//ASCII data string
	private	byte[]				binaryData_;	//binary byte[]

	public DataType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"data");
	}
	
	public DataType(byte[] bytes) throws ICCProfileException
	{
		fromByteArray(bytes, 0, 0);
	}

	public void fromByteArray(byte[] bytes, int offset, int len) throws ICCProfileException
	{
		if (bytes == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > bytes.length)
			throw new ICCProfileException("index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(bytes, offset);
		if (this.signature_.intValue() != 0x64617461)
			throw new ICCProfileException("DataType.ctor: signature not correct!");

		this.dataFlag_ = new UInt32Number(bytes, offset + 8);
		if (this.dataFlag_.intValue() > 1)
		{
			System.err.println("DataType.flag = "+this.dataFlag_.intValue());
			//throw new ICCProfileException("DataType.ctor: data flag not 0 or 1 : "+this.dataFlag_.intValue());
		}

		if (len <= 0)
			len = bytes.length - offset - 12;
		if (this.dataFlag_.intValue() == 0)
		{
			this.asciiString_ = new TextType();
			this.asciiString_.setText(bytes, offset + 12, len);
		} 
		else 
		{
			int idx = offset + 12;
			this.binaryData_ = new byte[len];
			for (int i=0; i<len; i++)
			{
				this.binaryData_[i] = bytes[idx++];
			}
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (dataFlag_==null || (asciiString_==null && binaryData_==null))
			throw new ICCProfileException("data not set",ICCProfileException.InvalidDataValueException);

		int n = this.dataFlag_.intValue();
		int len = 12 + ((n == 0) ? asciiString_.getLength() : binaryData_.length);
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		if (n == 0)
		{
			byte[] asciibytes = this.asciiString_.getBytes();
			System.arraycopy(asciibytes, 0, all, 12, asciibytes.length);
		}
		else
		{
			ICCUtils.appendByteArray(all, 8, this.dataFlag_);
			System.arraycopy(this.binaryData_, 0, all, 12, binaryData_.length);
		}

		return all;
	}
	
	public int size()
	{
		assert(this.dataFlag_!=null);
		int n = this.dataFlag_.intValue();
		return 12 + ((n == 0) ? asciiString_.getLength() : binaryData_.length);
	}

	public Signature getSignature()
	{
		return this.signature_;
	}

	public boolean isAsciiString()
	{
		return (dataFlag_!=null) && (dataFlag_.intValue()==0);
	}
	
	public String getText()
	{
		if (isAsciiString() && (asciiString_!=null))
		{
			try
			{
				String s = asciiString_.getText();
				return s;
			}
			catch (Exception e)
			{
				return null;
			}
		}
		return null;
	}

	public void setAsciiData(String str) throws ICCProfileException
	{
		this.dataFlag_ = new UInt32Number(0); //ASCII
		this.binaryData_ = null;
		this.asciiString_ = new TextType(str);
	}
	
	public byte[] getData()
	{
		if (!isAsciiString())
			return this.binaryData_;
		return null;
	}

	public void setBinaryData(byte[] data)
	{
		this.dataFlag_ = new UInt32Number(1);	//binary
		this.asciiString_ = null;
		this.binaryData_ = data;
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
			sb.append("<dataType sig=\"data\">");
		else
			sb.append("<dataType name=\""+name+"\" sig=\"data\">");
		sb.append(signature_.toXmlString());
		sb.append(dataFlag_.toXmlString("flag"));
		if (dataFlag_.intValue()==0)
			sb.append(asciiString_.toXmlString());
		else if (binaryData_!=null)
		{
			sb.append("<array name=\"binary_data\" dims=\"1\"><dim index=\"0\">");
			for (int i=0; i<binaryData_.length; i++)
			{
				if (i > 0)
					sb.append(',');
				sb.append(binaryData_[i]);
			}
			sb.append("</dim></array>");
		}
		sb.append("</dataType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
