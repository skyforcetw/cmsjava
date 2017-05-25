package auo.cms.ed;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.math.array.IntegerArray;
import shu.plot.Plot2D;
import shu.plot.Plot3D;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public abstract class AUORandom {
    protected long seed;
    public long getSeed() {
        return seed;
    }

    private AUORandom(long seed) {
        this.seed = seed;
    }

    public static AUORandom getLCGInstance(long seed) {
        return new LCGRandom(seed);
    }

//    public static AUORandom getMaximLFSRInstance() {
//        return new Maxim2LFSR();
//    }

//    public static AUORandom getMaximLFSR32_16Instance() {
//        return new MaximLFSR32_16();
//    }
//
//    public static AUORandom getMaximLFSR16_4Instance(long seed) {
//        return new MaximLFSR16_4(seed);
//    }

    public abstract int next(int bits);

    public abstract int next();

    public abstract long nextSeed();

    public static void main(String[] args) throws IOException {
        long seed = 0x1F9FA417B064L; //8825252*3939889;

        AUORandom lcg = AUORandom.getLCGInstance(seed);
//        AUORandom lfsr16 = AUORandom.getWikiLFSR16_4Instance(seed);
//        AUORandom lfsr32 = AUORandom.getWikiLFSRInstance(seed, 32, 32, 31, 29, 1);
//        AUORandom lfsr31 = AUORandom.getWikiLFSRInstance(seed, 31, 31, 30, 29, 28);
//        AUORandom lfsr32 = AUORandom.getWikiLFSR32_16Instance(3939889);
//        AUORandom lfsr31 = AUORandom.getWikiLFSR31_16Instance(28825252);
        AUORandom lfsr16 = AUORandom.getWikiLFSRInstance(8825252 * 27662000, 16, 16, 14, 13, 11);
        AUORandom lfsr15 = AUORandom.getWikiLFSRInstance(3939889 * 4125252, 15, 15, 14, 13, 11);
//        AUORandom.getWikiLFSRInstance(seed,)


        boolean writeToFile = false;
        long size = writeToFile ? 51320000 - 1 : 9800; //~=3840 * 2160;
//        long size = writeToFile ? 40000000 - 1 : 10000; //~=3840 * 2160;

        BufferedWriter writer = null;
        if (writeToFile) {
            writer = new BufferedWriter(new FileWriter(
                    "D:/MinGW/usr/share/TestU01/examples/bat1/lcg16.txt"));
            //          writer = new BufferedWriter(new FileWriter(
            //                "D:/MinGW/usr/share/TestU01/examples/bat1/lfsr16.txt"));
        }

        int max = 6;
        int dim = max + 1;
//        int max = (int) Math.pow(2, 16) - 1;

        Plot2D plot = Plot2D.getInstance();
        Plot3D plot3D = Plot3D.getInstance();
        double pref = 0;
        int prerandnum = 0;
        int[][] hist = new int[dim][dim];
        double[][] XYdX = new double[dim * dim][5];

        for (int n = 0; n < size; n++) {
//            int rand = lcg.next(3);

            lfsr16.nextSeed();
            long randnum1 = lfsr16.nextSeed(); //15
            long randnum2 = lfsr15.nextSeed();

            long xor = randnum1 ^ randnum2;
            int mask = (int) Math.pow(2, 3) - 1;
            int rand = (int) (xor & mask);
//            int rand = lcg.next(31);

//            int rand = lcg.next(5);
//            rand = (int) (rand & mask);

            int randnum = rand % (max + 1);

//            long v = 0;
//            for (int x = 0; x < max; x++) {
//                v = v + ((xor >> x) & 1);
//            }
//            randnum = (int) v;

            double f = ((double) randnum) / max;
            if (writeToFile) {
                writer.write(Double.toString(f));
                writer.write(' ');
            } else {
                if ((n + 1) % 2 == 0) {
                    plot.addScatterPlot("", Color.red, pref, f);
                    hist[prerandnum][randnum]++;
                }
            }
            pref = f;
            prerandnum = randnum;

        }
        if (writeToFile) {
            writer.flush();
            writer.close();
        } else {
            plot.setVisible();
            for (int m = 0; m < dim; m++) {
                for (int n = 0; n < dim; n++) {
                    int index = m * dim + n;
                    XYdX[index][0] = m;
                    XYdX[index][1] = n;
                    XYdX[index][2] = hist[m][n];
                    XYdX[index][3] = 1;
                    XYdX[index][4] = 1;
                }
            }
//            plot.addhis
            plot3D.addHistogramPlot("", XYdX);
//            plot3D.addHistogramPlot()
            plot3D.setVisible();
        }
//        DoubleArray.
        System.out.println(IntegerArray.toString(hist));
    }

    static long getLFSR4Mask(long bit1, long bit2, long bit3, long bit4) {
        long mask = (1L << (bit1 - 1)) + (1L << (bit2 - 1)) + (1L << (bit3 - 1)) + (1L << (bit4 - 1));
        return mask;
    }

    static long nextLFSR4Seed(int bit1, int bit2, int bit3, int bit4, long seed) {
        long mask = getLFSR4Mask(bit1, bit2, bit3, bit4);
        seed = (seed >> 1) ^ ( -(seed & 1) & mask);
        return seed;
    }

    static byte getLFSROutput(long seed, int bit) {
        return (byte) ((seed >> (bit - 1)) & 1);
    }


    static class MaximLFSR32_16 extends Maxim2LFSR {
        long get_random() {
            lfsr32 = shift_lfsr(lfsr32, mask32);
            return (int) (lfsr32 & 0xFFFF);
        }
    }


    static class MaximLFSR16_4 extends Maxim2LFSR {
        MaximLFSR16_4(long seed) {
            super(seed, 0);
            this.mask32 = 0xD295;
        }

        long get_random() {
            lfsr32 = shift_lfsr(lfsr32, mask32);
            return (int) (lfsr32 & 0xF);
        }
    }


    static class LFSR16Bit extends Maxim2LFSR {
        LFSR16Bit(long seed) {
            super(seed, 0);
        }

        public int next(int bits) {
            lfsr32 = shift_lfsr(lfsr32, mask32);
            int mask = 0xFFF;
            return (int) (lfsr32 & mask);

        }

        public int next() {
            throw new UnsupportedOperationException();
        }

    }


    public static AUORandom getWikiLFSR32_16Instance(long seed) {
        return new WikiLFSR32_16(seed);
    }

    static class WikiLFSR32_16 extends AUORandom {
        WikiLFSR32_16(long seed) {
            super(seed);
        }

        public int next(int bits) {
            seed = (seed >> 1) ^ ( -(seed & 1) & 0xD0000001);
            int mask = (int) Math.pow(2, bits) - 1;
            return (int) (seed & mask);
        }

        public int next() {
            seed = (seed >> 1) ^ ( -(seed & 1) & 0xD0000001);
            return (int) (seed & 0xFFFF);
        }

        public long nextSeed() {
            seed = (seed >> 1) ^ ( -(seed & 1) & 0xD0000001);
            return seed;
        }
    }


    public static AUORandom getWikiLFSR31_16Instance(long seed) {
        return new WikiLFSR31_16(seed);
    }

    static class WikiLFSR31_16 extends AUORandom {
        WikiLFSR31_16(long seed) {
            super(seed);
        }

        public int next(int bits) {
            seed = (seed >> 1) ^ ( -(seed & 1) & 0xA3000000);
            int mask = (int) Math.pow(2, bits) - 1;
            return (int) (seed & mask);
        }

        public int next() {
            seed = (seed >> 1) ^ ( -(seed & 1) & 0xA3000000);
            return (int) (seed & 0xFFFF);
        }

        public long nextSeed() {
            seed = (seed >> 1) ^ ( -(seed & 1) & 0xA3000000);
            return seed;
        }
    }


    public static AUORandom getWikiLFSR16_4Instance(long seed) {
        return new WikiLFSR16_4(seed);
    }

    public static AUORandom getWikiLFSRInstance(long seed, int seedbit, long bit1, long bit2,
                                                long bit3,
                                                long bit4) {
        return new WikiLFSRTemplate(seed, seedbit, bit1, bit2, bit3, bit4);
    }


    static class WikiLFSRTemplate extends AUORandom {
        long lfsrmask;
        long seedmask;
        WikiLFSRTemplate(long seed, int seedbit, long bit1, long bit2, long bit3, long bit4) {
            super(seed);
            seedmask = (long) (Math.pow(2, seedbit) - 1);
            super.seed = seed & seedmask;
            lfsrmask = getLFSR4Mask(bit1, bit2, bit3, bit4);
        }

        public int next(int bits) {
            seed = seed & seedmask;
            seed = (seed >> 1) ^ ( -(seed & 1) & lfsrmask);
            seed = seed & seedmask;
            int outputmask = (int) Math.pow(2, bits) - 1;
            return (int) (seed & outputmask);
        }

        public int next() {
            seed = seed & seedmask;
            long s1 = seed >> 1;
            long s2 = -(seed & 1);
            long s3 = ( -(seed & 1) & lfsrmask);

            seed = (seed >> 1) ^ ( -(seed & 1) & lfsrmask);
            seed = seed & seedmask;
            return (int) seed;
        }

        public long nextSeed() {
            seed = seed & seedmask;
            seed = (seed >> 1) ^ ( -(seed & 1) & lfsrmask);
            seed = seed & seedmask;
            return seed;

        }

    }


    static class WikiLFSR16_4 extends AUORandom {
        WikiLFSR16_4(long seed) {
//            super(seed);
            super(seed & 0xFFFF);
        }

        public int next(int bits) {
            seed = seed & 0xFFFF;
            seed = (seed >> 1) ^ ( -(seed & 1) & 0xB400);
            seed = seed & 0xFFFF;
            int mask = (int) Math.pow(2, bits) - 1;
            return (int) (seed & mask);
        }

        public int next() {
//            long mask = getLFSR4Mask(16, 14, 13, 11);
            seed = (seed >> 1) ^ ( -(seed & 1) & 0xB400);
            return (int) (seed & 0xF);
        }

        public long nextSeed() {
            throw new UnsupportedOperationException();
        }

    }


    static class WikiLFSR15_4 extends AUORandom {
        WikiLFSR15_4(long seed) {
            super(seed);
        }

        public int next(int bits) {
            long lfsrmask = getLFSR4Mask(15, 14, 13, 11);
            seed = (seed >> 1) ^ ( -(seed & 1) & lfsrmask);
            int mask = (int) Math.pow(2, bits) - 1;
            return (int) (seed & mask);
        }

        public int next() {
            long mask = getLFSR4Mask(15, 14, 13, 11);
            seed = (seed >> 1) ^ ( -(seed & 1) & mask);
            return (int) (seed & 0xF);
        }

        public long nextSeed() {
            throw new UnsupportedOperationException();
        }
    }


    static class Maxim2LFSR extends AUORandom {
        Maxim2LFSR(long seed32, long seed31) {
            super(0);
            this.lfsr32 = seed32;
            this.lfsr31 = seed31;
        }

        Maxim2LFSR() {
            super(0);
        }


        public int next(int bits) {
            throw new UnsupportedOperationException();
        }

        public long nextSeed() {
            throw new UnsupportedOperationException();
        }

        long shift_lfsr(long lfsr, long mask) {
            long feedback = lfsr & 1;
            lfsr >>= 1;
            if (feedback == 1) {
                lfsr ^= mask;
            }
            return lfsr;
        }

        long lfsr32 = 0xABCDE, lfsr31 = 0x23456789;
        long mask32 = 0xB4BCD35C;
        long mask31 = 0x7A5BC2E3;
        long get_random() {
            lfsr32 = shift_lfsr(lfsr32, mask32);
            lfsr32 = shift_lfsr(lfsr32, mask32);
            lfsr31 = shift_lfsr(lfsr31, mask31);
            return (lfsr32 ^ lfsr31) & 0xFFFF;
        }

        public int next() {
            return (int) get_random();
        }
    }


    static class LCGRandom extends AUORandom {
        LCGRandom(long seed) {
            super(seed);
        }

        final long multiplier = 0x5DEECE66DL; //A of LCG
        final long addend = 0xBL; //C of LCG
        final long mask = (1L<<48) - 1; //M of LCG
        public int next(int bits) {
            long oldseed, nextseed;
            do {
                oldseed = seed;
                nextseed = (oldseed * multiplier + addend) & mask;
            } while (!compareAndSet(oldseed, nextseed)); //此處確保不會產生同樣的seed, 但發生的機率非常低
            return (int) (nextseed >>> (48 - bits));

        }

        public int next() {
            throw new UnsupportedOperationException();
        }

        public long nextSeed() {
            throw new UnsupportedOperationException();
        }

        private boolean compareAndSet(long expect, long update) {
            if (seed == expect) {
                seed = update;
                return true;
            } else {
                return false;
            }
        }

    }


}
