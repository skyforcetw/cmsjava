
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class DataTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x64,(byte)0x61,(byte)0x74,(byte)0x61,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,	//dataFlag_:UInt32Number, 0=ascii
		(byte)0x41,(byte)0x42,(byte)0x43,(byte)0x44	//asciiString_:TextType
	};
	private byte[] ba1 = new byte[]
	{
		(byte)0x64,(byte)0x61,(byte)0x74,(byte)0x61,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,	//dataFlag_:UInt32Number, 1=binary
		(byte)0x1,(byte)0x2,(byte)0x3,(byte)0x4,	//binaryData_:byte[]
		(byte)0x00	//
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
			DataType empty = new DataType();
			assertEquals(DataType.SIGNATURE, empty.getSignature().intValue());

			DataType inst1 = new DataType(ba);
			assertEquals(DataType.SIGNATURE, inst1.getSignature().intValue());
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
		DataType data = new DataType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertTrue(data.isAsciiString());
			assertTrue(data.getData()==null);
			assertEquals("ABCD", data.getText());
			
			data.fromByteArray(ba1, 0, 0);
			assertFalse(data.isAsciiString());
			assertTrue(data.getText()==null);
			byte[] bac = data.getData();
			compareBytes(ba1,12,bac,0,bac.length);
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
			DataType ct = new DataType(ba1);
			byte[] bac = ct.toByteArray();
			compareBytes(ba1, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			DataType ct = new DataType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testSetAsciiData()
	{
		try
		{
			DataType ct = new DataType();
			ct.setAsciiData("my data");
			assertTrue(ct.isAsciiString());
			assertEquals("my data", ct.getText());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testIsAsciiString()
	{
		//ignore
	}

	public void testGetText()
	{
		//ignore
	}

}
