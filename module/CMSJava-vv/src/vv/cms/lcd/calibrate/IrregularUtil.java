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
     * �ˬd�O�_�����઺�{�H
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
     * �ˬd�����`�����p, �åB�^�Ǭ�����T
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
     * �ˬd�O�_�����઺�{�H
     * @param values double[]
     * @return int ���઺����
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
     * �ˬd�O�_�����઺�{�H
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
     * ���X�z�Ȫ��ץ� 1.�t�� 2.����
     * 1.�t�ȥΫe�@�өM�U�@�ӧ@����
     * 2.����γo�@�өM�U��ӧ@����
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
                //���t��
                RGB preRGB = rgbArray[x - 1];
                double precode = preRGB.getValue(ch);
                double interp = Interpolation.linear(1, 3, precode, next, 2);
                rgb.setValue(ch, interp);
                fixTimes++;
            } else if (isIrregular(now, next)) {
                //������ or ��code
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
     * �����B���ץ�
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
                //���t��
                //�p�G���t��, �N��pre��next�����X�s��now
                double pre = values[x - 1];
                double interp = Interpolation.linear(1, 3, pre, next, 2);
                values[x] = interp;
                fixTimes++;
            } else if (isIrregular(now, next)) {
                //������
                //�p�G������, �]�N�Onext�p�󵥩�now, ���ɴN��next�}�M
                //�Hnext2�Mnow�����Xnext
                double next2 = values[x + 2];
                double interp = Interpolation.linear(1, 3, now, next2, 2);
                values[x + 1] = interp;
                fixTimes++;
            }
        }
        return fixTimes;
    }

    /**
     * �ץ����t�ȤΤ��઺���p
     * @param rgbArray RGB[]
     * @return int �ץ�������
     */
    public final static int irregularFix(RGB[] rgbArray) {
        int fixTimes = 0;

        for (RGBBase.Channel ch : RGBBase.Channel.RGBChannel) {
            fixTimes += irregularFix(rgbArray, ch);
        }
        return fixTimes;
    }

}
