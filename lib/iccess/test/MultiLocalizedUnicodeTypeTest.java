
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class MultiLocalizedUnicodeTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x6D,(byte)0x6C,(byte)0x75,(byte)0x63,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,	//numNames_:UInt32Number
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,	//nameRecSize_:UInt32Number ?
		(byte)0x00,(byte)0x10,	//nameLangCode_:UInt16Number[]
		(byte)0x00,(byte)0x20,	//nameCountryCode_:UInt16Number[]
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x05,	//nameLength_:UInt32Number[]
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x1C,	//nameOffset_:UInt32Number[]
		(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44,(byte)0x45	//unicodes_:byte[]
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
			MultiLocalizedUnicodeType empty = new MultiLocalizedUnicodeType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			MultiLocalizedUnicodeType inst1 = new MultiLocalizedUnicodeType(ba);
			byte[] bac = inst1.toByteArray();

		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testFromByteArray()
	{
		MultiLocalizedUnicodeType data = new MultiLocalizedUnicodeType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertEquals(MultiLocalizedUnicodeType.SIGNATURE, data.getSignature().intValue());
			assertEquals(1, data.getNumNames().intValue());
			assertEquals(0x0C, data.getNameRecSize().intValue());
			assertEquals(0x10, data.getNameLangCode()[0].intValue());
			assertEquals(0x20, data.getNameCountryCode()[0].intValue());
			assertEquals(5, data.getNameLength()[0].intValue());
			assertEquals(0x1C, data.getNameOffset()[0].intValue());
			byte[] b = data.getUnicodes();
			compareBytes(ba, 28, b.length, b);
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
			MultiLocalizedUnicodeType ct = new MultiLocalizedUnicodeType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

  public void testSize()
  {
		try
		{
			MultiLocalizedUnicodeType ct = new MultiLocalizedUnicodeType(ba);
			assertEquals(ba.length, ct.size());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		MultiLocalizedUnicodeType ct = new MultiLocalizedUnicodeType();
		assertEquals(16, ct.size());
  }

}
