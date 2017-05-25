package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt8Number;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.datatype.S15Fixed16Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * Lut16Type is a tag type for 3x3 matrix.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 * 
 * This type contains four processing elements: a 3 by 3 matrix (which shall be the identity matrix unless the input colour space is XYZ), a
 * set of one dimensional input tables, a multidimensional lookup table, and a set of one dimensional output tables.
 * 
 * 0..3		4 'mft2' (6D667432h) [multi-function table with 2-byte precision] type signature
 * 4..7		4 reserved, must be set to 0
 * 8		1 Number of Input Channels (i) uInt8Number
 * 9		1 Number of Output Channels (o) uInt8Number
 * 10		1 Number of CLUT grid points (identical for each side) (g) uInt8Number
 * 11		1 Reserved for padding (required to be 00h)
 * 12..15	4 Encoded e00 parameter s15Fixed16Number
 * 16..19	4 Encoded e01 parameter s15Fixed16Number
 * 20..23	4 Encoded e02 parameter s15Fixed16Number
 * 24..27	4 Encoded e10 parameter s15Fixed16Number
 * 28..31	4 Encoded e11 parameter s15Fixed16Number
 * 32..35	4 Encoded e12 parameter s15Fixed16Number
 * 36..39	4 Encoded e20 parameter s15Fixed16Number
 * 40..43	4 Encoded e21 parameter s15Fixed16Number
 * 44..47	4 Encoded e22 parameter s15Fixed16Number
 * 48..49	4 Number of input table entries (n) uInt16Number
 * 50..51	4 Number of output table entries (m) uInt16Number
 * 52..51+(n*i*2)	n*i*2	Input tables uInt16Number[...]
 * 52+(n*i*2)..51+(n*i*2)+(g^i*o*2)	g^i*o*2 CLUT values uInt16Number[...]
 * 52+(n*i*2)+(g^i*o*2)..end		m*o*2	Output tables uInt16Number[...]
 */
public class Lut16Type extends AbstractTagType
{
	public static final int SIGNATURE = 0x6D667432;
	
	protected	Signature	signature_;
	protected	UInt8Number		numInputChannels_;		//8		i
	protected	UInt8Number		numOutputChannels_;		//9		o
	protected	UInt8Number		numGridPoints_;			//10	g
	protected	UInt8Number		padding_;				//11 (zero)
	protected	S15Fixed16Number	e00_;				//12
	protected	S15Fixed16Number	e01_;				//16
	protected	S15Fixed16Number	e02_;				//20
	protected	S15Fixed16Number	e10_;				//24
	protected	S15Fixed16Number	e11_;				//28
	protected	S15Fixed16Number	e12_;				//32
	protected	S15Fixed16Number	e20_;				//36
	protected	S15Fixed16Number	e21_;				//40
	protected	S15Fixed16Number	e22_;				//44
	protected	UInt16Number	numInputEntries_;		//48	n
	protected	UInt16Number	numOutputEntries_;		//50	m
	protected	UInt16Number[]	inputTables_;			//52	n * i
	protected	UInt16Number[]	clutValues_;			//		g^i * o
	protected	UInt16Number[]	outputTables_;			//		m * o

	public Lut16Type()
	{
		this.signature_ = new Signature(SIGNATURE);	//"mft2");
	}

	public Lut16Type(byte[] byteArray) throws ICCProfileException
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
		if (this.signature_.intValue() != 0x6D667432)
			throw new ICCProfileException("incorrect signature", ICCProfileException.IncorrectSignatureException);

		this.numInputChannels_ = new UInt8Number(byteArray, offset + 8);
		this.numOutputChannels_ = new UInt8Number(byteArray, offset + 9);
		this.numGridPoints_ = new UInt8Number(byteArray, offset + 10);
		this.e00_ = new S15Fixed16Number(byteArray, offset + 12);
		this.e01_ = new S15Fixed16Number(byteArray, offset + 16);
		this.e02_ = new S15Fixed16Number(byteArray, offset + 20);
		this.e10_ = new S15Fixed16Number(byteArray, offset + 24);
		this.e11_ = new S15Fixed16Number(byteArray, offset + 28);
		this.e12_ = new S15Fixed16Number(byteArray, offset + 32);
		this.e20_ = new S15Fixed16Number(byteArray, offset + 36);
		this.e21_ = new S15Fixed16Number(byteArray, offset + 40);
		this.e22_ = new S15Fixed16Number(byteArray, offset + 44);
		this.numInputEntries_ = new UInt16Number(byteArray, offset + 48);
		this.numOutputEntries_ = new UInt16Number(byteArray, offset + 50);
		int i = this.numInputChannels_.intValue();
		int o = this.numOutputChannels_.intValue();
		int g = this.numGridPoints_.intValue();
		int n = this.numInputEntries_.intValue();
		int m = this.numOutputEntries_.intValue();
		int it = n * i;
		int ct = (int) (java.lang.Math.pow((double)g, (double)i) * o);
		int ot = m * o;
		int idx = offset + 52;
		if (idx + 2*(it + ct + ot) > byteArray.length)
			throw new ICCProfileException("byte array not big enough",ICCProfileException.WrongSizeException);

		this.inputTables_ = new UInt16Number[it];
		for (int ii=0; ii<it; ii++)
		{
			this.inputTables_[ii] = new UInt16Number(byteArray, idx);
			idx += 2;
		}

		this.clutValues_ = new UInt16Number[ct];
		for (int ii=0; ii<ct; ii++)
		{
			this.clutValues_[ii] = new UInt16Number(byteArray, idx);
			idx += 2;
		}

		this.outputTables_ = new UInt16Number[ot];
		for (int ii=0; ii<ot; ii++)
		{
			this.outputTables_[ii] = new UInt16Number(byteArray, idx);
			idx += 2;
		}
	}

	public byte[] toByteArray() throws ICCProfileException
	{
	  if (numInputChannels_==null)
      throw new ICCProfileException("Lut16Type.toByteArray():data not set",ICCProfileException.InvalidDataValueException);
      
		int len = 52 + 2*(inputTables_.length + clutValues_.length + outputTables_.length);
		byte[] all = new byte[len];
		ICCUtils.appendByteArray(all, 0, this.signature_);
		all[8] = numInputChannels_.byteValue();
		all[9] = numOutputChannels_.byteValue();
		all[10] = numGridPoints_.byteValue();
		ICCUtils.appendByteArray(all, 12, this.e00_);
		ICCUtils.appendByteArray(all, 16, this.e01_);
		ICCUtils.appendByteArray(all, 20, this.e02_);
		ICCUtils.appendByteArray(all, 24, this.e10_);
		ICCUtils.appendByteArray(all, 28, this.e11_);
		ICCUtils.appendByteArray(all, 32, this.e12_);
		ICCUtils.appendByteArray(all, 36, this.e20_);
		ICCUtils.appendByteArray(all, 40, this.e21_);
		ICCUtils.appendByteArray(all, 44, this.e22_);
		ICCUtils.appendByteArray(all, 48, numInputEntries_);
		ICCUtils.appendByteArray(all, 50, numOutputEntries_);
		int idx = 52;
		for (int i=0; i<inputTables_.length; i++)
		{
			ICCUtils.appendByteArray(all, idx, inputTables_[i]);
			idx += 2;
		}
		for (int i=0; i<clutValues_.length; i++)
		{
			ICCUtils.appendByteArray(all, idx, clutValues_[i]);
			idx += 2;
		}
		for (int i=0; i<outputTables_.length; i++)
		{
			ICCUtils.appendByteArray(all, idx, outputTables_[i]);
			idx += 2;
		}

		return all;
	}
	
	public int size()
	{
		assert(inputTables_!=null);
		return 52 + 2*(inputTables_.length + clutValues_.length + outputTables_.length);
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
	public UInt8Number getNumGridPoints()
	{
		return this.numGridPoints_;
	}
	public void setNumGridPoints(UInt8Number number)
	{
		this.numGridPoints_ = number;
	}
	public S15Fixed16Number getE00()
	{
		return this.e00_;
	}
	public void setE00(S15Fixed16Number number)
	{
		this.e00_ = number;
	}
	public S15Fixed16Number getE01()
	{
		return this.e01_;
	}
	public void setE01(S15Fixed16Number number)
	{
		this.e01_ = number;
	}
	public S15Fixed16Number getE02()
	{
		return this.e02_;
	}
	public void setE02(S15Fixed16Number number)
	{
		this.e02_ = number;
	}
	public S15Fixed16Number getE10()
	{
		return this.e10_;
	}
	public void setE10(S15Fixed16Number number)
	{
		this.e10_ = number;
	}
	public S15Fixed16Number getE11()
	{
		return this.e11_;
	}
	public void setE11(S15Fixed16Number number)
	{
		this.e11_ = number;
	}
	public S15Fixed16Number getE12()
	{
		return this.e12_;
	}
	public void setE12(S15Fixed16Number number)
	{
		this.e12_ = number;
	}
	public S15Fixed16Number getE20()
	{
		return this.e20_;
	}
	public void setE20(S15Fixed16Number number)
	{
		this.e20_ = number;
	}
	public S15Fixed16Number getE21()
	{
		return this.e21_;
	}
	public void setE21(S15Fixed16Number number)
	{
		this.e21_ = number;
	}
	public S15Fixed16Number getE22()
	{
		return this.e22_;
	}
	public void setE22(S15Fixed16Number number)
	{
		this.e22_ = number;
	}
	public UInt16Number getNumInputEntries()
	{
		return this.numInputEntries_;
	}
	public void setNumInputEntries(UInt16Number number)
	{
		this.numInputEntries_ = number;
	}
	public UInt16Number getNumOutputEntries()
	{
		return this.numOutputEntries_;
	}
	public void setNumOutputEntries(UInt16Number number)
	{
		this.numOutputEntries_ = number;
	}
	public UInt16Number[] getInputTables()
	{
		return this.inputTables_;
	}
	public void setInputTables(UInt16Number[] table)
	{
		this.inputTables_ = table;
	}
	public UInt16Number[] getClutValues()
	{
		return this.clutValues_;
	}
	public void setClutValues(UInt16Number[] table)
	{
		this.clutValues_ = table;
	}
	public UInt16Number[] getOutputTables()
	{
		return this.outputTables_;
	}
	public void setOutputTables(UInt16Number[] table)
	{
		this.outputTables_ = table;
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
			sb.append("<lut16Type sig=\"\">");
		else
			sb.append("<lut16Type name=\""+name+"\" sig=\"\">");
		sb.append(signature_.toXmlString());
		sb.append(numInputChannels_.toXmlString("num_input_channels"));
		sb.append(numOutputChannels_.toXmlString("num_output_channels"));
		sb.append(numGridPoints_.toXmlString("num_grid_points"));
		//sb.append(padding_.toXmlString());
		sb.append(e00_.toXmlString("E00"));
		sb.append(e01_.toXmlString("E01"));
		sb.append(e02_.toXmlString("E02"));
		sb.append(e10_.toXmlString("E10"));
		sb.append(e11_.toXmlString("E11"));
		sb.append(e12_.toXmlString("E12"));
		sb.append(e20_.toXmlString("E20"));
		sb.append(e21_.toXmlString("E21"));
		sb.append(e22_.toXmlString("E22"));
		sb.append(numInputEntries_.toXmlString("num_input_entries"));
		sb.append(numOutputEntries_.toXmlString("num_output_entries"));
		sb.append("<array name=\"input_table\" dims=\"1\" type=\"UInt16Number\"><dim index=\"0\">");
		for (int i=0; i<inputTables_.length; i++)
		{
			//sb.append(inputTables_[i].toXmlString());
			if (i > 0) sb.append(", ");
			sb.append(inputTables_[i].intValue());
		}
		sb.append("</dim></array>");
		sb.append("<array name=\"CLUT\" dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<clutValues_.length; i++)
		{
			//sb.append(clutValues_[i].toXmlString());
			if (i > 0) sb.append(", ");
			sb.append(clutValues_[i].intValue());
		}
		sb.append("</dim></array>");
		sb.append("<array name=\"output_table\" dims=\"1\"><dim index=\"0\">");
		for (int i=0; i<outputTables_.length; i++)
		{
			//sb.append(outputTables_[i].toXmlString());
			if (i > 0) sb.append(", ");
			sb.append(outputTables_[i].intValue());
		}
		sb.append("</dim></array>");
		sb.append("</lut16Type>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
