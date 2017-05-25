package vv.cms.lcd.calibrate;

import java.util.*;

import shu.cms.colorspace.depend.*;
import shu.math.*;

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
public class IrregularUtil {
    public final static boolean isIrregular(RGB[] rgbArray, RGBBase.Channel ch) {
        return irregularCount(rgbArray, ch) != 0;
    }

    /**
     * 檢查是否有反轉的現象
     * @param rgbArray RGB[]
     * @return int
     */
    public final static int irregularCount(final RGB[] rgbArray) {
        int count = 0;
        for (RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
            count += irregularCount(rgbArray, ch);
        }
        return count;
    }

    public static boolean isIrregular(double now, double next) {
        if (allowEqual) {
            return next < now;
        } else {
            return next <= now;
        }
    }

    public static enum Type {
        Equal, Reverse, All, None
    }


    public static class Info {
        public int index;
        public Type type;
        public int width = -1;

        public Info(int index, Type type, int width) {
            this.index = index;
            this.type = type;
            this.width = width;
        }

        public Info(int index, Type type) {
            this.index = index;
            this.type = type;
        }

        public String toString() {
            return index + ": " + type + ((width == -1) ? "" : " " + width);
        }
    }


    /**
     * 檢查不正常的狀況, 並且回傳相關資訊
     * @param rgbArray RGB[]
     * @param ch Channel
     * @return Info[]
     */
    public final static Info[] getIrregularInfo(final RGB[] rgbArray,
                                                RGBBase.Channel ch) {
        int size = rgbArray.length;
        List<Info> infoList = new ArrayList<Info>();

        for (int x = 1; x < size - 1; x++) {
            RGB rgb = rgbArray[x];
            RGB nextRGB = rgbArray[x + 1];
            double now = rgb.getValue(ch);
            double next = nextRGB.getValue(ch);
            if (isIrregular(now, next)) {
                int y = x + 2;
                for (; y < size; y++) {
                    RGB nextnextRGB = rgbArray[y];
                    double nextnext = nextnextRGB.getValue(ch);
                    if (!isIrregular(now, nextnext)) {
                        break;
                    }
                }
                int width = y - x;

                Type type = getIrregularType(now, next);
                Info info = new Info(x, type, width);
                infoList.add(info);
                x += width - 1;
            }
        }
        Info[] result = infoList.toArray(new Info[infoList.size()]);
        return result;
    }

    private static Type getIrregularType(double now, double next) {
        boolean equal = isEqualIrregular(now, next);
        boolean reverse = isReverseIrregular(now, next);
        if (equal && reverse) {
            return Type.All;
        } else if (equal) {
            return Type.Equal;
        } else if (reverse) {
            return Type.Reverse;
        } else {
            return Type.None;
        }
    }

    private static boolean isEqualIrregular(double now, double next) {
        return next == now;
    }

    private static boolean isReverseIrregular(double now, double next) {
        return next < now;
    }

    private static boolean allowEqual = false;

    public final static void setAllowEqual(boolean allow) {
        allowEqual = allow;
    }

    /**
     * 檢查是否有反轉的現象
     * @param values double[]
     * @return int 反轉的次數
     */
    public final static int irregularCount(double[] values) {
        int size = values.length;
        int count = 0;

        for (int x = 1; x < size - 2; x++) {
            double now = values[x];
            double next = values[x + 1];
            if (isIrregular(now, next)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 檢查是否有反轉的現象
     * @param rgbArray RGB[]
     * @param ch Channel
     * @return int
     */
    public final static int irregularCount(final RGB[] rgbArray,
                                           RGBBase.Channel ch) {
        int size = rgbArray.length;
        int count = 0;

        for (int x = 1; x < size - 1; x++) {
            RGB rgb = rgbArray[x];
            RGB nextRGB = rgbArray[x + 1];
            double now = rgb.getValue(ch);
            double next = nextRGB.getValue(ch);
            if (isIrregular(now, next)) {
                count++;
            }
        }
        return count;
    }

    public final static boolean isIrregular(double[] values) {
        return irregularCount(values) != 0;
    }

    public final static boolean isIrregular(RGB[] rgbArray) {
        return irregularCount(rgbArray) != 0;
    }

    /**
     * 不合理值的修正 1.負值 2.反轉
     * 1.負值用前一個和下一個作內插
     * 2.反轉用這一個和下兩個作內插
     * @param rgbArray RGB[]
     * @param ch Channel
     * @return int
     */
    public final static int irregularFix(RGB[] rgbArray, RGBBase.Channel ch) {
        int size = rgbArray.length;
        int fixTimes = 0;

        for (int x = 1; x < size - 2; x++) {
            RGB rgb = rgbArray[x];
            RGB nextRGB = rgbArray[x + 1];
            double now = rgb.getValue(ch);
            double next = nextRGB.getValue(ch);
            if (now < 0) {
                //有負值
                RGB preRGB = rgbArray[x - 1];
                double precode = preRGB.getValue(ch);
                double interp = Interpolation.linear(1, 3, precode, next, 2);
                rgb.setValue(ch, interp);
                fixTimes++;
            } else if (isIrregular(now, next)) {
                //有反轉 or 等code
                RGB next2RGB = rgbArray[x + 2];
                double next2code = next2RGB.getValue(ch);
                double interp = Interpolation.linear(1, 3, now, next2code, 2);
                nextRGB.setValue(ch, interp);
                fixTimes++;
            }
        }
        return fixTimes;
    }

    /**
     * 對反轉處做修正
     * @param values double[]
     * @return int
     */
    public final static int irregularFix(double[] values) {
        int size = values.length;
        int fixTimes = 0;

        for (int x = 1; x < size - 2; x++) {
            double now = values[x];
            double next = values[x + 1];
            if (now < 0) {
                //有負值
                //如果有負值, 就用pre跟next內插出新的now
                double pre = values[x - 1];
                double interp = Interpolation.linear(1, 3, pre, next, 2);
                values[x] = interp;
                fixTimes++;
            } else if (isIrregular(now, next)) {
                //有反轉
                //如果有反轉, 也就是next小於等於now, 此時就對next開刀
                //以next2和now內插出next
                double next2 = values[x + 2];
                double interp = Interpolation.linear(1, 3, now, next2, 2);
                values[x + 1] = interp;
                fixTimes++;
            }
        }
        return fixTimes;
    }

    /**
     * 修正有負值及反轉的狀況
     * @param rgbArray RGB[]
     * @return int 修正的次數
     */
    public final static int irregularFix(RGB[] rgbArray) {
        int fixTimes = 0;

        for (RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
            fixTimes += irregularFix(rgbArray, ch);
        }
        return fixTimes;
    }

}
