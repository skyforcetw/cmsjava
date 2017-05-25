
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class S15Fixed16ArrayTypeTest extends TestCase
{
	private byte[] ba = new byte[]
	{
		(byte)0x73,(byte)0x66,(byte)0x33,(byte)0x32,	//signature
		(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
		(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00	//array_:S15Fixed16Number[]
	};
	private byte[] ba2 = new byte[]
		{
			(byte)0x73,(byte)0x66,(byte)0x33,(byte)0x32,	//signature
			(byte)0,(byte)0,(byte)0,(byte)0,	//reserved
			(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,	//array_:S15Fixed16Number[]
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
			S15Fixed16ArrayType empty = new S15Fixed16ArrayType();
			assertEquals(empty.SIGNATURE, empty.getSignature().intValue());

			S15Fixed16ArrayType inst1 = new S15Fixed16ArrayType(ba);
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
		S15Fixed16ArrayType data = new S15Fixed16ArrayType();
		try
		{
			data.fromByteArray(ba, 0, 0);
			assertEquals(S15Fixed16ArrayType.SIGNATURE, data.getSignature().intValue());
			assertEquals(1, data.getArraySize());
			S15Fixed16Number[] ar = data.getArray();
			assertEquals(1.0, ar[0].doubleValue());

			data.fromByteArray(ba2, 0, 0);
			assertEquals(2, data.getArraySize());
			S15Fixed16Number[] ar2 = data.getArray();
			assertEquals(1.0, ar2[0].doubleValue());
			assertEquals(1.0, ar2[1].doubleValue());
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
			S15Fixed16ArrayType ct = new S15Fixed16ArrayType(ba);
			byte[] bac = ct.toByteArray();
			compareBytes(ba, bac);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		try
		{
			S15Fixed16ArrayType ct = new S15Fixed16ArrayType();
			byte[] bac = ct.toByteArray();
			assertFalse("should raise ICCProfileException.InvalidDataValueException",true);
		}
		catch (ICCProfileException e)
		{
			assertTrue(e.getMessage(),ICCProfileException.InvalidDataValueException==e.getType());
		}
	}

	public void testAddNumber()
	{
		S15Fixed16ArrayType ct = new S15Fixed16ArrayType ();
		S15Fixed16Number[] ns = new S15Fixed16Number[3];
		try
		{
			for (int i=0; i<ns.length; i++)
				ns[i] = new S15Fixed16Number((i+1) * 1.0);
		}
		catch (ICCProfileException e)
		{
			assertFalse(e.getMessage(), true);
		}
		for (int i=0; i<ns.length; i++)
		{
			ct.addNumber(ns[i]);
			assertEquals(i+1, ct.getArraySize());
			S15Fixed16Number[] rs = ct.getArray();
			assertEquals(ns[i].doubleValue(), rs[rs.length-1].doubleValue(), 0.001);
		}
	}

}
