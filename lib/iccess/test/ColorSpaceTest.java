
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ColorSpaceTest extends TestCase
{
	private int[] spaces_ = 
		{
			0x58595A20,0x4C616220,0x4C757620,0x59436272,0x59787920,0x52474220,0x47524159,
			0x48535620,0x484C5320,0x434D594B,0x434D5920,0x32434C52,0x33434C52,0x34434C52,
			0x35434C52,0x36434C52,0x37434C52,0x38434C52,0x39434C52,0x41434C52,0x42434C52,
			0x43434C52,0x44434C52,0x45434C52,0x46434C52
		};

	private String[] names_ = 
		{
			"XYZ ","Lab ","Luv ","YCbr","Yxy ","RGB ","GRAY","HSV ","HLS ",
			"CMYK","CMY ","2CLR","3CLR","4CLR","5CLR","6CLR","7CLR","8CLR",
			"9CLR","ACLR","BCLR","CCLR","DCLR","ECLR","FCLR"
		};

	private int[] tags_ = 
		{
			ColorSpace.XYZ_DATA,
			ColorSpace.LAB_DATA,
			ColorSpace.LUV_DATA,
			ColorSpace.YCBCR_DATA,
			ColorSpace.YXY_DATA,
			ColorSpace.RGB_DATA,
			ColorSpace.GRAY_DATA,
			ColorSpace.HSV_DATA, 
			ColorSpace.HLS_DATA,
			ColorSpace.CMYK_DATA,
			ColorSpace.CMY_DATA, 
			ColorSpace.COLOR2_DATA,
			ColorSpace.COLOR3_DATA,
			ColorSpace.COLOR4_DATA,
			ColorSpace.COLOR5_DATA,
			ColorSpace.COLOR6_DATA,
			ColorSpace.COLOR7_DATA,
			ColorSpace.COLOR8_DATA,
			ColorSpace.COLOR9_DATA,
			ColorSpace.COLOR10_DATA,
			ColorSpace.COLOR11_DATA,
			ColorSpace.COLOR12_DATA,
			ColorSpace.COLOR13_DATA,
			ColorSpace.COLOR14_DATA,
			ColorSpace.COLOR15_DATA
		};

	public void setUp()
	{
	}

	private void compareBytes(byte[] expected, byte[] result)
	{
		assertEquals("two byte array not same size",expected.length, result.length);
		for (int i=0; i<result.length; i++)
			assertEquals("byte "+i, expected[i], result[i]);
	}

	private void compareBytes(byte[] expected, int offset, int len, byte[] result)
	{
		for (int i=0; i<len; i++)
			assertEquals("byte "+i, expected[i+offset], result[i]);
	}

	public void testConstructors()
	{
		try
		{
			ColorSpace empty = new ColorSpace();

			byte[] ba = new byte[]
			{
				(byte)0x4C,(byte)0x61,(byte)0x62,(byte)0x20
			};
			ColorSpace inst1 = new ColorSpace(ba);
			byte[] bac = inst1.toByteArray();
			compareBytes(ba, bac);

			ColorSpace cs = new ColorSpace(ColorSpace.COLOR15_DATA);
			assertTrue(cs.is15ColorData());
		}
		catch (ICCProfileException e)
		{
			System.err.println("Error occurred:"+e.getMessage());
		}
	}

	public void testFromByteArray()
	{
		ColorSpace data = new ColorSpace();
		byte[] ba = new byte[]
		{
			(byte)0x58,(byte)0x59,(byte)0x5A,(byte)0x20,
			(byte)0x4C,(byte)0x61,(byte)0x62,(byte)0x20
		};
		try
		{
			data.fromByteArray(ba, 0, 4);
			assertTrue(data.isXYZData());
			byte[] res = data.toByteArray();
			compareBytes(ba,0,0,res);
			data.fromByteArray(ba, 4, 4);
			assertTrue(data.isLabData());
			byte[] re2 = data.toByteArray();
			compareBytes(ba,0,0,re2);
		}
		catch (ICCProfileException e)
		{
			assertFalse("should not get here",true);
		}
	}

	public void testSetters()
	{
		ColorSpace cs = new ColorSpace();
		for (int i=0; i<tags_.length; i++)
		{
			cs.setColorSpace(tags_[i]);
			assertEquals(spaces_[i], cs.intValue());
			assertEquals(names_[i], cs.toString());
		}
	}

	public void testSetXYZData()
	{
		ColorSpace cs = new ColorSpace();
		cs.setXYZData();
		assertTrue(cs.isXYZData());
	}

	public void testSetLabData()
	{
		ColorSpace cs = new ColorSpace();
		cs.setLabData();
		assertTrue(cs.isLabData());
	}

	public void testSetLuvData()
	{
		ColorSpace cs = new ColorSpace();
		cs.setLuvData();
		assertTrue(cs.isLuvData());
	}

	public void testSetYCbCrData()
	{
		ColorSpace cs = new ColorSpace();
		cs.setYCbCrData();
		assertTrue(cs.isYCbCrData());
	}

	public void testSetYxyData()
	{
		ColorSpace cs = new ColorSpace();
		cs.setYxyData();
		assertTrue(cs.isYxyData());
	}

	public void testSetRgbData()
	{
		ColorSpace cs = new ColorSpace();
		cs.setRgbData();
		assertTrue(cs.isRgbData());
	}

	public void testSetGrayData()
	{
		ColorSpace cs = new ColorSpace();
		cs.setGrayData();
		assertTrue(cs.isGrayData());
	}

	public void testSetHsvData()
	{
		ColorSpace cs = new ColorSpace();
		cs.setHsvData();
		assertTrue(cs.isHsvData());
	}

	public void testSetHlsData()
	{
		ColorSpace cs = new ColorSpace();
		cs.setHlsData();
		assertTrue(cs.isHlsData());
	}

	public void testSetCmykData()
	{
		ColorSpace cs = new ColorSpace();
		cs.setCmykData();
		assertTrue(cs.isCmykData());
	}

	public void testSetCmyData()
	{
		ColorSpace cs = new ColorSpace();
		cs.setCmyData();
		assertTrue(cs.isCmyData());
	}

	public void testSet2ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set2ColorData();
		assertTrue(cs.is2ColorData());
	}

	public void testSet3ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set3ColorData();
		assertTrue(cs.is3ColorData());
	}

	public void testSet4ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set4ColorData();
		assertTrue(cs.is4ColorData());
	}

	public void testSet5ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set5ColorData();
		assertTrue(cs.is5ColorData());
	}

	public void testSet6ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set6ColorData();
		assertTrue(cs.is6ColorData());
	}

	public void testSet7ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set7ColorData();
		assertTrue(cs.is7ColorData());
	}

	public void testSet8ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set8ColorData();
		assertTrue(cs.is8ColorData());
	}

	public void testSet9ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set9ColorData();
		assertTrue(cs.is9ColorData());
	}

	public void testSet10ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set10ColorData();
		assertTrue(cs.is10ColorData());
	}

	public void testSet11ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set11ColorData();
		assertTrue(cs.is11ColorData());
	}

	public void testSet12ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set12ColorData();
		assertTrue(cs.is12ColorData());
	}

	public void testSet13ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set13ColorData();
		assertTrue(cs.is13ColorData());
	}

	public void testSet14ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set14ColorData();
		assertTrue(cs.is14ColorData());
	}

	public void testSet15ColorData()
	{
		ColorSpace cs = new ColorSpace();
		cs.set15ColorData();
		assertTrue(cs.is15ColorData());
	}

	public void testIsXYZData()
	{
		//done in testSetXXXX
	}

	public void testIsLabData()
	{
	}

	public void testIsLuvData()
	{
	}

	public void testIsYCbCrData()
	{
	}

	public void testIsYxyData()
	{
	}

	public void testIsRgbData()
	{
	}

	public void testIsGrayData()
	{
	}

	public void testIsHsvData()
	{
	}

	public void testIsHlsData()
	{
	}

	public void testIsCmykData()
	{
	}

	public void testIsCmyData()
	{
	}

	public void testIs2ColorData()
	{
	}

	public void testIs3ColorData()
	{
	}

	public void testIs4ColorData()
	{
	}

	public void testIs5ColorData()
	{
	}

	public void testIs6ColorData()
	{
	}

	public void testIs7ColorData()
	{
	}

	public void testIs8ColorData()
	{
	}

	public void testIs9ColorData()
	{
	}

	public void testIs10ColorData()
	{
	}

	public void testIs11ColorData()
	{
	}

	public void testIs12ColorData()
	{
	}

	public void testIs13ColorData()
	{
	}

	public void testIs14ColorData()
	{
	}

	public void testIs15ColorData()
	{
	}

	public void testToString()
	{
		//done in testSetters()
	}


}
