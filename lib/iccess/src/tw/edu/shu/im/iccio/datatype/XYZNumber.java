package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.Streamable;
import tw.edu.shu.im.iccio.ICCProfileException;
import tw.edu.shu.im.iccio.ICCUtils;

/**
 * XYZNumber is a complex data type containing CIE X,Y,Z values. 
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * Each value of an XYZNumber object is of type S15Fixed16Number data type.
 * 
 */
public class XYZNumber implements Streamable
{
	public static final int SIZE = 12;

	private S15Fixed16Number ciex_ = new S15Fixed16Number();
	private S15Fixed16Number ciey_ = new S15Fixed16Number();
	private S15Fixed16Number ciez_ = new S15Fixed16Number();

	public XYZNumber()
	{
	}

	public XYZNumber(byte[] asByteArray) throws ICCProfileException
	{
		fromByteArray(asByteArray, 0, SIZE);
	}

	public XYZNumber(byte[] asByteArray, int offset) throws ICCProfileException
	{
		fromByteArray(asByteArray, offset, SIZE);
	}

	public XYZNumber(XYZNumber copy)
	{
		this.ciex_ = new S15Fixed16Number(copy.ciex_);
		this.ciey_ = new S15Fixed16Number(copy.ciey_);
		this.ciez_ = new S15Fixed16Number(copy.ciez_);
	}

	public XYZNumber(double x, double y, double z) throws ICCProfileException
	{
		this.ciex_ = new S15Fixed16Number(x);
		this.ciey_ = new S15Fixed16Number(y);
		this.ciez_ = new S15Fixed16Number(z);
	}

	public void fromByteArray(byte[] asByteArray, int offset, int len) throws ICCProfileException
	{
		if (asByteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);

		if (offset < 0)
			throw new ICCProfileException("offset < 0", ICCProfileException.IndexOutOfBoundsException);

		if (len != SIZE)
			throw new ICCProfileException("len parameter is not equal to SIZE", ICCProfileException.WrongSizeException);

		if (asByteArray.length < offset+len)
			throw new ICCProfileException("offset outside byte array", ICCProfileException.IndexOutOfBoundsException);

		ciex_.fromByteArray(asByteArray, offset + 0, 4);
		ciey_.fromByteArray(asByteArray, offset + 4, 4);
		ciez_.fromByteArray(asByteArray, offset + 8, 4);
	}

	public byte[] toByteArray() throws ICCProfileException
	{
		if (this.ciex_==null || this.ciey_==null || this.ciez_==null)
			throw new ICCProfileException("data not set", ICCProfileException.InvalidDataValueException);

		byte[] all = new byte[SIZE];

		ICCUtils.appendByteArray(all, 0, this.ciex_);
		ICCUtils.appendByteArray(all, 4, this.ciey_);
		ICCUtils.appendByteArray(all, 8, this.ciez_);

		return all;
	}

	public int size()
	{
		return this.SIZE;
	}

	public S15Fixed16Number getCIEX()
	{
		return this.ciex_;
	}
	public S15Fixed16Number getCIEY()
	{
		return this.ciey_;
	}
	public S15Fixed16Number getCIEZ()
	{
		return this.ciez_;
	}

	/**
	 * Make a string of this object in the format of "CIEX,CIEY,CIEZ".
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(ciex_.toString());
		sb.append(',');
		sb.append(ciey_.toString());
		sb.append(',');
		sb.append(ciez_.toString());
		return sb.toString();
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
			sb.append("<XYZNumber>");
		else
			sb.append("<XYZNumber name=\""+name+"\">");
		sb.append(ciex_.toXmlString("CIEX"));
		sb.append(ciey_.toXmlString("CIEY"));
		sb.append(ciez_.toXmlString("CIEZ"));
		sb.append("</XYZNumber>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
