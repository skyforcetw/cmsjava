package tw.edu.shu.im.iccio.tagtype;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt8Number;
import tw.edu.shu.im.iccio.datatype.UInt16Number;
import tw.edu.shu.im.iccio.datatype.S15Fixed16Number;
import tw.edu.shu.im.iccio.ICCUtils;
import tw.edu.shu.im.iccio.ICCProfileException;

/**
 * Lut8TypeV3 is a tag type for colour transform tables with 8-bit precision values used in ICC Profile version 3.x.
 * 
 * @author Ted Wen
 * @version 0.1
 * @update 2006-11-30
 * 
 * This structure represents a colour transform using tables of 8-bit precision. This type contains four processing
 * elements: a 3 by 3 matrix (which shall be the identity matrix unless the input colour space is XYZ), a set of one
 * dimensional input tables, a multidimensional lookup table, and a set of one dimensional output tables.
 * 
 * 0..3		4	'mft1' (6D667431h) [multi-function table with 1-byte precision] type signature
 * 4..7		4	reserved, must be set to 0
 * 8		1	Number of Input Channels (i) uInt8Number
 * 9		1	Number of Output Channels (o) uInt8Number
 * 10		1	Number of CLUT grid points (identical for each side) (g) uInt8Number
 * 11		1	Reserved for padding (fill with 00h)
 * 12..15	4	Encoded e00 parameter s15Fixed16Number
 * 16..19	4	Encoded e01 parameter s15Fixed16Number
 * 20..23	4	Encoded e02 parameter s15Fixed16Number
 * 24..27	4	Encoded e10 parameter s15Fixed16Number
 * 28..31	4	Encoded e11 parameter s15Fixed16Number
 * 32..35	4	Encoded e12 parameter s15Fixed16Number
 * 36..39	4	Encoded e20 parameter s15Fixed16Number
 * 40..43	4	Encoded e21 parameter s15Fixed16Number
 * 44..47	4	Encoded e22 parameter s15Fixed16Number
 * 48..m		Input tables uInt8Number[...]
 * m+1..n		CLUT values uInt8Number[...]
 * n+1..o		Output tables uInt8Number[...]
 */
public class Lut8TypeV3 extends Lut8Type
{
	public static final int NUM_INPUT_ENTRIES = 256;
	public static final int NUM_OUTPUT_ENTRIES = 256;

	public Lut8TypeV3()
	{
		super();
		//this.signature_ = new Signature(SIGNATURE);	//"mft1");
	}

	public Lut8TypeV3(byte[] byteArray) throws ICCProfileException
	{
		super(byteArray);
		//fromByteArray(byteArray, 0, 0);
	}

	public void fromByteArray(byte[] byteArray, int offset, int len) throws ICCProfileException
	{
		if (byteArray == null)
			throw new ICCProfileException("Lut8TypeV3.fromByteArray():byte array null", ICCProfileException.NullPointerException);
		
		if (offset < 0 || offset+len > byteArray.length)
			throw new ICCProfileException("Lut8TypeV3.fromByteArray():index out of range", ICCProfileException.IndexOutOfBoundsException);

		this.signature_ = new Signature(byteArray, offset);
		if (this.signature_.intValue() != 0x6D667431)
			throw new ICCProfileException("Lut8TypeV3.fromByteArray():incorrect signature", ICCProfileException.IncorrectSignatureException);

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
		this.numInputEntries_ = new UInt16Number((short)NUM_INPUT_ENTRIES); //byteArray, offset + 48);
		this.numOutputEntries_ = new UInt16Number((short)NUM_OUTPUT_ENTRIES); //byteArray, offset + 50);
		int i = this.numInputChannels_.intValue();
		int o = this.numOutputChannels_.intValue();
		int g = this.numGridPoints_.intValue();
		int n = this.numInputEntries_.intValue();
		int m = this.numOutputEntries_.intValue();
		int it = n * i;
		int ct = (int) (java.lang.Math.pow((double)g, (double)i) * o);
		int ot = m * o;
		int idx = offset + 48;
		if (idx + it + ct + ot > byteArray.length)
			throw new ICCProfileException("Lut8TypeV3.fromByteArray():byte array not big enough",ICCProfileException.WrongSizeException);

		this.inputTables_ = new UInt8Number[it];
		for (int ii=0; ii<it; ii++)
			this.inputTables_[ii] = new UInt8Number(byteArray, idx++);

		this.clutValues_ = new UInt8Number[ct];
		for (int ii=0; ii<ct; ii++)
			this.clutValues_[ii] = new UInt8Number(byteArray, idx++);

		this.outputTables_ = new UInt8Number[ot];
		for (int ii=0; ii<ot; ii++)
			this.outputTables_[ii] = new UInt8Number(byteArray, idx++);
	}

	public byte[] toByteArray() throws ICCProfileException
	{
    if (numInputChannels_==null)
      throw new ICCProfileException("Lut8TypeV3.toByteArray():data not set",ICCProfileException.InvalidDataValueException);
      
		int len = 48 + inputTables_.length + clutValues_.length + outputTables_.length;
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
		//ICCUtils.appendByteArray(all, 48, numInputEntries_);
		//ICCUtils.appendByteArray(all, 50, numOutputEntries_);
		int idx = 48;
		for (int i=0; i<inputTables_.length; i++)
			all[idx++] = inputTables_[i].byteValue();
		for (int i=0; i<clutValues_.length; i++)
			all[idx++] = clutValues_[i].byteValue();
		for (int i=0; i<outputTables_.length; i++)
			all[idx++] = outputTables_[i].byteValue();

		return all;
	}
	
	public int size()
	{
		assert(inputTables_!=null);
		return 48 + inputTables_.length + clutValues_.length + outputTables_.length;
	}

}
