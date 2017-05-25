package tw.edu.shu.im.iccio.datatype;

import tw.edu.shu.im.iccio.Streamable;
import tw.edu.shu.im.iccio.ICCProfileException;

import java.util.Calendar;

/**
 * ICC Profile data type for date and time.
 *
 * It is a fixed 12-byte struct containing the year, month, day, hour, minute
 * and second numbers each occupy two bytes ie, a short integer.
 */
public class DateTimeNumber implements Streamable
{
    public static final int SIZE = 12;

    private short year_;    //1990..
    private short month_;   //1..12
    private short day_;     //1..31
    private short hours_;   //0..23
    private short minutes_; //0..59
    private short seconds_; //0..59

	public DateTimeNumber()
	{
		//default constructor use current system date time
		Calendar rightNow = Calendar.getInstance();
		setDateTime(rightNow);
	}

	public DateTimeNumber(Calendar c)
	{
		setDateTime(c);
	}

    public DateTimeNumber(byte[] dtAsByteArray) throws ICCProfileException
    {
        fromByteArray(dtAsByteArray, 0, SIZE);
    }

	public DateTimeNumber(byte[] dtAsByteArray, int offset) throws ICCProfileException
	{
		fromByteArray(dtAsByteArray, offset, SIZE);
	}

	public DateTimeNumber(DateTimeNumber copy)
	{
		this.year_ = copy.year_;
		this.month_ = copy.month_;
		this.day_ = copy.day_;
		this.hours_ = copy.hours_;
		this.minutes_ = copy.minutes_;
		this.seconds_ = copy.seconds_;
	}

	public DateTimeNumber(short year, short month, short day, short hour, short minute, short second)
	{
		this.year_ = year;
		this.month_ = month;
		this.day_ = day;
		this.hours_ = hour;
		this.minutes_ = minute;
		this.seconds_ = second;
	}

	public DateTimeNumber(int year, int month, int day, int hour, int minute, int second)
	{
		this.year_ = (short)year;
		this.month_ = (short)month;
		this.day_ = (short)day;
		this.hours_ = (short)hour;
		this.minutes_ = (short)minute;
		this.seconds_ = (short)second;
	}

    public void fromByteArray(byte[] dtAsByteArray, int offset, int len) throws ICCProfileException
    {
		if (dtAsByteArray == null)
			throw new ICCProfileException("byte array null", ICCProfileException.NullPointerException);

		if (offset < 0)
			throw new ICCProfileException("offset < 0", ICCProfileException.IndexOutOfBoundsException);

		if (len != SIZE)
			throw new ICCProfileException("len parameter is not equal to SIZE", ICCProfileException.WrongSizeException);

		if (dtAsByteArray.length < offset+len)
			throw new ICCProfileException("offset outside byte array", ICCProfileException.IndexOutOfBoundsException);

		//year,month,day,hour,minutes,seconds
		int i = offset;
		this.year_ = (short)((dtAsByteArray[i++] & 0xff) << 8 | dtAsByteArray[i++] & 0xff);
		this.month_ = (short)((dtAsByteArray[i++] & 0xff) << 8 | dtAsByteArray[i++] & 0xff);
		this.day_ = (short)((dtAsByteArray[i++] & 0xff) << 8 | dtAsByteArray[i++] & 0xff);
		this.hours_ = (short)((dtAsByteArray[i++] & 0xff) << 8 | dtAsByteArray[i++] & 0xff);
		this.minutes_ = (short)((dtAsByteArray[i++] & 0xff) << 8 | dtAsByteArray[i++] & 0xff);
		this.seconds_ = (short)((dtAsByteArray[i++] & 0xff) << 8 | dtAsByteArray[i++] & 0xff);
    }

	public byte[] toByteArray() throws ICCProfileException
	{
		byte[] d = new byte[SIZE];
		d[0] = (byte)(this.year_ >> 8);
		d[1] = (byte)this.year_;
		d[2] = (byte)(this.month_ >> 8);
		d[3] = (byte)this.month_;
		d[4] = (byte)(this.day_ >> 8);
		d[5] = (byte)this.day_;
		d[6] = (byte)(this.hours_ >> 8);
		d[7] = (byte)this.hours_;
		d[8] = (byte)(this.minutes_ >> 8);
		d[9] = (byte)this.minutes_;
		d[10] = (byte)(this.seconds_ >> 8);
		d[11] = (byte)this.seconds_;
		return d;
	}

    public short getYear()
    {
        return this.year_;
    }

    public short getMonth()
    {
        return this.month_;
    }

    public short getDay()
    {
        return this.day_;
    }

    public short getHour()
    {
        return this.hours_;
    }

    public short getMinute()
    {
        return this.minutes_;
    }

    public short getSecond()
    {
        return this.seconds_;
    }

    public int size()
    {
        return this.SIZE;
    }

	public void setDateTime(Calendar c)
	{
		this.year_ = (short)c.get(Calendar.YEAR);
		this.month_ = (short)(c.get(Calendar.MONTH)+1);
		this.day_ = (short)c.get(Calendar.DATE);
		this.hours_ = (short)c.get(Calendar.HOUR_OF_DAY);
		this.minutes_ = (short)c.get(Calendar.MINUTE);
		this.seconds_ = (short)c.get(Calendar.SECOND);
	}

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(this.year_);sb.append('-');
        sb.append(this.month_);sb.append('-');
        sb.append(this.day_);sb.append(' ');
        sb.append(this.hours_);sb.append(':');
        sb.append(this.minutes_);sb.append(':');
        sb.append(this.seconds_);
        return sb.toString();
    }


	/**
	 * Return XML element of this object.
	 * @param name - attribute name on element
	 * @return XML fragment as a string
	 */
	public String toXmlString(String name)
	{
		StringBuffer sb = new StringBuffer();
		if (name==null || name.length()<1)
			sb.append("<dateTimeNumber>");
		else
			sb.append("<dateTimeNumber name=\""+name+"\">");
		sb.append("<year value=\"");sb.append(year_);sb.append("\"/>");
		sb.append("<month value=\"");sb.append(month_);sb.append("\"/>");
		sb.append("<day value=\"");sb.append(day_);sb.append("\"/>");
		sb.append("<hour value=\"");sb.append(hours_);sb.append("\"/>");
		sb.append("<minute value=\"");sb.append(minutes_);sb.append("\"/>");
		sb.append("<second value=\"");sb.append(seconds_);sb.append("\"/>");
		sb.append("</dateTimeNumber>");
		return sb.toString();
	}

	public String toXmlString()
	{
		return toXmlString(null);
	}

}
