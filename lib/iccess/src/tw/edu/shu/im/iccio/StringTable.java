package tw.edu.shu.im.iccio;

import java.util.ArrayList;

/**
 * StringTable keeps an array of strings and allow to check whether a given string is in the array.
 * This can be implemented using a hashtable but it uses simple ArrayList instead because there are
 * only a few strings.
 */
public final class StringTable
{
	private	ArrayList	strs_ = new ArrayList();

	/**
	 * Construct a StringTable.
	 */
	public StringTable()
	{
	}

	/**
	 * Add a string to the array.
	 * @param str - the string to add
	 */
	public void addString(String str)
	{
		this.strs_.add(str);
	}

	/**
	 * Test whether a given string is in the array.
	 * @param str - the string to test for existence
	 * @return true if the given string is found in the table, else false
	 */
	public boolean contains(String str)
	{
		for (int i=0; i<this.strs_.size(); i++)
		{
			String s = (String)this.strs_.get(i);
			if (s.equals(str))
			{
				return true;
			}
		}
		return false;
	}
}
