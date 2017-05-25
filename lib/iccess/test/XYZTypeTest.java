
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class XYZTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x58,(byte)0x59,(byte)0x5A,(byte)0x20,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//xyzValues_:XYZNumber[], 1.0
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00
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
			XYZType empty = new XYZType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			XYZType inst1 = new XYZType(ba);
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
		XYZType data = new XYZType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			//byte[] res = data.toByteArray();
			assertEquals(XYZType.SIGNATURE, data.getSignature().intValue());
			assertEquals(1, data.getXYZCount());
			XYZNumber[] xyzs = data.getXYZNumbers();
			XYZNumber xyz = xyzs[0];
			assertEquals(1.0,xyz.getCIEX().doubleValue());
			assertEquals(1.0,xyz.getCIEY().doubleValue());
			assertEquals(1.0,xyz.getCIEZ().doubleValue());
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
			data.fromByteArray(ba, ba.length, ba.length);
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


	public void testToByteArray()
	{
		try
		{
			XYZType ct = new XYZType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			XYZType ct = new XYZType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testSetters()
	{
		XYZType ct = new XYZType();
		try
		{
			XYZNumber xyz = new XYZNumber(0, 1.0, 2.0);
			ct.addXYZNumber(xyz);
			assertEquals(1, ct.getXYZCount());
			ct.addXYZNumber(xyz);
			assertEquals(2, ct.getXYZCount());
			XYZNumber[] rs = ct.getXYZNumbers();
			assertEquals(2.0, rs[1].getCIEZ().doubleValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}
}
