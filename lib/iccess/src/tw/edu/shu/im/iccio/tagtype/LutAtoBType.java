package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt8Number;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * LutAtoBType is a tag type for A-to-B mapping table.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The type contains up to five processing elements which are stored
 * in the AtoBTag tag in the following order: a set of one dimensional curves, a 3 by 3 matrix with offset terms, a
 * set of one dimensional curves, a multidimensional lookup table, and a set of one dimensional output curves.
 * 
 * 0..3		4	'mAB ' (6D414220h) [multi-function A-to-B table] type signature
 * 4..7		4	reserved, must be set to 0
 * 8		1	Number of Input Channels (i) uInt8Number
 * 9		1	Number of Output Channels (o) uInt8Number
 * 10..11	2	Reserved for padding, must be set to 0
 * 12..15	4	Offset to first "B" curve* uInt32Number
 * 16..19	4	Offset to matrix uInt32Number
 * 20..23	4	Offset to first "M" curve* uInt32Number
 * 24..27	4	Offset to CLUT uInt32Number
 * 28..31	4	Offset to first "A" curve* uInt32Number
 * 32..end		Data
 */
public class LutAtoBType extends AbstractTagType
{
	public static final int SIGNATURE = 0x6D414220;
	
	private	Signature	signature_;
	private	UInt8Number	numInputChannels_;		//8
	private	UInt8Number	numOutputChannels_;		//9
	private	UInt32Number	offsetBcurve_;			//12
	private	UInt32Number	offsetMatrix_;			//16
	private	UInt32Number	offsetMcurve_;			//20
	private	UInt32Number	offsetClut_;			//24
	private	UInt32Number	offsetAcurve_;			//28
	private	byte[]		data_;					//32 .. end of array 

	//TODO: parse data_ into meaningful fields

	public LutAtoBType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"mAB ");
	}

	public LutAtoBType(byte[] byteArray) throws ICCProfileException
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
		if (this.signature_.intValue() != 0x6D414220)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.numInputChannels_ = new UInt8Number(byteArray, offset + 8);
		this.numOutputChannels_ = new UInt8Number(byteArray, offset + 9);
		this.offsetBcurve_ = new UInt32Number(byteArray, offset + 12);
		this.offsetMatrix_ = new UInt32Number(byteArray, offset + 16);
		this.offsetMcurve_ = new UInt32Number(byteArray, offset + 20);
		this.offsetClut_ = new UInt32Number(byteArray, offset + 24);
		this.offsetAcurve_ = new UInt32Number(byteArray, offset + 28);
		int n = byteArray.length - offset - 32;
		if (n > 0)
		{
			this.data_ = new byte[n];
			System.arraycopy(byteArray, 32, this.data_, 0, n);
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
	  if (numInputChannels_==null)
      throw new ICCProfileException("Lut16Type.toByteArray():data not set",ICCProfileException.InvalidDataValueException);

		int len = 32 + this.data_.length;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		all[8] = this.numInputChannels_.byteValue();
		all[9] = this.numOutputChannels_.byteValue();
		ICCUtils.appendByteArray(all, 12, this.offsetBcurve_);
		ICCUtils.appendByteArray(all, 16, this.offsetMatrix_);
		ICCUtils.appendByteArray(all, 20, this.offsetMcurve_);
		ICCUtils.appendByteArray(all, 24, this.offsetClut_);
		ICCUtils.appendByteArray(all, 28, this.offsetAcurve_);
		System.arraycopy(this.data_, 0, all, 32, this.data_.length);

		return all;
	}
	
	public int size()
	{
		assert(this.data_!=null);
		return 32 + this.data_.length;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	public UInt8Number getNumInputChannels()
	{
		return this.numInputChannels_;
	}
	public void setNumInputChannels(UInt8Number number)
	{
		this.numInputChannels_ = number;
	}
	public UInt8Number getNumOutputChannels()
	{
		return this.numOutputChannels_;
	}
	public void setNumOutputChannels(UInt8Number number)
	{
		this.numOutputChannels_ = number;
	}
	public UInt32Number getOffsetBcurve()
	{
		return this.offsetBcurve_;
	}
	public void setOffsetBcurve(UInt32Number bcurve)
	{
		this.offsetBcurve_ = bcurve;
	}
	public UInt32Number getOffsetMatrix()
	{
		return this.offsetMatrix_;
	}
	public void setOffsetMatrix(UInt32Number matrix)
	{
		this.offsetMatrix_ = matrix;
	}
	public UInt32Number getOffsetMcurve()
	{
		return this.offsetMcurve_;
	}
	public void setOffsetMcurve(UInt32Number mcurve)
	{
		this.offsetMcurve_ = mcurve;
	}
	public UInt32Number getOffsetClut()
	{
		return this.offsetClut_;
	}
	public void setOffsetClut(UInt32Number clut)
	{
		this.offsetClut_ = clut;
	}
	public UInt32Number getOffsetAcurve()
	{
		return this.offsetAcurve_;
	}
	public void setOffsetAcurve(UInt32Number acurve)
	{
		this.offsetAcurve_ = acurve;
	}
	public byte[] getData()
	{
		return this.data_;
	}
	public void setData(byte[] data)
	{
		this.data_ = data;
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
			sb.append("<lutAtoBType sig=\"\">");
		else
			sb.append("<lutAtoBType name=\""+name+"\" sig=\"\">");
		sb.append(signature_.toXmlString());
		sb.append(numInputChannels_.toXmlString("num_input_channels"));
		sb.append(numOutputChannels_.toXmlString("num_output_channels"));
		sb.append(offsetBcurve_.toXmlString("offset_B_curve"));
		sb.append(offsetMatrix_.toXmlString("offset_matrix"));
		sb.append(offsetMcurve_.toXmlString("offset_M_curve"));
		sb.append(offsetClut_.toXmlString("offset_CLUT"));
		sb.append(offsetAcurve_.toXmlString("offset_A_curve"));
		sb.append("<array name=\"data\" dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<data_.length; i++)
		{
			if (i > 0) sb.append(", ");
			sb.append(data_[i]);
		}
		sb.append("</dim></array>");
		sb.append("</lutAtoBType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
