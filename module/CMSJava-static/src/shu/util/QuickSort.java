package shu.util;

import java.util.*;

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
public class QuickSort {
  /*
    private static final String EmbeddedCopyright =
    "copyright (c) 1996-2003 Roedy Green, Canadian Mind Products, http://mindprod.com";
   */

  private static final boolean debugging = false;

  // callback object we are passed that has
  // a Comparator(Object a, Object b) method.
  private Comparator delegate;

  // pointer to the array of user's objects we are sorting
  private List userArray;
  // create a QuickSort object and sort the user's array
  public static void sort(List userArray, Comparator delegate) {
    QuickSort h = new QuickSort();
    h.delegate = delegate;
    h.userArray = userArray;
//      if ( h.isAlreadySorted() ) return;
    h.quicksort(0, userArray.size() - 1);
    //if ( h.isAlreadySorted() ) return;
    if (debugging) {
      // debug ensure sort is working
      if (!h.isAlreadySorted()) {
        System.out.println("Sort failed");
      }
    }
    return;
  } // end sort

  // recursive quicksort that breaks array up into sub
  // arrays and sorts each of them.
  private void quicksort(int p, int r) {
    if (p < r) {
      int q = partition(p, r);
      if (q == r) {
        q--;
      }
      quicksort(p, q);
      quicksort(q + 1, r);
    } // end if
  } // end quicksort

  // Partition by splitting this chunk to sort in two and
  // get all big elements on one side of the pivot and all
  // the small elements on the other.
  private int partition(int lo, int hi) {
    Object pivot = userArray.get(lo);
    while (true) {
      while (delegate.compare(userArray.get(hi), pivot) >= 0 &&
             lo < hi) {
        hi--;
      }
      while (delegate.compare(userArray.get(lo), pivot) < 0 &&
             lo < hi) {
        lo++;
      }
      if (lo < hi) {
        // exchange objects on either side of the pivot
        Object T = userArray.get(lo);
        userArray.set(lo, userArray.get(hi));
        userArray.set(hi, T);
      }
      else {
        return hi;
      }
    } // end while
  } // end partition

  // check if user's array is already sorted
  private boolean isAlreadySorted() {
    for (int i = 1; i < userArray.size(); i++) {
      if (delegate.compare(userArray.get(i), userArray.get(i - 1)) < 0) {
        return false;
      }
    }
    return true;
  } // end isAlreadySorted

} // end class QuickSort
