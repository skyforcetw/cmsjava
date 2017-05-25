package shu.cms.lcd.calibrate.measured.util;

import java.util.*;

import shu.cms.colorspace.depend.*;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: a Colour Management System by Java</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: skygroup</p>
 *
 * @author skyforce
 * @version 1.0
 */
public class DuplicateLinkedList<E>
    extends LinkedList<E> {
  /**
   * Constructs an empty list.
   *
   */
  public DuplicateLinkedList() {
    super();
  }

  public DuplicateLinkedList(E e) {
    super();
    this.push(e);
  }

  /**
   * Constructs a list containing the elements of the specified collection, in
   * the order they are returned by the collection's iterator.
   *
   * @param c the collection whose elements are to be placed into this list
   */
  public DuplicateLinkedList(Collection<E> c) {
    super(c);
  }

  private boolean duplicate;
  public boolean duplicate() {
    return duplicate;
  }

  /**
   * 檢查最後last個是否有重複
   * @param last int
   * @return boolean
   */
  public boolean duplicate(int last) {
    DuplicateLinkedList<E> list = lastToList(last);
    return list.duplicate(true);
  }

  /**
   * 檢查是否有重複
   * @param checking boolean
   * @return boolean
   */
  public boolean duplicate(boolean checking) {
    if (checking) {
      int size = this.size();
      duplicate = false;

      for (int x = size - 1; x >= 0; x--) {
        E e = this.get(x);
        int index = this.indexOf(e);
        int lastIndex = this.lastIndexOf(e);
        duplicate = duplicate || (index != lastIndex);
        if (true == duplicate) {
          break;
        }
      }

    }
    return duplicate;
  }

  public DuplicateLinkedList<E> lastToList(int last) {
    int size = this.size();
    int start = size - last;
    start = start < 0 ? 0 : start;
    DuplicateLinkedList<E> list = new DuplicateLinkedList<E> ();

    for (int x = 0; x < last && x + start < size; x++) {
      list.add( (E)this.get(x + start));
    }

    return list;
  }

  /**
   * 將最後n個轉成Array, n由a的length決定
   * 如果n==0, 則將全部回傳
   * @param a T[]
   * @return T[]
   */
  public <T> T[] lastToArray(T[] a) {
    int size = this.size();
    if (a.length == 0) {
      //length ==0 將全部的結果回傳
      //因此先產生可放所有結果長度的陣列
      a = (T[]) java.lang.reflect.Array.newInstance(
          a.getClass().getComponentType(), size);
    }

    int asize = a.length;
    int start = size - asize;
    start = start < 0 ? 0 : start;
    for (int x = 0; x < asize && x + start < size; x++) {
      a[x] = (T)this.get(x + start);
    }

    return a;
  }

  public static void main(String[] args) {
    DuplicateLinkedList list = new DuplicateLinkedList<RGB> ();
    list.add(RGB.White);
    list.add(RGB.Red);
    list.add(RGB.Green);
    list.add(RGB.Blue);
    list.add(RGB.White);
    list.add(RGB.Red);
    list.add(RGB.Green);
    list.add(RGB.Blue);
//    System.out.println(list.duplicate());
//    System.out.println(list.duplicate(true));
    System.out.println(list.duplicate(5));
//    System.out.println(Arrays.toString(list.lastToArray(new RGB[0])));
//    System.out.println(Arrays.toString(list.lastToArray(new RGB[3])));
  }

  /**
   * Ensures that this collection contains the specified element (optional
   * operation).
   *
   * @param e element whose presence in this collection is to be ensured
   * @return <tt>true</tt> if this collection changed as a result of the call
   */
  public boolean add(E e) {
    boolean contains = this.contains(e);
    boolean result = super.add(e);
    duplicate = duplicate || contains;
    return result;
  }

  public boolean addAll(E[] array) {
    for (E e : array) {
      this.add(e);
    }
    return true;
  }
}
