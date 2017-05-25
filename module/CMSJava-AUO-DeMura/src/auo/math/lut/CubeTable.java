package auo.math.lut;


import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import shu.util.Searcher;

/**
 * <p>Title: Colour Management System</p>
 *
 * <p>Description: </p>
 * �Ψӹ�Ӫ�һݭn��key�H��value
 * ���Ѥ@�Ǳ`�Ϊ��ާ@,��K��Ӫ��ϥ�.
 *
 * Ĵ�p��,�i�H�w��key�Ϊ�value���Y�@�����i��Ƨ�.
 * �i�H�Ntable�����������G���X..etc
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
     * �ΨӪ�ܤ@��key�Pvalue���զX
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
         * �x�skey�Mvalue,�Ĥ@�Ӥ����N��key,�ĤG�ӥN��value
         */
        protected short[][] keyValue = new short[2][];
        protected int index;
//        protected double rmsd;

        public KeyValue(short[] key, short[] value) {
            keyValue[0] = key;
            keyValue[1] = value;
        }

        /**
         * ���okey��
         * @return double[]
         */
        public short[] getKey() {
            return keyValue[0];
        }

        public int getValueCounts() {
            return keyValue[1].length;
        }

        /**
         * ���ovalue��
         * @return double[]
         */
        public short[] getValue() {
            return keyValue[1];
        }

        /**
         * ��searchType���w���okey��value
         * @param searchType SearchType
         * @return double[]
         */
        public short[] getKeyValue(SearchType searchType) {
            return keyValue[searchType.index];
        }

        /**
         * �P�ɦ^��key�Mvalue
         * @return double[][]
         */
        public short[][] getKeyValue() {
            return keyValue;
        }

        /**
         * ��searchType���w���okey��value������
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
         * �N�B�I�ư}�C�ഫ��KeyValue�}�C.
         * �B�I�ư}�C���榡��: �ĤT�� ���ޭ�; �ĤG�� input��output; �Ĥ@��in/out�������ƭ�
         * ex: {{1,3},{3,4}},{{2,1},{1,1}} ��(1,3)����(3,4)�B(2,1)����(1,1)
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
//     * �̷�Value�ƧǪ����ǲ��ͪ�index�C��
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
//     * �Q��rmsd�����ǥh�j�M
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
//     * ���on�ӳ̱���keyValue,�B�Hrms�����ǱƦC
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
//     * @return int �̷�value�ƧǪ�����
//     */
//    protected final int leftNearBinarySearchValue(short[] value) {
//        int binarySearchResulte = binarySearchValue(value);
//        return Searcher.leftNearBinarySearch0(size(), binarySearchResulte);
//    }

//    /**
//     * �j�M���䱵��value
//     * @param value double[]
//     * @return int �̷�value�ƧǪ�����
//     */
//    protected final int binarySearchValue(short[] value) {
//        KeyValue val = new KeyValue(null, value);
//        return binarySearchValue(lut, indexOfValueSort, val, xyzValueComparator);
//    }

    /**
     * �G���j�Mvalue�O�_�ba�̭�.
     * �Ba�����ǬO�HindexOfValueSort���D
     *
     * @param a KeyValue[] �n�Q�j�M��array
     * @param indexOfValueSort int[] a�ƭȪ�����
     * @param value KeyValue �n�Q�j�M����
     * @param c Comparator ����Ϊ�����
     * @return int �̷�value�ƧǪ�����
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
//     * �B�I�ư}�C���榡��: �Ĥ@�� ���ޭ�; �ĤG�� input��output; �ĤT��in/out�������ƭ�
//     * ex: {{1,3},{3,4}},{{2,1},{1,1}} ��(1,3)����(3,4)�B(2,1)����(1,1)
//     *
//     * ex:
//     * *---*---*---*
//     * *����Ƹ`�I
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
     * �O�_���X�z��key
     * (�W�XkeyMin�MkeyMax�����X�z)
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
     * �C�@��value���ƭȭӼ�
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
