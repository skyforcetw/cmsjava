package auo.cms.frc.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import auo.cms.frc.FRCPattern;
import shu.math.array.IntArray;

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
public class FRC3_16PatternProducer2 {
    public FRC3_16PatternProducer2() {

    }


    /**
     * ���1/8 (2/16), �̾�check���ƭ�{2,1}��{1,2},
     * �M�w+1�����, �u�|�[�_�Ʀ�, �Ϊ̰��Ʀ�
     * @param orgFrame boolean[][]
     * @param okPos boolean[][]
     * @param check int[]
     * @return LinkedList
     */
    static LinkedList<boolean[][]> ABFramePicker(boolean[][] orgFrame, boolean[][] okPos,
                                                 int[] check
            ) {

        int height = okPos.length;
        int width = okPos[0].length;

        int okcount = 0;
        for (int w = 0; w < width; w++) {
            if (okPos[0][w]) {
                okcount++;
            }
        }
        byte[][] okPosIndex = new byte[height][okcount];
        for (int h = 0; h < height; h++) {
            int index = 0;
            for (byte w = 0; w < width; w++) {
                if (okPos[h][w]) {
                    okPosIndex[h][index++] = w;
                }
            }
        }

        int starth = check[0] < check[1] ? 0 : 1;
        LinkedList<boolean[][]> result = new LinkedList<boolean[][]>();
        byte[] checkArray = new byte[width];
        boolean doSkip = false;

        for (int L1 = 0; L1 < okcount; L1++) {
            int L1h = starth;
            int L1pos = okPosIndex[L1h][L1];

            if (doSkip && ((L1h > 0 && orgFrame[L1h - 1][L1pos]) ||
                           (L1pos > 0 && orgFrame[L1h][L1pos - 1]) ||
                           (L1pos < width - 1 && orgFrame[L1h][L1pos + 1]) ||
                           (orgFrame[L1h + 1][L1pos]))) {
                continue;
            }

            for (int L2 = 0; L2 < okcount; L2++) {
                int L2h = starth + 2;
                int L2pos = okPosIndex[L2h][L2];

                if (doSkip && (L1pos == L2pos ||
                               (L2h > 0 && orgFrame[L2h - 1][L2pos]) ||
                               (L2pos > 0 && orgFrame[L2h][L2pos - 1]) ||
                               (L2pos < (width - 1) && orgFrame[L2h][L2pos + 1]) ||
                               (orgFrame[L2h + 1][L2pos]))) {
                    continue;
                }

                for (int L3 = 0; L3 < okcount; L3++) {
                    int L3h = starth + 4;
                    int L3pos = okPosIndex[L3h][L3];

                    if (doSkip && ((L1pos == L3pos || L2pos == L3pos) ||
                                   (L3h > 0 && orgFrame[L3h - 1][L3pos]) ||
                                   (L3pos > 0 && orgFrame[L3h][L3pos - 1]) ||
                                   (L3pos < width - 1 && orgFrame[L3h][L3pos + 1]) ||
                                   (orgFrame[L3h + 1][L3pos]))) {
                        continue;
                    }

                    for (int L4 = 0; L4 < okcount; L4++) {
                        int L4h = starth + 6;
                        int L4pos = okPosIndex[L4h][L4];

                        if (doSkip && ((L1pos == L4pos || L2pos == L4pos || L3pos == L4pos) ||
                                       (L4h > 0 && orgFrame[L4h - 1][L4pos]) ||
                                       (L4pos > 0 && orgFrame[L4h][L4pos - 1]) ||
                                       (L4pos < width - 1 && orgFrame[L4h][L4pos + 1]) ||
                                       (L4h < height - 1 && orgFrame[L4h + 1][L4pos]))) {
                            continue;
                        }

                        //==========================================================================
                        // �ˬd���C��+1�ƶq
                        //==========================================================================
                        for (int x = 0; x < width; x++) {
                            checkArray[x] = 1;
                        }
                        checkArray[L1pos]++;
                        checkArray[L2pos]++;
                        checkArray[L3pos]++;
                        checkArray[L4pos]++;

                        boolean ok = true;
                        for (int x = 0; x < width / 2; x++) {
                            if (checkArray[x * 2] != check[0] || checkArray[x * 2 + 1] != check[1]) {
                                ok = false;
                                break;
                            }
                        }
                        if (!ok) {
                            continue;
                        }
                        //==========================================================================


                        boolean[][] frc = FRCUtil.copy(orgFrame);
                        frc[L1h][L1pos] = true;
                        frc[L2h][L2pos] = true;
                        frc[L3h][L3pos] = true;
                        frc[L4h][L4pos] = true;

                        result.add(frc);
                    }
                }
            }
        }

        return result;
    }

    enum BaseOn {
        FRC1_8, FRC1_16
    }


    enum Method {
        AB, Eight16
    }


    public static void main(String[] args) throws IOException {
        //�귽�Ҧb���ؿ�
        String dir = "FRC/FRC 3-16_2/";
        BaseOn baseon = BaseOn.FRC1_8; //��ĳ��1/8�h��3/16. �]��1/16��pattern�ܤ��W�h, �y�X�Ӫ�3/16�۹�]����W�h��
        //==============================================================================================================
        // ���ƻ�覡�h+1
        // AB : �C��frame +1�ƶq�ۦP. �P�@��frame, �_���椣�P�ƶq+1, �F��n/16���ѪR��
        // Eight16 : �C��frame +1�ƶq���P. �H1/8�M2/8�ﴡ���Pframe, �����X3/16���ĪG
        //
        // AB�ĪG����, ����ĳ�ϥ�
        //==============================================================================================================
        Method method = Method.Eight16;
//        Method method = Method.AB;
        //==============================================================================================================

        //==============================================================================================================
        boolean choiceFirstFor16 = false; //�Ĥ@��frame�O�_�N�[��16
        boolean plusAdjoin = true; //+1�O�_�n��b�۾F��frame. �۾F: 0++0, ���۾F0+0+
        ArtifactsAnalyzer.InversionMode inversion = ArtifactsAnalyzer.InversionMode._1V1H;
        //==============================================================================================================

        FRCPattern frcPattern = null;
        switch (baseon) {
        case FRC1_8:
            frcPattern = new FRCPattern(dir + "1_8frc-auo.txt", 0);
            break;
        case FRC1_16:
            frcPattern = new FRCPattern(dir + "1_16frc.txt", 0);
            break;
        }
        System.out.println(frcPattern);

        //==============================================================================================================
        // ���ok pos
        //==============================================================================================================
        boolean[][][] ok4frame = FRCUtil.getOkPosition(frcPattern);
        System.out.println("ok frame");
        for (boolean[][] o : ok4frame) {
            System.out.println(FRCUtil.toString(o));
        }
        //==============================================================================================================

        boolean[][][] frcpattern = frcPattern.pattern[0];
        int framecount = frcpattern.length;

        LinkedList<Frame> frameList[] = new LinkedList[4];

        String pickfilename = dir + "pick" + (choiceFirstFor16 ? "1" : "2") +
                              (plusAdjoin ? "_" : "") + ".obj";
        LinkedList<FRCPattern>
                frcList = null;

        //==============================================================================================================
        // ��X�C��frame���Ҧ�+1�զX
        //==============================================================================================================
        if (new File(pickfilename).exists()) {
            //�Y�w�g��pick�X�Ӫ����G, ����load�ɮ�
            try {
                ObjectInputStream f = new ObjectInputStream(new BufferedInputStream(new
                        FileInputStream(pickfilename)));
                frcList = (LinkedList<FRCPattern>) f.readObject();
                f.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            }
        } else {
            //�_�h���s�y

            if (method == Method.AB) {
                //AB�k�ĪG���n, ���L����
//            int[][] check = { {2, 1}, {1, 2}
//            };
                int[][] check = { {1, 2}, {2, 1}
                };
                for (int f = 0; f < framecount; f++) {
                    LinkedList<boolean[][]>
                            pickFrame = ABFramePicker(frcpattern[f], ok4frame[f], check[f % 2]);
                    frameList[f] = new LinkedList<Frame>();
                    for (boolean[][] pick : pickFrame) {
                        Frame frame = new Frame(pick);
                        frameList[f].add(frame);
                    }
                }
            } else if (method == Method.Eight16) {
                //8+16���˨ϥ�

                for (int f = 0; f < framecount; f++) {
                    frameList[f] = new LinkedList<Frame>();
                    int index = choiceFirstFor16 ? f : f + 1;
                    if (plusAdjoin ? (f == 1 || f == 2) : index % 2 == 0) {
                        LinkedList<boolean[][]>
                                pickFrame = FRCUtil.eight16FramePicker(frcpattern[f], ok4frame[f]);
                        for (boolean[][] pick : pickFrame) {
                            Frame frame = new Frame(pick);
                            frameList[f].add(frame);
                        }

                    } else {
                        Frame frame = new Frame(frcpattern[f]);
                        frameList[f].add(frame);
                    }
                }
            }

            try {
                //�y�n�N�x�s���ɮ�, �H�ƥH��ݭn
                boolean checkingOverlapping = true;
                boolean skipSameFrame = false;
                int f1_start = 0;
                int pickmethod = 2;
                frcList = FRC3_16PatternProducer.frcPicker3(frameList, f1_start, pickmethod, dir, checkingOverlapping,
                        skipSameFrame, inversion);
                System.out.println(frcList.size());

                ObjectOutputStream f = new ObjectOutputStream(new BufferedOutputStream(new
                        FileOutputStream(pickfilename)));
                f.writeObject(frcList);
                f.flush();
                f.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

        }
        //==============================================================================================================

        //==============================================================================================================
        // �W�z�Ҧ��զX��, �̾�rule�L�o�X�X�z��FRC Pattern
        //==============================================================================================================
        LinkedList<FRCPattern> filterFRCList = new LinkedList<FRCPattern>();
        int index = 0;
        for (FRCPattern frc : frcList) {
            //�p��rule�һݭn����T
            frc.caculateInfo();
            int[] Lcount = frc.Lcount;
            int[] hcount = frc.twohcount;
            int[] vcount = frc.twovcount;
            int[][] greenPixel = frc.greenPixel;

            boolean ok = true;
            //====================================================================
            //H V L�O�_�ŦX�W�h
            //====================================================================
            for (int f = 0; f < 4; f++) {
                if (!(hcount[f] >= vcount[f] && vcount[f] >= Lcount[f]) || // H>V>L
                    frc.maxSlash > 4 || Lcount[f] > 0
                        /*|| vcount[f] != 0*/) {
                    ok = false;
                    break;
                }
            }
            //====================================================================

            int size = greenPixel.length;
            //====================================================================
            //green�O�_�ŦX�W�h
            //====================================================================
            for (int x = 0; x < size; x++) {
                for (int c = 0; c < 3; c++) {
                    if (0 == greenPixel[x][c]) {
                        ok = false;
                        break;
                    }
                }
            }
            //====================================================================

            byte[][] balancedSum = frc.balancedSum;
            int height = balancedSum.length;
            int width = balancedSum[0].length;

            //====================================================================
            //�F�����O0�����p�N�h��
            //====================================================================
            for (int h = 0; h < height; h++) {
                for (int w = 0; w < width; w++) {
                    if (balancedSum[h][w] == 0) {
                        if ((h > 0 && balancedSum[h - 1][w] == 0) ||
                            (w > 0 && balancedSum[h][w - 1] == 0) ||
                            (w < width - 1 && balancedSum[h][w + 1] == 0) ||
                            (h < height - 1 && balancedSum[h + 1][w] == 0)) {
                            ok = false;
                            break;
                        }
                    }
                }
            }
            //====================================================================

            //��u����>=4�N�h��
            if (frc.maxHLine >= 4) {
                ok = false;
            }

            //====================================================================
            //�p��F�����ƶq
            //====================================================================
            int[] posAdjoin = CheckTool.getAdjoinCount(frc, false);
            int[] neaAdjoin = CheckTool.getAdjoinCount(frc, true);
            int maxAdjoinPolarity = 0;
            for (int x = posAdjoin.length - 1; x > 0; x--) {
                if (posAdjoin[x] != 0 || neaAdjoin[x] != 0) {
                    maxAdjoinPolarity = x;
                    break;
                }
            }
            if (maxAdjoinPolarity > 4) {
                ok = false;
            }
            //====================================================================

            //====================================================================
            // �p��۾FG+1���ƶq(�קK����)
            //====================================================================
            int[][] twovGcount = CheckTool.getTwovGcount(frc);
            int[] twovGsimplify = new int[twovGcount.length];
            for (int x = 0; x < twovGcount.length; x++) {
                twovGsimplify[x] = IntArray.sum(twovGcount[x]);
            }
            //twovG�N�ȬO��twovGcount�����[�`�_�ӦӤw
            int twovG = IntArray.sum(twovGsimplify);
            if (twovG > 12) { //twov����W�L12
                ok = false;
            }
            //====================================================================

            //�즹�w�g�]���Ҧ�rule, �ŦX��檺Pattern�x�s�_��

            if (ok) {
                filterFRCList.add(frc);
                System.out.println((index++) + " " + "maxAdjoinPolarity: " + maxAdjoinPolarity);

                System.out.println(frc);
                boolean noinfo = true;

                if (noinfo) {
                    frc.artifacts = null;
                    frc.balancedSum = null;
                    frc.greenPixel = null;
                    frc.twohcount = null;
                    frc.twovcount = null;
                    frc.Lcount = null;
                    frc.maxHLine = 0;
                    frc.maxSlash = 0;
                }
                System.out.println(frc);

            }
        }
        //==============================================================================================================

        System.out.println("final filter result: " + filterFRCList.size() + " / " + frcList.size());
    }


    static void frcPicker(LinkedList < boolean[][] > [] frameresult) {

    }
}
