
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ICCFileWriterTest extends TestCase
{
	private ICCFileWriter	data_;
  private String  testfile_ = "testwriter.icc";

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
		try
		{
			ICCFileWriter inst1 = new ICCFileWriter(testfile_);
			assertTrue("File create error",inst1.getFile()!=null);
			inst1.close();
			//removeFile(testfile_);
		}
		catch (ICCProfileException e)
		{
			assertFalse("Error occurred:"+e.getMessage(), true);
		}
		try
		{
			ICCFileWriter inst1 = new ICCFileWriter(testfile_);
		}
		catch (ICCProfileException e)
		{
			assertTrue("got it, can't overwrite",true);
			removeFile(testfile_);
		}
	}

	private ICCFileWriter createFile(String filename)
	{
		try
		{
			ICCFileWriter writer = new ICCFileWriter(filename);
			return writer;
		}
		catch (ICCProfileException e)
		{
			assertFalse("file can't create",true);
			return null;
		}
	}

	private void removeFile(String filename)
	{
		java.io.File f = new java.io.File(filename);
		f.delete();
	}

	public void testGetFile()
	{
		ICCFileWriter writer = createFile(testfile_);
		java.io.RandomAccessFile raf = writer.getFile();
		try
		{
			raf.write(100);
			writer.close();
			removeFile(testfile_);
		}
		catch (java.io.IOException e)
		{
			assertFalse("should not cause exception",true);
		}
	}

	public void testSeek()
	{
		ICCFileWriter writer = createFile(testfile_);
		try
		{
			byte[] b = new byte[]{(byte)1,(byte)2,(byte)5,(byte)6};
			writer.write(b);
			writer.seek(2);
			writer.write((short)0x0304);
			writer.close();
			byte[] bb = new byte[]{(byte)1,(byte)2,(byte)3,(byte)4};
			compareFile(testfile_,bb);
			removeFile(testfile_);
		}
		catch (ICCProfileException e)
		{
			assertFalse("should not cause ex",true);
		}
	}

	private void compareFile(String filename, byte[] bytes)
	{
		try
		{
			java.io.FileInputStream fis = new java.io.FileInputStream(filename);
			for (int i=0; i<bytes.length; i++)
			{
				byte b = (byte)fis.read();
				assertEquals(bytes[i], b);
			}
			fis.close();
		}
		catch (java.io.IOException e)
		{
			assertFalse("should not get here",true);
		}
	}

	public void testWrite()
	{
		ICCFileWriter writer = createFile(testfile_);
		try
		{
			writer.write((byte)1);
			writer.write((short)2);
			writer.write(3);
			writer.write((long)4);
			byte[] ba = new byte[]{(byte)255,(byte)254,(byte)253};
			writer.write(ba);
			writer.close();
			byte[] bytes = new byte[]
			{
				(byte)1,(byte)0,(byte)2,(byte)0,(byte)0,(byte)0,(byte)3,
				(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)0,(byte)4,
				(byte)255,(byte)254,(byte)253
			};
			compareFile(testfile_,bytes);
			removeFile(testfile_);
		}
		catch (ICCProfileException e)
		{
			assertFalse("should not cause ex",true);
		}
	}

	public void testWriteWithPadding()
	{
		ICCFileWriter writer = createFile(testfile_);
		try
		{
			byte[] ba = new byte[]{(byte)255,(byte)254,(byte)253};
			writer.writeWithPadding(ba, 4);
			byte[] bb = new byte[]{(byte)255,(byte)254,(byte)253,(byte)0};
			compareFile(testfile_,bb);
		}
		catch (ICCProfileException e)
		{
			assertFalse("should not cause ex",true);
		}
		finally
		{
      writer.close();
      removeFile(testfile_);
    }
	}

	public void testClose()
	{
		//ignore
	}

}
