
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class DateTimeTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x64,(byte)0x74,(byte)0x69,(byte)0x6D,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x07,(byte)0xC6,(byte)0x00,(byte)0x0C,(byte)0x00,(byte)0x1F,
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x05,(byte)0x00,(byte)0x20	//dateTime_:DateTimeNumber
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
			DateTimeType empty = new DateTimeType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			DateTimeType inst1 = new DateTimeType(ba);
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
		DateTimeType data = new DateTimeType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertEquals(DateTimeType.SIGNATURE, data.getSignature().intValue());
			DateTimeNumber d = data.getDateTime();
			DateTimeNumber d0 = new DateTimeNumber(ba, 8);
			assertEquals(d0.toString(),d.toString());
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
			DateTimeType ct = new DateTimeType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			DateTimeType ct = new DateTimeType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testGetDateTime()
	{
		//ignore
	}

	public void testSetDateTime()
	{
		DateTimeType ct = new DateTimeType();
		DateTimeNumber dn = new DateTimeNumber(2006,1,2,10,20,30);
		ct.setDateTime(dn);
		assertEquals(dn.toString(), ct.getDateTime().toString());
		java.util.Calendar dc = java.util.Calendar.getInstance();
		ct.setDateTime(dc);
		DateTimeNumber dn2 = new DateTimeNumber(dc);
		assertEquals(dn2.toString(), ct.getDateTime().toString());
	}


}
