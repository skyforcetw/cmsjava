
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class SignatureTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x73,(byte)0x69,(byte)0x67,(byte)0x20,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44	//signature content
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
			SignatureType empty = new SignatureType();
			assertEquals(SignatureType.SIGNATURE, empty.getSignature().intValue());

			SignatureType inst1 = new SignatureType(ba);
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
		SignatureType data = new SignatureType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertEquals(SignatureType.SIGNATURE, data.getSignature().intValue());
			Signature s = data.getSignatureData();
			assertEquals("ABCD", s.getSignature());
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
			SignatureType ct = new SignatureType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			SignatureType ct = new SignatureType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testGetSignature()
	{
		SignatureType ct = new SignatureType(0x41424344);
		assertEquals(SignatureType.SIGNATURE, ct.getSignature().intValue());
	}

	public void testGetSignatureData()
	{
		try
		{
			SignatureType ct = new SignatureType(0x41424344);
			assertEquals("ABCD", ct.getSignatureData().getSignature());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
		}
	}

	public void testSetSignatureData()
	{
		try
		{
			SignatureType ct = new SignatureType(0x41424344);
			assertEquals("ABCD", ct.getSignatureData().getSignature());
			ct.setSignatureData("xyz ");
			//System.out.println(ct.getSignatureData().getSignature());
			assertEquals("xyz", ct.getSignatureData().getSignature());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(),true);
		}
	}


}
