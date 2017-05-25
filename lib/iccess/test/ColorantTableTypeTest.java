
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ColorantTableTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x63,(byte)0x6c,(byte)0x72,(byte)0x74,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,	//numColorants_:UInt32Number
		(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,	//colorantNames_:TextType[] should be 32 bytes padded with zeros
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0,(byte)0,(byte)0,(byte)0,
		(byte)0x80,(byte)0x00,	//pcsValues_:UInt16Number[1][3], all 1.0
		(byte)0x80,(byte)0x00,
		(byte)0x80,(byte)0x00
	};


	private void compareBytes(byte[] expected, byte[] result)
	{
		assertEquals("two byte array not same size",expected.length, result.length);
		for (int i=0; i<result.length; i++)
			assertEquals("byte "+i, expected[i], result[i]);
	}

	private void compareBytes(byte[] expected, int offset1, byte[] result, int offset2, int len)
	{
		for (int i=0; i<len; i++)
			assertEquals("byte "+i, expected[i+offset1], result[i+offset2]);
	}

	public void testConstructors()
	{
		try
		{
			ColorantTableType empty = new ColorantTableType();
			assertEquals(ColorantTableType.SIGNATURE, empty.getSignature().intValue());

			ColorantTableType inst1 = new ColorantTableType(ba);
			//byte[] bac = inst1.toByteArray();
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testFromByteArray()
	{
		ColorantTableType data = new ColorantTableType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			//byte[] res = data.toByteArray();
			assertEquals(ColorantTableType.SIGNATURE, data.getSignature().intValue());
			assertEquals(1, data.getColorantCount().intValue());
			TextType[] names = data.getColorantNames();
			String ttbs = names[0].getText();
			assertEquals("Colorant Name", "ABCD", ttbs.trim());
			UInt16Number[][] pcs = data.getPCSValues();
			assertEquals(0x8000, pcs[0][0].intValue());
			assertEquals(0x8000, pcs[0][1].intValue());
			assertEquals(0x8000, pcs[0][2].intValue());
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
			ColorantTableType ct = new ColorantTableType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			ColorantTableType ct = new ColorantTableType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testGetColorantCount()
	{
		//ignore
	}

	public void testGetColorantName()
	{
		try
		{
			ColorantTableType ctt = new ColorantTableType(ba);
			String name = ctt.getColorantName(0);
			assertEquals("colorant name is different:", "ABCD", name.trim());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testSetColorant()
	{
		try
		{
			ColorantTableType ctt = new ColorantTableType(ba);
			ctt.setColorant(0, "abcd", 1, 2, 3);
			assertEquals("abcd", ctt.getColorantName(0).trim());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testAddColorant()
	{
		try
		{
			ColorantTableType ctt = new ColorantTableType(ba);
			ctt.addColorant("xyz",4,5,6);
			assertEquals(2, ctt.getColorantCount().intValue());
			assertEquals("xyz", ctt.getColorantName(1).trim());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}


}
