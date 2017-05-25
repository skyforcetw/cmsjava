
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class CurveTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x63,(byte)0x75,(byte)0x72,(byte)0x76,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00	//numEntries_:UInt32Number, 0, identity response
	};
	private byte[] ba1 = new byte[]
	{
		(byte)0x63,(byte)0x75,(byte)0x72,(byte)0x76,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,	//numEntries_:UInt32Number
		(byte)0x01,(byte)0x00	//gammaValue_:U8Fixed8Number	1.0
	};
	private byte[] ba2 = new byte[]
	{
		(byte)0x63,(byte)0x75,(byte)0x72,(byte)0x76,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,	//numEntries_:UInt32Number
		(byte)0x80,(byte)0x00,	//curveValues_:UInt16Number[0]	32768
		(byte)0x80,(byte)0x00	//[1]
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
			CurveType empty = new CurveType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			CurveType inst1 = new CurveType(ba);
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
		CurveType data = new CurveType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertEquals(0, data.getEntryCount().intValue());
			data.fromByteArray(ba1, 0, 0);
			assertEquals(1, data.getEntryCount().intValue());
			assertEquals(1.0, data.getGammaValue().doubleValue());
			data.fromByteArray(ba2, 0, 0);
			assertEquals(2, data.getEntryCount().intValue());
			UInt16Number[] cvs = data.getCurveValues();
			assertEquals(2, cvs.length);
			assertEquals(32768, cvs[0].intValue());
			assertEquals(32768, cvs[1].intValue());
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
			CurveType ct = new CurveType(ba2);
			byte[] bac = ct.toByteArray();
			compareBytes(ba2, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			CurveType ct = new CurveType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testGetEntryCount()
	{
		try
		{
			CurveType ct = new CurveType(ba2);
			assertEquals(2, ct.getEntryCount().intValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}

	public void testGetGammaValue()
	{
		try
		{
			CurveType ct = new CurveType(ba1);
			assertEquals(1.0, ct.getGammaValue().doubleValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}
}

