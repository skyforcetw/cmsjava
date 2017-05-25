
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ParametricCurveTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x70,(byte)0x61,(byte)0x72,(byte)0x61,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x00,	//funcTypeCode_:UInt16Number 0..4 allowed
		(byte)0,(byte)0,  //reserved
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//params_:S15Fixed16Number[]
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,
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
			ParametricCurveType empty = new ParametricCurveType();
			assertEquals(ParametricCurveType.SIGNATURE, empty.getSignature().intValue());

			ParametricCurveType inst1 = new ParametricCurveType(ba);
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
		ParametricCurveType data = new ParametricCurveType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertEquals(ParametricCurveType.SIGNATURE, data.getSignature().intValue());
			assertEquals(0, data.getFunctionTypeCode().intValue());
			S15Fixed16Number[] ar = data.getParameters();
			assertEquals(1.0, ar[0].doubleValue());
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
			ParametricCurveType ct = new ParametricCurveType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			ParametricCurveType ct = new ParametricCurveType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testSetFunctionTypeCode()
	{
		ParametricCurveType ct = new ParametricCurveType();
		ct.setFunctionTypeCode(new UInt16Number(1));
		assertEquals(1, ct.getFunctionTypeCode().intValue());
	}

	public void testAddParameter()
	{
		ParametricCurveType ct = new ParametricCurveType();
		try
		{
			S15Fixed16Number num = new S15Fixed16Number(1.5);
			ct.addParameter(num);
			S15Fixed16Number[] parms = ct.getParameters();
			assertEquals(1.5, parms[0].doubleValue());
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
	}
}
