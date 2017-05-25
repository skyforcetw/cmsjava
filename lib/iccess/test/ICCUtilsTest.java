
import junit.framework.*;
import tw.edu.shu.im.iccio.*;
import tw.edu.shu.im.iccio.datatype.*;
import tw.edu.shu.im.iccio.tagtype.*;

public class ICCUtilsTest extends TestCase
{
  private class MyBytes implements Streamable
  {
    private byte[] b_;
    public MyBytes(int first, int len)
    {
      this.b_ = new byte[len];
      for (int i=0; i<len; i++)
        this.b_[i] = (byte)first++;
    }
    public void fromByteArray(byte[] ba, int offset, int len) throws ICCProfileException
    {
      //not used here
    }
    public byte[] toByteArray() throws ICCProfileException
    {
      return this.b_;
    }
    public String toXmlString(String name) {
	    return "";
    }
    public String toXmlString() {
	    return "";
    }
  }
  public void testAppendByteArray()
  {
    MyBytes b1 = new MyBytes(1, 5);
    MyBytes b2 = new MyBytes(11, 5);
    byte[] all = new byte[12];
    try
    {
      ICCUtils.appendByteArray(all, 0, b1);
      for (int i=0; i<5; i++)
        assertEquals((byte)(i+1), all[i]);
      ICCUtils.appendByteArray(all, 5, b2);
      for (int i=5; i<10; i++)
        assertEquals((byte)(i+11-5), all[i]);
    }
    catch (ICCProfileException e)
    {
      assertFalse(e.getMessage(),true);
    }
  }

  public void testExpand()
  {
    UInt8Number[] ba = new UInt8Number[]{new UInt8Number((byte)1),new UInt8Number((byte)2)};
    UInt8Number[] ba2 = ICCUtils.expand(ba, 1);
    assertEquals(ba.length+1, ba2.length);
    assertEquals(1, ba2[0].intValue());
    assertEquals(2, ba2[1].intValue());
    //TODO: test UInt16Number, UInt32Number, and UInt64Number, should work as well
  }
  
  public void testEnUnicode()
  {
	String s = "This is ASCII text.";
	byte[] sb = s.getBytes();
	try
	{
	byte[] data = ICCUtils.enUnicode(s);
	for (int i=0; i<s.length(); i++)
	{
		int k = i << 1;
		assertTrue(data[k] == 0);
		assertEquals(sb[i], data[k+1]);
	}
	} catch (ICCProfileException e)
	{
		System.err.println(e.getMessage());
	}
	String sc = "\u7F85\u6885\u541B";
	try
	{
		byte[] cdata = ICCUtils.enUnicode(sc);
		assertEquals(0x7f, cdata[0]);
		assertEquals(0x85, 0xff & cdata[1]);
		assertEquals(0x68, cdata[2]);
		assertEquals(0x85, 0xff & cdata[3]);
		assertEquals(0x54, cdata[4]);
		assertEquals(0x1B, cdata[5]);
	} catch (ICCProfileException e)
	{
		System.err.println(e.getMessage());
	}
	}

	public void testDeUnicode()
	{
		byte[] cdata = new byte[]{(byte)0x7f,(byte)0x85,(byte)0x68,(byte)0x85,(byte)0x54,(byte)0x1B};
		try
		{
			String s = ICCUtils.deUnicode(cdata);
			//System.out.println(s);
			assertEquals("\u7F85\u6885\u541B",s);
		}catch (ICCProfileException e)
		{
			System.err.println(e.getMessage());
		}
	}

}
