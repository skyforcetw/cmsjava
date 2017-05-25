
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ColorantCodeTest extends TestCase
{
	private class Code
	{
		public short code_;
		public String typeName_;
		public double x1_, y1_;    //channel 1
		public double x2_, y2_;   //channel 2
		public double x3_, y3_;   //channel 3
		public Code(short code, String name, double x1, double y1, double x2, double y2, double x3, double y3)
		{
			this.code_ = code;
			this.typeName_ = name;
			this.x1_ = x1;
			this.y1_ = y1;
			this.x2_ = x2;
			this.y2_ = y2;
			this.x3_ = x3;
			this.y3_ = y3;
		}
	}
	private Code[] codes_ = new Code[]
	{
		new Code((short)0,"unknown",0.,0.,0.,0.,0.,0.),
		new Code((short)1,"ITU-R BT.709",0.64,0.33,0.3,0.6,0.15,0.6),
		new Code((short)2,"SMPTE RP145-1994",0.63,0.34,0.31,0.595,0.155,0.07),
		new Code((short)3,"EBU Tech.3213-E",0.64,0.33,0.29,0.6,0.15,0.06),
		new Code((short)4,"P22",0.625,0.34,0.28,0.605,0.155,0.07)
	};
	private byte[] ba = new byte[]
	{
		(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00
	};

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
			ColorantCode empty = new ColorantCode();
			assertEquals(0, empty.intValue());

			ColorantCode inst1 = new ColorantCode(ba);
			assertEquals(0x8000, inst1.intValue());
			byte[] bac = inst1.toByteArray();
			//compareBytes(ba, bac);

		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testFromByteArray()
	{
		ColorantCode data = new ColorantCode();
		try
		{
			data.fromByteArray(ba, 0, 2);	//UInt16Number.SIZE
			assertEquals(0x8000,data.intValue());
			//byte[] res = data.toByteArray();
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
		}

		try
		{
			data.fromByteArray(ba, -1, ba.length);
			assertFalse("index out of bounds, should raise exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(e.getMessage(),ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(ba, ba.length, 2);
			assertFalse("index out of bounds, should raise exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(e.getMessage(),ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(null, 0, 0);
			assertFalse("should raise null pointer exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(e.getMessage(),ICCProfileException.NullPointerException, e.getType());
		}
	}

	public void testGetName()
	{
		for (int i=0; i<codes_.length; i++)
		{
			ColorantCode cc = new ColorantCode(i);
			assertEquals(codes_[i].typeName_, cc.getName());
		}
	}

	public void testGetX1()
	{
		for (int i=0; i<codes_.length; i++)
		{
			ColorantCode cc = new ColorantCode(i);
			assertEquals(codes_[i].x1_, cc.getX1());
		}
	}

	public void testGetX2()
	{
		for (int i=0; i<codes_.length; i++)
		{
			ColorantCode cc = new ColorantCode(i);
			assertEquals(codes_[i].x2_, cc.getX2());
		}
	}

	public void testGetX3()
	{
		for (int i=0; i<codes_.length; i++)
		{
			ColorantCode cc = new ColorantCode(i);
			assertEquals(codes_[i].x3_, cc.getX3());
		}
	}

	public void testGetY1()
	{
		for (int i=0; i<codes_.length; i++)
		{
			ColorantCode cc = new ColorantCode(i);
			assertEquals(codes_[i].y1_, cc.getY1());
		}
	}

	public void testGetY2()
	{
		for (int i=0; i<codes_.length; i++)
		{
			ColorantCode cc = new ColorantCode(i);
			assertEquals(codes_[i].y2_, cc.getY2());
		}
	}

	public void testGetY3()
	{
		for (int i=0; i<codes_.length; i++)
		{
			ColorantCode cc = new ColorantCode(i);
			assertEquals(codes_[i].y3_, cc.getY3());
		}
	}

}
