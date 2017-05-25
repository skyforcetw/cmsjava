package tw.edu.shu.im.iccio;

/**
 * Saveable provides an interface for all data object that needs to be saved in a disk file.
 * 
 * Interface methods:
 * save(ICCFileOutput) - save the element into ICCFileOutput object.
 */
public interface Saveable
{
	/**
	 * Save the data element into the specified ICCFileOutput object.
	 * @param out - a class instance that implemented ICCFileOutput interface.
	 */
	public void save(ICCFileOutput out) throws ICCProfileException;
}
