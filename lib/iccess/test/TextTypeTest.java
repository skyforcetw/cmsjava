
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class TextTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x74,(byte)0x65,(byte)0x78,(byte)0x74,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44	//asciiBytes_:byte[]
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
			TextType empty = new TextType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			TextType stt = new TextType("abc");
			assertEquals("abc", stt.getText());

			TextType sttn = new TextType("ABCDEFG", 20);
			String sttns = sttn.getText();
			//assertEquals(20, sttns.length());	//changed to return a trim()
			assertEquals("ABCDEFG", sttns);

			TextType inst1 = new TextType(ba);
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
		TextType data = new TextType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertEquals(TextType.SIGNATURE, data.getSignature().intValue());
			assertEquals(4, data.getLength());
			assertEquals("ABCD", data.getText());
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
			TextType ct = new TextType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			TextType ct = new TextType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testGetText()
	{
		//ignore
	}

	public void testSetText()
	{
		try
		{
			TextType ct = new TextType("abcd");
			ct.setText("ABCD");
			assertEquals(4, ct.getLength());
			assertEquals("ABCD", ct.getText());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testGetLength()
	{
		//ignore
	}


}
