
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class DateTimeNumberTest extends TestCase
{
	private byte[] ar_ = new byte[]
	{
		(byte)0x07, (byte)0xC6,  (byte)0x00, (byte)0x0C,  (byte)0x00, (byte)0x1F, 
		(byte)0x00, (byte)0x01, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x20
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
		DateTimeNumber dnow = new DateTimeNumber();
		java.util.Calendar rightNow = java.util.Calendar.getInstance();
		assertEquals(rightNow.get(java.util.Calendar.YEAR),dnow.getYear());
		assertEquals(rightNow.get(java.util.Calendar.MONTH),dnow.getMonth());
		assertEquals(rightNow.get(java.util.Calendar.DATE),dnow.getDay());
		assertEquals(rightNow.get(java.util.Calendar.HOUR_OF_DAY),dnow.getHour());
		assertEquals(rightNow.get(java.util.Calendar.MINUTE),dnow.getMinute());
		assertEquals(rightNow.get(java.util.Calendar.SECOND),dnow.getSecond());

		try 
		{
			DateTimeNumber d = new DateTimeNumber(ar_);
			assertEquals(12, d.size());
			assertEquals("Year", 1990, (int)d.getYear());
			assertEquals("Month", 12, d.getMonth());
			assertEquals("Day", 31, d.getDay());
			assertEquals("Hours", 1, d.getHour());
			assertEquals("Minute", 5, d.getMinute());
			assertEquals("Second", 32, d.getSecond());
		} 
		catch (ICCProfileException e) 
		{
			assertFalse("Exception occurred: "+e.getMessage(), true);
		}

		DateTimeNumber d = new DateTimeNumber(2006, 5, 24, 18, 8, 15);
		assertEquals("Year", 2006, (int)d.getYear());
		assertEquals("Month", 5, d.getMonth());
		assertEquals("Day", 24, d.getDay());
		assertEquals("Hours", 18, d.getHour());
		assertEquals("Minute", 8, d.getMinute());
		assertEquals("Second", 15, d.getSecond());
	}

	public void testFromByteArray()
	{
		byte[] ba = new byte[]
		{
			(byte)0x07, (byte)0xC7,  (byte)0x00, (byte)0x0B,  (byte)0x00, (byte)0x1E, 
			(byte)0x00, (byte)0x0A, (byte)0x00, (byte)0x05, (byte)0x00, (byte)0x21
		};
		try 
		{
			DateTimeNumber d = new DateTimeNumber();
			d.fromByteArray(ba, 0, ba.length);
			assertEquals("Year", 1991, d.getYear());
			assertEquals("Month", 11, d.getMonth());
			assertEquals("Day", 30, d.getDay());
			assertEquals("Hours", 10, d.getHour());
			assertEquals("Minute", 5, d.getMinute());
			assertEquals("Second", 33, d.getSecond());
		} 
		catch (ICCProfileException e) 
		{
			assertFalse("Exception occurred: "+e.getMessage(), true);
		}
		byte[] ar2 = new byte[] 
			{
				(byte)1,(byte)2,(byte)3,
				(byte)0x07, (byte)0xCF, (byte)0x00, (byte)0x0B, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x09, 
				(byte)0x00, (byte)0x17, (byte)0x00, (byte)0x07,
				(byte)0,(byte)0};
		try 
		{
			DateTimeNumber dt = new DateTimeNumber();
			dt.fromByteArray(ar2,3,DateTimeNumber.SIZE);
			assertEquals(1999,dt.getYear());
			assertEquals(11,dt.getMonth());
			assertEquals(2,dt.getDay());
			assertEquals(9,dt.getHour());
			assertEquals(23,dt.getMinute());
			assertEquals(7,dt.getSecond());
		} 
		catch (ICCProfileException e) 
		{
			System.err.println(e.getMessage());
		}

		DateTimeNumber data = new DateTimeNumber();
		try
		{
			data.fromByteArray(ba, -1, ba.length);
			assertFalse("index out of bounds, should raise exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(ba, ba.length, ba.length);
			assertFalse("index out of bounds, should raise exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.IndexOutOfBoundsException, e.getType());
		}
		try
		{
			data.fromByteArray(ba, 0, 3);
			assertFalse("should raise wrong size exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.WrongSizeException, e.getType());
		}
		try
		{
			data.fromByteArray(null, 0, 0);
			assertFalse("should raise null pointer exception",true);
		}
		catch (ICCProfileException e)
		{
			assertEquals(ICCProfileException.NullPointerException, e.getType());
		}
	}

	public void testToByteArray()
	{
		byte[] data = new byte[]
		{
			(byte)0x07,(byte)0xD6,(byte)0,(byte)5,(byte)0,(byte)0x18,
			(byte)0,(byte)0x12, (byte)0,(byte)8, (byte)0,(byte)0xf};
		DateTimeNumber d = new DateTimeNumber(2006, 5, 24, 18, 8, 15);
		try 
		{
			byte[] ar = d.toByteArray();
			assertEquals("length of returned datetime",DateTimeNumber.SIZE,ar.length);
			for (int i=0; i<ar.length; i++)
				assertEquals(data[i], ar[i]);
		} 
		catch (ICCProfileException e) 
		{
			assertFalse("Exception: "+e.getMessage(), true);
		}
	}
	
	public void testToString()
	{
		DateTimeNumber d = new DateTimeNumber(2006, 5, 24, 18, 8, 15);
		String s = d.toString();
		assertEquals("2006-5-24 18:8:15", s);
	}

	public void testGetYear()
	{
	}

	public void testGetMonth()
	{
	}

	public void testGetDay()
	{
	}

	public void testGetHour()
	{
	}

	public void testGetMinute()
	{
	}

	public void testGetSecond()
	{
	}

	public void testSize()
	{
		DateTimeNumber data = new DateTimeNumber();
		assertEquals(DateTimeNumber.SIZE, data.size());
	}

	public void testSetDateTime()
	{
	}

}
