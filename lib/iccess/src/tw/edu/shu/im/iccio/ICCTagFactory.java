package tw.edu.shu.im.iccio;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.tagtype.*;

/**
 * The tag factory creates the tagged data objects according to the tag type signature in the
 * given byte stream.  The static method createTagData accepts a byte array and assumes
 * it is read from the ICC profile file and contains only the tagged data block.
 * This follows the factory design pattern.
 */
public class ICCTagFactory
{
	/**
	 * Create an instance of any tag type determined by the signature at the
	 * very beginning of the byte array.
	 * The signature that determines the tag type to create is assumed to be
	 * the first 4 bytes in the given byte array.
	 * In addition, it loads different classes based on version where applicable.
	 *
	 * @param byteArray - the byte array containing the tagged data block
	 * @param version - ICC Profile version as given in the Header
	 * @return AbstractTagType with instantiation of a particular tag type.
	 */
	public static AbstractTagType createTagData(byte[] byteArray, int version) throws ICCProfileException
	{
		if (byteArray==null || byteArray.length<4)
			throw new ICCProfileException("bad byte array", ICCProfileException.IllegalArgumentException);
		
		Signature signature = new Signature(byteArray);
		switch (signature.intValue())
		{
		case ChromaticityType.SIGNATURE:
			return new ChromaticityType(byteArray);
		case ColorantOrderType.SIGNATURE:
			return new ColorantOrderType(byteArray);
		case ColorantTableType.SIGNATURE:
			return new ColorantTableType(byteArray);
		case CurveType.SIGNATURE:
			return new CurveType(byteArray);
		case DataType.SIGNATURE:
			return new DataType(byteArray);
		case DateTimeType.SIGNATURE:
			return new DateTimeType(byteArray);
		case Lut8Type.SIGNATURE:
			if (version >= 0x04200000)
				return new Lut8Type(byteArray);
			else
				return new Lut8TypeV3(byteArray);
		case Lut16Type.SIGNATURE:
			return new Lut16Type(byteArray);
		case LutAtoBType.SIGNATURE:
			return new LutAtoBType(byteArray);
		case LutBtoAType.SIGNATURE:
			return new LutBtoAType(byteArray);
		case MeasurementType.SIGNATURE:
			return new MeasurementType(byteArray);
		case MultiLocalizedUnicodeType.SIGNATURE:
			return new MultiLocalizedUnicodeType(byteArray);
		case ParametricCurveType.SIGNATURE:
			return new ParametricCurveType(byteArray);
		case NamedColor2Type.SIGNATURE:
			return new NamedColor2Type(byteArray);
		case ProfileSequenceDescType.SIGNATURE:
			return new ProfileSequenceDescType(byteArray);
		case ResponseCurveSet16Type.SIGNATURE:
			return new ResponseCurveSet16Type(byteArray);
		case S15Fixed16ArrayType.SIGNATURE:
			return new S15Fixed16ArrayType(byteArray);
		case SignatureType.SIGNATURE:
			return new SignatureType(byteArray);
		case TextType.SIGNATURE:
			return new TextType(byteArray);
		case U16Fixed16ArrayType.SIGNATURE:
			return new U16Fixed16ArrayType(byteArray);
		case UInt8ArrayType.SIGNATURE:
			return new UInt8ArrayType(byteArray);
		case UInt16ArrayType.SIGNATURE:
			return new UInt16ArrayType(byteArray);
		case UInt32ArrayType.SIGNATURE:
			return new UInt32ArrayType(byteArray);
		case UInt64ArrayType.SIGNATURE:
			return new UInt64ArrayType(byteArray);
		case ViewingConditionsType.SIGNATURE:
			return new ViewingConditionsType(byteArray);
		case XYZType.SIGNATURE:
			return new XYZType(byteArray);
		case TextDescriptionType.SIGNATURE:
			return new TextDescriptionType(byteArray);	//OLD version of ICC profile
		default:
			System.err.println("Unknown tag, possible old version of ICC, tag signature: "+Integer.toHexString(signature.intValue()));
			//throw new ICCProfileException("Unknown tag, possibly private");
		}
		return null;
	}
}
