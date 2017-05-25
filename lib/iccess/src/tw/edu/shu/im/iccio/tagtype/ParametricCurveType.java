package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.datatype.S15Fixed16Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * ParametricCurveType is a tag type for 1-D curve of parametric functions.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * The parametricCurveType describes a one-dimensional curve by specifying one of a predefined set of
 * functions using the parameters.
 * <code>
 * 0..3		4	'para' (70617261h) type signature
 * 4..7		4	reserved, must be set to 0
 * 8..9		2	encoded value of the function type uInt16Number (see table 47)
 * 10..11	2	reserved, must be set to 0
 * 12..end		one or more parameters (see table 47) s15Fixed16Number [...]
 * </code>
 * <code>
 * encoded value	parameter bytes
 * 0				4
 * 1				12
 * 2				16
 * 3				20
 * 4				28
 * </code>
 */
public class ParametricCurveType extends AbstractTagType
{
	public static final int SIGNATURE = 0x70617261;
	
	private	Signature			signature_;
	private	UInt16Number		funcTypeCode_;
	private	S15Fixed16Number[]	params_;

	private	int[]	paramBytes_ = new int[]{4,12,16,20,28};

	public ParametricCurveType()
	{
		this.signature_ = new Signature(SIGNATURE);	//"para");
	}

	public ParametricCurveType(byte[] byteArray) throws ICCProfileException
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
		if (this.signature_.intValue() != 0x70617261)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.funcTypeCode_ = new UInt16Number(byteArray, offset + 8);
		int code = this.funcTypeCode_.intValue();
		this.params_ = new S15Fixed16Number[paramBytes_[code]];
		if (byteArray.length - offset < 12 + this.params_.length * S15Fixed16Number.SIZE)
			throw new ICCProfileException("byte array contains less bytes than specified by params",ICCProfileException.WrongSizeException);
		int idx = offset + 12;
		for (int i=0; i<params_.length; i++)
		{
			this.params_[i] = new S15Fixed16Number(byteArray, idx);
			idx += S15Fixed16Number.SIZE;
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.funcTypeCode_==null || this.params_==null)
			throw new ICCProfileException("data not set",ICCProfileException.InvalidDataValueException);

		int len = 12 + this.params_.length * S15Fixed16Number.SIZE;
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		ICCUtils.appendByteArray(all, 8, this.funcTypeCode_);
		int idx = 12;
		for (int i=0; i<this.params_.length; i++)
		{
			ICCUtils.appendByteArray(all, idx, this.params_[i]);
			idx += S15Fixed16Number.SIZE;
		}

		return all;
	}
	
	public int size()
	{
		assert(this.params_!=null);
		return 12 + this.params_.length * S15Fixed16Number.SIZE;
	}

	public Signature getSignature()
	{
		return this.signature_;
	}
	/**
	 * Return the current function type encoded value as an UInt16Number object.
	 */
	public UInt16Number getFunctionTypeCode()
	{
		return this.funcTypeCode_;
	}

	/**
	 * Assign the current function type code.
	 * The code value must be from 0 to 4, otherwise the function fails.
	 */
	public void setFunctionTypeCode(UInt16Number code)
	{
		if (code!=null && code.intValue()>=0 && code.intValue()<5)
			this.funcTypeCode_ = code;
	}

	/**
	 * Return the number of bytes for the function type.
	 * The values are 4,12,16,20,28 that map code values of 0..4.
	 * If function type not specified, this method returns -1.
	 */
	public int getFunctionTypeBytes()
	{
		if (funcTypeCode_==null)
			return -1;
		int n = this.funcTypeCode_.intValue();
		if (n >= 0 && n < this.paramBytes_.length)
			return this.paramBytes_[n];
		return -1;
	}

	public S15Fixed16Number[] getParameters()
	{
		return this.params_;
	}

	/**
	 * Add a S15Fixed16Number value to the end of the array.
	 * @param number - number object of S15Fixed16Number.
	 */
	public void addParameter(S15Fixed16Number number)
	{
		if (this.params_ == null)
			this.params_ = new S15Fixed16Number[1];
		else
		{
			S15Fixed16Number[] bac = this.params_;
			this.params_ = new S15Fixed16Number[bac.length + 1];
			for (int i=0; i<bac.length; i++)
				this.params_[i] = bac[i];
		}
		this.params_[this.params_.length-1] = number;
	}

	/**
	 * Add an array of values to the end of the array.
	 * If the array is empty, it is created.
	 * @param numbers - array of doubles
	 */
	public void addParameters(double[] numbers) throws ICCProfileException
	{
		assert( numbers != null );
		int len = numbers.length;
		int idx = 0;
		if (this.params_ == null)
			this.params_ = new S15Fixed16Number[len];
		else
		{
			S15Fixed16Number[] bac = this.params_;
			idx = bac.length;
			this.params_ = new S15Fixed16Number[bac.length + len];
			for (int i=0; i<bac.length; i++)
				this.params_[i] = bac[i];
		}
		for (int i=0; i<len; i++)
			this.params_[idx++] = new S15Fixed16Number(numbers[i]);
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
			sb.append("<parametricCurveType sig=\"para\">");
		else
			sb.append("<parametricCurveType name=\""+name+"\" sig=\"para\">");
		sb.append(signature_.toXmlString());
		sb.append(funcTypeCode_.toXmlString());
		sb.append("<array dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<params_.length; i++)
		{
			sb.append(params_[i].toXmlString());
		}
		sb.append("</dim></array>");
		sb.append("</parametricCurveType>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
