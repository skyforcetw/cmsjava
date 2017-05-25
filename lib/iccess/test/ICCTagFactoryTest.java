
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ICCTagFactoryTest extends TestCase
{
	private String[] tags_ = new String[]
	{
    "chrm","clro","clrt","curv","data",
    "dtim","mft2","mft1","mAB ","mBA ","meas",
    "mluc","ncl2","para","pseq",
    "rcs2","sf32","sig ","text",
    "uf32","ui16","ui32","ui64","ui08",
    "view","XYZ "
	};
	private int[] sizes_ = new int[]
	{
    20,13,50,12,12,
    20,52,52,32,32,36,
    16,84,28,12,
    12,12,12,12,
    12,10,12,16,9,
    36,20
	};
	private String[] classNames_ = new String[]
	{
	   "ChromaticityType","ColorantOrderType","ColorantTableType","CurveType","DataType",
	   "DateTimeType","Lut16Type","Lut8Type","LutAtoBType","LutBtoAType","MeasurementType",
	   "MultiLocalizedUnicodeType","NamedColor2Type","ParametricCurveType","ProfileSequenceDescType",
	   "ResponseCurveSet16Type","S15Fixed16ArrayType","SignatureType","TextType",
	   "U16Fixed16ArrayType","UInt16ArrayType","UInt32ArrayType","UInt64ArrayType","UInt8ArrayType",
	   "ViewingConditionsType","XYZType"
	};

	public void testCreateTagData()
	{
			for (int i=0; i<tags_.length; i++)
			{
				byte[] sig = tags_[i].getBytes();
				byte[] data = new byte[sizes_[i]];
				System.arraycopy(sig,0,data,0,sig.length);
				if (i==1 || i==2)
				{
          data[11] = 1;
        }
        
        try
        {
  				AbstractTagType at = ICCTagFactory.createTagData(data, 0x4200000);
	   			assertEquals("tw.edu.shu.im.iccio.tagtype."+classNames_[i], at.getClass().getName());
				}
				catch (ICCProfileException e)
				{
          System.out.println("Error on "+classNames_[i]+":"+tags_[i]);
          assertFalse(e.getMessage(), true);
        }
			}
	}

}

