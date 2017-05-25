package auo.math.lut;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import shu.util.Searcher;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * 用來對照表所需要的key以及value
 * 提供一些常用的操作,方便對照表的使用.
 *
 * 譬如說,可以針對key或者value的某一個欄位進行排序.
 * 可以將table中的部份結果取出..etc
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: </p>
 *
 * @author cms.shu.edu.tw
 * @version 1.0
 */
public class CubeTable implements Serializable {

    /**
     *
     * <p>Title: Colour Management System</p>
     *
     * <p>Description: a Colour Management System by Java</p>
     * 用來表示一個key與value的組合
     *
     * <p>Copyright: Copyright (c) 2008</p>
     *
     * <p>Company: skygroup</p>
     *
     * @author skyforce
     * @version 1.0
     */
    public static class KeyValue implements Comparable, Serializable {
        /**
         * 儲存key和value,第一個元素代表key,第二個代表value
         */
        protected short[][] keyValue = new short[2][];
        protected int index;
//        protected double rmsd;

        public KeyValue(short[] key, short[] value) {
            keyValue[0] = key;
            keyValue[1] = value;
        }

        /**
         * 取得key值
         * @return double[]
         */
        public short[] getKey() {
            return keyValue[0];
        }

        public int getValueCounts() {
            return keyValue[1].length;
        }

        /**
         * 取得value值
         * @return double[]
         */
        public short[] getValue() {
            return keyValue[1];
        }

        /**
         * 由searchType指定取得key或value
         * @param searchType SearchType
         * @return double[]
         */
        public short[] getKeyValue(SearchType searchType) {
            return keyValue[searchType.index];
        }

        /**
         * 同時回傳key和value
         * @return double[][]
         */
        public short[][] getKeyValue() {
            return keyValue;
        }

        /**
         * 由searchType指定取得key或value的長度
         * @param searchType SearchType
         * @return int
         */
        public int getLength(SearchType searchType) {
            return keyValue[searchType.index].length;
        }

        /**
         * Compares this object with the specified object for order.
         *
         * @param o the Object to be compared.
         * @return a negative integer, zero, or a positive integer as this object
         *   is less than, equal to, or greater than the specified object.
         */
        public int compareTo(Object o) {
            short val0 = this.keyValue[searchType.index][searchIndex];
            short val1 = ((KeyValue) o).keyValue[searchType.index][searchIndex];
            return new Short(val0).compareTo(val1);
        }

        /**
         * Returns a hash code value for the object.
         *
         * @return a hash code value for this object.
         */
        public int hashCode() {
            return super.hashCode();
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            buf.append('[');
            buf.append(Arrays.toString(keyValue[0]));
            buf.append(',');
            buf.append(Arrays.toString(keyValue[1]));
            buf.append(']');
            return buf.toString();
        }

        /**
         * 將浮點數陣列轉換為KeyValue陣列.
         * 浮點數陣列的格式為: 第三維 索引值; 第二維 input及output; 第一維in/out對應的數值
         * ex: {{1,3},{3,4}},{{2,1},{1,1}} 為(1,3)對應(3,4)且(2,1)對應(1,1)
         *
         * @param dArray double[][][]
         * @return KeyValue[]
         */
        protected final static KeyValue[] toKeyValueArray(short[][][] dArray) {
            int size = dArray.length;
            KeyValue[] kvArray = new KeyValue[size];
            for (int x = 0; x < size; x++) {
                kvArray[x] = new KeyValue(dArray[x][0], dArray[x][1]);
                kvArray[x].index = x;
            }
            return kvArray;
        }


        protected final static short[][] toValueArray(KeyValue[] kvArray) {
            int size = kvArray.length;
            short[][] valueArray = new short[size][];
            for (int x = 0; x < size; x++) {
                valueArray[x] = kvArray[x].getValue();
            }
            return valueArray;
        }

        /**
         * Indicates whether some other object is "equal to" this one.
         *
         * @param obj the reference object with which to compare.
         * @return <code>true</code> if this object is the same as the obj
         *   argument; <code>false</code> otherwise.
         */
        public boolean equals(Object obj) {
            return compareTo(obj) == 0;
        }

    }


    private static SearchType searchType = SearchType.Value;
    private static int searchIndex = 0;

    public static void setSearchType(SearchType type) {
        searchType = type;
    }

    public static void setSearchIndex(int index) {
        searchIndex = index;
    }

    public void setLeftNearSearch(boolean leftNearSearch) {
        this.leftNearSearch = leftNearSearch;
    }

    public enum SearchType {
        Key(0), Value(1);

        SearchType(int index) {
            this.index = index;
        }

        int index = 0;
    }


    protected KeyValue[] lut;
    protected short[] keyMaxValue;
    protected short[] keyMinValue;

//    protected int[] indexOfValueSort;

//    /**
//     * 依照Value排序的順序產生的index列表
//     */
//    public void initIndexOfValueSort() {
//        if (indexOfValueSort == null) {
//            KeyValue[] clone = lut.clone();
//            Arrays.sort(clone, xyzValueComparator);
//            int size = clone.length;
//            indexOfValueSort = new int[size];
//            for (int x = 0; x < size; x++) {
//                indexOfValueSort[x] = clone[x].index;
//            }
//        }
//
//    }

//    /**
//     * 利用rmsd的順序去搜尋
//     * @param value double[]
//     * @return KeyValue[]
//     */
//    public final KeyValue[] getKeyValueByRMSDSort(double[] value) {
//        KeyValue[] keyValueArray = lut.clone();
//        int size = keyValueArray.length;
//        for (int x = 0; x < size; x++) {
//            KeyValue kv = keyValueArray[x];
//            kv.rmsd = Maths.RMSD(kv.getValue(), value);
//        }
//        Arrays.sort(keyValueArray, rmsComparator);
//        return keyValueArray;
//    }

//    /**
//     * 取得n個最接近的keyValue,且以rms的順序排列
//     * @param n int
//     * @param value double[]
//     * @return double[][][]
//     */
//    protected final KeyValue[] getNNearKeyValueByRMSSort(int n, double[] value) {
//        KeyValue[] nNearValue = getNNearKeyValue(n, value);
//        int size = nNearValue.length;
//        for (int x = 0; x < size; x++) {
//            KeyValue kv = nNearValue[x];
//            kv.rmsd = Maths.RMSD(kv.getValue(), value);
//        }
//        Arrays.sort(nNearValue, rmsComparator);
//        return nNearValue;
//    }

//    protected final KeyValue[] getNNearKeyValue(int n, double[] value) {
//        int valueIndex = leftNearBinarySearchValue(value);
//        int startIndex = (valueIndex - n < 0) ? 0 : (valueIndex - n);
//
//        int size = n * 2 + 1;
//        KeyValue[] nearKeyValue = new KeyValue[size];
//        for (int x = 0; x < size; x++) {
//            nearKeyValue[x] = lut[getKeyIndex(startIndex + x)];
//        }
//
//        return nearKeyValue;
//    }

//    /**
//     *
//     * @param value double[]
//     * @return int 依照value排序的順位
//     */
//    protected final int leftNearBinarySearchValue(short[] value) {
//        int binarySearchResulte = binarySearchValue(value);
//        return Searcher.leftNearBinarySearch0(size(), binarySearchResulte);
//    }

//    /**
//     * 搜尋左邊接近的value
//     * @param value double[]
//     * @return int 依照value排序的順位
//     */
//    protected final int binarySearchValue(short[] value) {
//        KeyValue val = new KeyValue(null, value);
//        return binarySearchValue(lut, indexOfValueSort, val, xyzValueComparator);
//    }

    /**
     * 二元搜尋value是否在a裡面.
     * 且a的順序是以indexOfValueSort為主
     *
     * @param a KeyValue[] 要被搜尋的array
     * @param indexOfValueSort int[] a數值的順序
     * @param value KeyValue 要被搜尋的值
     * @param c Comparator 比較用的物件
     * @return int 依照value排序的順位
     */
    protected final static int binarySearchValue(KeyValue[] a,
                                                 int[] indexOfValueSort,
                                                 KeyValue value,
                                                 Comparator<KeyValue> c) {

        int low = 0;
        int high = a.length - 1;

        while (low <= high) {
            int mid = (low + high) >> 1;
            KeyValue midVal = a[indexOfValueSort[mid]];

            int cmp = c.compare(midVal, value);

            if (cmp < 0) {
                low = mid + 1;
            } else if (cmp > 0) {
                high = mid - 1;
            } else {
                return mid; // key found
            }
        }
        return -(low + 1); // key not found.
    }

    public int size() {
        return lut.length;
    }


//    /**
//     * @param lut double[][][]
//     * 浮點數陣列的格式為: 第一維 索引值; 第二維 input及output; 第三維in/out對應的數值
//     * ex: {{1,3},{3,4}},{{2,1},{1,1}} 為(1,3)對應(3,4)且(2,1)對應(1,1)
//     *
//     * ex:
//     * *---*---*---*
//     * *為資料節點
//     * grid: 4
//     * stair = 3
//     *
//     * @param lut double[][][]
//     * @param axisXKeys double[]
//     * @param axisYKeys double[]
//     * @param axisZKeys double[]
//     */
//    public CubeTable(double[][][] lut, double[] axisXKeys, double[] axisYKeys,
//                     double[] axisZKeys) {
//        this(KeyValue.toKeyValueArray(lut), axisXKeys, axisYKeys, axisZKeys);
//    }

    public CubeTable(KeyValue[] lut, short[] axisXKeys, short[] axisYKeys,
                     short[] axisZKeys) {
        this.lut = lut;
        this.axisKeys = new short[][] {
                        axisXKeys, axisYKeys, axisZKeys};
        keyMinValue = new short[3];
        keyMaxValue = new short[3];
        for (int x = 0; x < 3; x++) {
            keyMinValue[x] = axisKeys[x][0];
            int size = axisKeys[x].length;
            keyMaxValue[x] = axisKeys[x][size - 1];
        }
    }

    /**
     * 是否為合理的key
     * (超出keyMin和keyMax為不合理)
     * @param key double[]
     * @return boolean
     */
    public boolean isValidKey(double[] key) {
        if (key.length != keyMinValue.length) {
            throw new IllegalArgumentException("key.length is not valid.");
        }
        int size = key.length;
        boolean invalid = false;
        for (int x = 0; x < size; x++) {
            invalid = invalid || key[x] < keyMinValue[x];
            invalid = invalid || key[x] > keyMaxValue[x];
        }

        return!invalid;
    }

    public int getIndex(int[] gridKeyIndex) {
        if (null != indexFinder) {
            return indexFinder.getIndex(gridKeyIndex);
        } else {
            throw new UnsupportedOperationException();
        }

    }


    public interface IndexFinderIF {
        public int getIndex(short[] gridKey);

        public int getIndex(int[] gridKeyIndex);
    }


    private IndexFinderIF indexFinder;
    public void registerIndexFinderIF(IndexFinderIF indexFinder) {
        this.indexFinder = indexFinder;
    }

    public short[] getKey(int index) {
        return lut[index].getKey();
    }

    public short[] getValue(int index) {
        return lut[index].getValue();
    }

    public short[][] getKeyValueArray(int index) {
        return lut[index].getKeyValue();
    }

    public CubeTable.KeyValue getKeyValue(int index) {
        return lut[index];
    }

    /**
     * 每一筆value的數值個數
     * @return int
     */
    public int getValueCounts() {
        return lut[0].getValueCounts();
    }

//    public short[][] getKeyValueByValueIndex(int valueIndex) {
//        return lut[indexOfValueSort[valueIndex]].getKeyValue();
//    }

//    public int getKeyIndex(int valueIndex) {
//        return indexOfValueSort[valueIndex];
//    }

    public int getLength(SearchType searchType) {
        return lut[0].getLength(searchType);
    }

//  public static void main(String[] args) {
//    int size = (int) Math.pow(4, 3);
//    double[][][] lut = new double[size][2][];
//    double[] rgbValues = new double[3];
//    int index = 0;
//
//    for (double r = 0; r <= 1; r += .33) {
//      for (double g = 0; g <= 1; g += .33) {
//        for (double b = 0; b <= 1; b += .33) {
//          rgbValues[0] = r;
//          rgbValues[1] = g;
//          rgbValues[2] = b;
//          double[] XYZValues = RGB.toXYZValues(rgbValues,
//                                               RGB.ColorSpace.sRGB);
//          lut[index][0] = rgbValues.clone();
//          lut[index][1] = XYZValues;
////          lut[index][1]=new double[]{XYZValues[0]};
//          index++;
//
//          System.out.println(DoubleArray.toString(rgbValues) + " " +
//                             DoubleArray.toString(XYZValues));
//        }
//      }
//    }
//
//    CubeTable ct = new CubeTable(lut, new double[] {0, 0, 0}, new double[] {1,
//                                 1, 1}, 4);
//    Persistence.writeObjectAsXML(ct, "cube.xml");
//    Persistence.writeObject(ct, "cube.tbl");
//  }


    public short[] getKeyMaxValue() {
        return keyMaxValue;
    }

    public short[] getKeyMinValue() {
        return keyMinValue;
    }


    public int[] getNearestNodeKeyIndex(short[] keys) {
        int[] index = new int[3];
        for (int x = 0; x < 3; x++) {
            if (leftNearSearch) {
                index[x] = Searcher.leftNearBinarySearch(axisKeys[x], keys[x]);
            } else {
                index[x] = Searcher.leftBinarySearch(axisKeys[x], keys[x]);
            }
        }
        return index;
    }

    private boolean leftNearSearch = true;

    public short getNearestNodeKey(int axis, short key) {
        int index = Searcher.leftNearBinarySearch(axisKeys[axis], key);
        return axisKeys[axis][index];
    }

    public short[] getNearestNodeKey(short[] keys) {
        short[] nodeKey = new short[3];
        for (int x = 0; x < 3; x++) {
            nodeKey[x] = getNearestNodeKey(x, keys[x]);
        }
        return nodeKey;
    }

    private short[][] axisKeys; // = new double[3][];
    public short[] getAxisKeys(int axis) {
        return axisKeys[axis];
    }

    public int getGrid(int axis) {
        return axisKeys[axis].length;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return a string representation of the object.
     */
    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (KeyValue keyValue : lut) {
            buf.append(keyValue.toString());
            buf.append('\n');
        }
        return buf.toString();
    }

    private static int[] getCoordinateIndex(int[] keyIndex) {
        int x0 = keyIndex[0];
        int y0 = keyIndex[1];
        int z0 = keyIndex[2];
        int x1 = keyIndex[0] + 1;
        int y1 = keyIndex[1] + 1;
        int z1 = keyIndex[2] + 1;
        return new int[] {
                x0, y0, z0, x1, y1, z1};
    }

    private int[] interpolateCellIndex;
    public int[] getInterpolateCellIndex() {
        return interpolateCellIndex;
    }

    public CubeTable.KeyValue[] getInterpolateCell(short[] key) {
        int[] nodeKeyIndex = this.getNearestNodeKeyIndex(key);
//========================================================================
//    if (showCellChange && null != preNodeKeyIndex &&
//        !Arrays.equals(preNodeKeyIndex, nodeKeyIndex)) {
//      System.out.println("changed: " + IntArray.toString(preNodeKeyIndex) +
//                         "->" + IntArray.toString(nodeKeyIndex));
//    }
//    preNodeKeyIndex = nodeKeyIndex;
//========================================================================

        int[] C = getCoordinateIndex(nodeKeyIndex);
        interpolateCellIndex = C;

        CubeTable.KeyValue[] result = new CubeTable.KeyValue[8];
        result[0] = this.getKeyValue(this.getIndex(new int[] {
                C[0], C[1], C[2]}));
        result[1] = this.getKeyValue(this.getIndex(new int[] {
                C[3], C[1], C[2]}));
        result[2] = this.getKeyValue(this.getIndex(new int[] {
                C[0], C[4], C[2]}));
        result[3] = this.getKeyValue(this.getIndex(new int[] {
                C[3], C[4], C[2]}));
        result[4] = this.getKeyValue(this.getIndex(new int[] {
                C[0], C[1], C[5]}));
        result[5] = this.getKeyValue(this.getIndex(new int[] {
                C[3], C[1], C[5]}));
        result[6] = this.getKeyValue(this.getIndex(new int[] {
                C[0], C[4], C[5]}));
        result[7] = this.getKeyValue(this.getIndex(new int[] {
                C[3], C[4], C[5]}));
        return result;
    }

}
