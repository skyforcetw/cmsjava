package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.datatype.U8Fixed8Number;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * CurveType is a tag type for 1-D mapping table.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The curveType contains a 4-byte count value and a one-dimensional table of 2-byte values.
 * It contains a one-dimensional function which maps an input value in the domain of the
 * function to an output value in the range of the function.
 *
 * Data elements:
 * Position       Bytes        Content                           Type
 * 0..3				4			'curv'      (0x63757276)
 * 4..7				4			reseved 0
 * 8..11			4			Count of entries(n)				UInt32Number
 * 12..end			2*n			curve values					UInt16Number[]
 *
 * Note: 
 * If n=0, it's identity response
 * If n=1, the curve value is a gamma value as U8Fixed8Number, the exponent in y=x^gamma
 * If n>1, then:
 * The first entry is located at 0,0, the last entry at 1,0, and intermediate entries are uniformly spaced using an
 * increment of 1,0/(n-1). These entries are encoded as uInt16Numbers (i.e. the values represented by the
 * entries, which are in the range 0,0 to 1,0 are encoded in the range 0,0 to 65535,0). Function values between
 * the entries shall be obtained through linear interpolation.
 * If the input is the XYZ PCS, 1+(32767/32768) shall be mapped to the value 1,0. If the output is the XYZ PCS,
 * the value 1,0 shall be mapped to 1+(32767/32768).
 */
public class CurveType extends AbstractTagType
{
	public static final int SIGNATURE = 0x63757276;
	
	private	Signature		signature_;
	private	UInt32Number		numEntries_;		//count of entries
	private	U8Fixed8Number		gammaValue_;		//if numEntries_ = 1, then this is valid
	private	UInt16Number[]		curveValues_;		//array of curve values for interpolation

	public CurveType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"curv");
	}

	public CurveType(byte[] bytes) throws ICCProfileException
	{
		fromByteArray(bytes, 0, 0);
	}

	public void fromByteArray(byte[] bytes, int offset, int len) throws ICCProfileException
	{
		if (bytes == null)
			throw new ICCProfileException("CurveType.fromByteArray:byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > bytes.length)
			throw new ICCProfileException("CurveType.fromByteArray:index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(bytes, offset);
		if (this.signature_.intValue() != 0x63757276)
			throw new ICCProfileException("CurveType.fromByteArray: signature not correct!");

		this.numEntries_ = new UInt32Number(bytes, offset + 8);
		int n = this.numEntries_.intValue();
		if (n == 1)
		{
			this.gammaValue_ = new U8Fixed8Number(bytes, offset + 12);
		}
		else if (n > 1)
		{
			int idx = offset + 12;
			this.curveValues_ = new UInt16Number[n];
			for (int i=0; i<this.curveValues_.length; i++)
			{
				this.curveValues_[i] = new UInt16Number(bytes, idx);
				idx += 2;
			}
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (numEntries_==null)
			throw new ICCProfileException("CurveType.toByteArray:data not set",ICCProfileException.InvalidDataValueException);

		int n = this.numEntries_.intValue();
		int len = 12 + 2 * n;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.numEntries_);
		if (n == 1)
		{
			ICCUtils.appendByteArray(all, 12, this.gammaValue_);
		} 
		else if (n > 1)
		{
			int idx = 12;
			for (int i=0; i<n; i++)
			{
				ICCUtils.appendByteArray(all, idx, this.curveValues_[i]);
				idx += 2;
			}
		}

		return all;
	}
	
	public int size()
	{
		assert (this.numEntries_!=null);
		int n = this.numEntries_.intValue();
		return 2 * n + 12;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}

	/**
	 * Return the number of entries as an UInt32Number object.
	 * @return null if no data set, or object of UInt32Number.
	 */
	public UInt32Number getEntryCount()
	{
		return this.numEntries_;
	}
	
	/**
	 * Return the gamma value as an object of U8Fixed8Number, or null if not available.
	 * @return U8Fixed8Number or null
	 */
	public U8Fixed8Number getGammaValue()
	{
		return this.gammaValue_;
	}
	
	/**
	 * Set the gamma value for the curve type.
	 * @param gammaValue
	 */
	public void setGammaValue(U8Fixed8Number gammaValue)
	{
		this.gammaValue_ = gammaValue;
		this.numEntries_ = new UInt32Number(1);
	}
	
	/**
	 * Return 2D array of UInt16Number objects, or null if data not available.
	 * @return UInt16Number[n][2]
	 */
	public UInt16Number[] getCurveValues()
	{
		return this.curveValues_;
	}

	/**
	 * Set the curve values for the curve type.
	 * @param curveValues
	 */
	public void setCurveValues(UInt16Number[] curveValues)
	{
		this.curveValues_ = curveValues;
		this.numEntries_ = new UInt32Number(curveValues.length);
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
			sb.append("<curveType sig=\"curv\">");
		else
			sb.append("<curveType name=\""+name+"\" sig=\"curv\">");
		sb.append(signature_.toXmlString());
		sb.append(numEntries_.toXmlString("Number_of_entries"));
		if (numEntries_.intValue()==1)
			sb.append(gammaValue_.toXmlString("gamma"));
		else
		{
			sb.append("<array name=\"curve_values\" dims=\"1\" type=\"UInt16Number\"><dim index=\"0\">");
			for (int i=0; i<curveValues_.length; i++)
			{
				//sb.append(curveValues_[i].toXmlString());
				//use concise format to save space
				if (i > 0) sb.append(", ");
				sb.append(curveValues_[i].toString());
			}
			sb.append("</dim></array>");
		}
		sb.append("</curveType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
