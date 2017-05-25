package tw.edu.shu.im.iccio;

import tw.edu.shu.im.iccio.datatype.Signature;
import tw.edu.shu.im.iccio.datatype.UInt32Number;
import tw.edu.shu.im.iccio.tagtype.AbstractTagType;

/**
 * ICCProfileTagEntry contains one tag entry of an ICC profile tag table.
 *
 * @author Ted Wen
 * @version 0.1
 * @update 2006-10-29
 *
 * The ICC profile tag table contains a number of tag entries each of which defines
 * a record of tagged data element.
 *
 * A TAG is a struct composed of three elements:
 * <ul>
 *  <li>signature is a 4-byte number, each byte represents a ASCII character, for example, 'APPL'
 *  <li>offset is a 4-byte number representing a pointer to the absolute address of the tagged data block in file
 *  <li>tagsize is a 4-byte number giving the number of bytes of the data block
 * </ul>
 * In addition to the three fields that are stored in the profile, this class keeps a link to
 * the actual tagged data element, which is a byte array that this entry refers to
 * (loaded separately after the tag table is loaded)
 */
public class ICCProfileTagEntry
    implements Streamable {
  public static final int SIZE = 12;

  private Signature signature_; //4-byte chars like 'APPL'
  private UInt32Number offset_; //4-byte number pointing to the address of the tagged data block beginning from top of file
  private UInt32Number tagsize_; //number of bytes of the block pointed to by offset
  private AbstractTagType dataBlock_; //contains byte array for this element data chunk

  /**
   * Construct an empty ICCProfileTagEntry object.
   */
  public ICCProfileTagEntry() {
  }

  /**
   * Construct an ICCProfileTagEntry with the values of the three elements.
   * @param signature - int value
   * @param offset - offset pointing to the address of the tagged data block
   * @param size - number of bytes of the data block
   */
  public ICCProfileTagEntry(int signature, int offset, int size) throws
      ICCProfileException {
    this.signature_ = new Signature(signature);
    this.offset_ = new UInt32Number(offset);
    this.tagsize_ = new UInt32Number(size);
  }

  /**
   * Construct an ICCProfileTagEntry with a given tag signature and a tag type data element.
   * This is useful for creating a tag entry and data on scratch for generating a profile.
   * @param signature - signature value for the tag entry.
   * @param data - object of a tag type derived from AbstractTagType.
   */
  public ICCProfileTagEntry(int signature, AbstractTagType data) throws
      ICCProfileException {
    this.signature_ = new Signature(signature);
    this.offset_ = new UInt32Number(0);
    if (data == null) {
      throw new ICCProfileException("Tag data null",
                                    ICCProfileException.NullPointerException);
    }
    this.dataBlock_ = data;
    this.tagsize_ = new UInt32Number(data.size());
  }

  /**
   * Construct an ICCProfileTagEntry with data from a byte array starting at a position.
   * @param byteArray - byte array containing the data bytes of a tag
   * @param offset - starting position of the tag record in the byte array
   * @exception - ICCProfileException
   */
  public ICCProfileTagEntry(byte[] byteArray, int offset) throws
      ICCProfileException {
    fromByteArray(byteArray, offset, SIZE);
  }

  /**
   * Implemented interface method to parse a byte array into the tag record.
   * @param byteArray - byte array containing the data bytes of a tag
   * @param offset - starting position of the tag record in the byte array
   * @param len - number of bytes, not used here
   * @exception - ICCProfileException
   */
  public void fromByteArray(byte[] byteArray, int offset, int len) throws
      ICCProfileException {
    if (byteArray == null) {
      throw new ICCProfileException(
          "ICCProfileTagEntry.fromByteArray():byte array null",
          ICCProfileException.NullPointerException);
    }

    if (offset < 0 || offset >= byteArray.length) {
      throw new ICCProfileException(
          "ICCProfileTagEntry.fromByteArray():offset < 0",
          ICCProfileException.IndexOutOfBoundsException);
    }

    if (len != SIZE) {
      throw new ICCProfileException(
          "ICCProfileTagEntry.fromByteArray():len parameter is not equal to SIZE",
          ICCProfileException.WrongSizeException);
    }

    if (byteArray.length < offset + len) {
      throw new ICCProfileException(
          "ICCProfileTagEntry.fromByteArray():offset outside byte array",
          ICCProfileException.IndexOutOfBoundsException);
    }

    this.signature_ = new Signature(byteArray, offset);
    this.offset_ = new UInt32Number(byteArray, offset + 4);
    this.tagsize_ = new UInt32Number(byteArray, offset + 8);
  }

  public byte[] toByteArray() throws ICCProfileException {
    if (this.signature_ == null || this.offset_ == null || this.tagsize_ == null) {
      throw new ICCProfileException(
          "ICCProfileTagEntry.toByteArray():data not set",
          ICCProfileException.InvalidDataValueException);
    }

    byte[] all = new byte[signature_.size() + offset_.size() + tagsize_.size()];
    if (all.length != SIZE) {
      throw new ICCProfileException(
          "ICCProfileTagEntry.toByteArray():size error",
          ICCProfileException.WrongSizeException);
    }

    ICCUtils.appendByteArray(all, 0, this.signature_);
    ICCUtils.appendByteArray(all, 4, this.offset_);
    ICCUtils.appendByteArray(all, 8, this.tagsize_);

    return all;
  }

  /**
   * Getter for signature element value.
   * @return int value of signature
   */
  public Signature getSignature() {
    return this.signature_;
  }

  /**
   * Getter for offset element value.
   * @return int value of offset. -1 if data not set.
   */
  public int getOffset() {
    return (this.offset_ == null) ? -1 : this.offset_.intValue();
  }

  /**
   * Setter for offset (address).
   * @param offset - address of this data element relative to the beginning of the file.
   */
  public void setOffset(int offset) {
    this.offset_ = new UInt32Number(offset);
  }

  /**
   * Getter to return the size of the tag data block.
   * @return int size of tag data block,-1 if data not set.
   */
  public int getSize() {
    return (this.tagsize_ == null) ? -1 : this.tagsize_.intValue();
  }

  public void setSize(int size) {
    this.tagsize_ = new UInt32Number(size);
  }

  /**
   * Get the aligned data size in bytes, ie., actual number of bytes of a tagged dataset stored in the file.
   * The aligned data block is always on a 4-byte boundary.
   */
  public int getAlignedSize() {
    if (this.tagsize_ == null) {
      return -1;
    }
    int size = this.tagsize_.intValue();
    int n = size % 4;
    return (n > 0) ? size + 4 - n : size;
  }

  /**
   * Getter to reutrn the element data chunk for this entry
   */
  public AbstractTagType getData() {
    return this.dataBlock_;
  }

  public void setData(AbstractTagType data) {
    this.dataBlock_ = data;
  }

  /**
   * Align data size on 4-byte boundary and return adjusted size.
   * @param size - not aligned size, for example, 42
   * @return aligned size on 4-byte boundary, eg. 44 for 42
   */
  public static int alignDataSize(int size) {
    int n = size % 4;
    return (n > 0) ? size + 4 - n : size;
  }

  /**
   * Return the size of the class data elements, not the size of the tag data block.
   * @return size of structure
   */
  public int size() {
    return this.SIZE;
  }

  /**
   * Return a string of the element values, separated by commas.
   * @return string of tag element values.
   */
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append(this.signature_);
    sb.append(',');
    sb.append(this.offset_);
    sb.append(',');
    sb.append(this.tagsize_);
    return sb.toString();
  }

  /**
   * Convert the Tag entry into an XML fragment.
   * @param name - name attribute of the tag entry element
   * @return XML document of this profile as a String.
   */
  public String toXmlString(String name) {
    StringBuffer sb = new StringBuffer();
    sb.append("<entry");
    if (name != null && name.length() > 0) {
      sb.append(" name=\"" + name + "\">");
    }
    else {
      sb.append(">");
    }
    try {
      sb.append("<tag hex=\"" + Integer.toHexString(signature_.intValue()) +
                "\">" + signature_.getSignature() + "</tag>");
    }
    catch (ICCProfileException e) {
      sb.append("<tag hex=\"" + Integer.toHexString(signature_.intValue()) +
                "\"/>");
    }
    sb.append("<offset>" + offset_.toString() + "</offset>");
    sb.append("<size>" + tagsize_.toString() + "</size>");
    sb.append("</entry>");
    return sb.toString();
  }

  public String toXmlString() {
    return toXmlString(null);
  }
}
