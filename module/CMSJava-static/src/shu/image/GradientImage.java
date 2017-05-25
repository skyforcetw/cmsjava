package shu.image;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * <p>Title: CMSJava-core</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class GradientImage {

//    private final static int STUFF = 0;
//    private final static boolean CONDITION = true;
    public final static BufferedImage getImage(Dimension size, int start,
                                               int end, boolean R,
                                               boolean G, boolean B,
                                               boolean inv,
                                               boolean vertical, int scale,
                                               boolean fill,
                                               BufferedImage bufferedImage) {
        int width = size.width;
        int height = size.height;
        if (bufferedImage == null || bufferedImage.getWidth() != width
            || bufferedImage.getHeight() != height) {
            bufferedImage = new BufferedImage(width,
                                              height,
                                              BufferedImage.TYPE_INT_RGB);
        }

        //==========================================================================
        // �Ѽƪ�l��
        //==========================================================================
        //�C�[�@��code,���ܪ��q
        int codeScale = 255 / (scale - 1);
        //�X��,�e���Q�����X�q
        int level = ((end - start + 1) / codeScale); // + STUFF;
        if (level == 0) {
            return getImage(size, 0, 255, true, true, true, false, false,
                            256, true, bufferedImage);
        }
        //�C�@����pixel��
        int normalStep = (width / level);
        int gripStep = (height / level);
        //�C�@��code���W�[�q
        int additive = inv ? -codeScale : codeScale;
        //code�ƭȪ�������
        int limit = inv ? start : end;
        //code����l��
        int codeStart = inv ? end : start;
        //�X��pixel�ܴ��@��code
        int step = vertical ? gripStep : normalStep;
//        boolean condition = (step == 1) ? false : CONDITION;
        //==========================================================================

        //==========================================================================
        // fill�Ѽƪ�l��
        //==========================================================================
        int remainder = width - (normalStep * level); //�ѤU�o�Ӧhpixel�S��

        int fillInLevel = (0 == remainder) ? 0 :
                          (int) Math.ceil(((double) level) / remainder);
        int fillCount = 0;
        //���ô��G�b�Ҧ����Ƕ���
        //==========================================================================

        short[][] pixels = new short[level][3]; //rgb
        short[][] coordinates = new short[level][2]; //���W��x y
        short[][] sizes = new short[level][2]; //�e ��
        short code = (short) codeStart;
        int accumulateSize = 0;

        for (int x = 0; x < level; x++) {

            pixels[x][0] = R ? code : 0;
            pixels[x][1] = G ? code : 0;
            pixels[x][2] = B ? code : 0;
//             System.out.println(x + " " + code);
            code += additive;

            coordinates[x][0] = (short) (vertical ? 0 : step * x);
            coordinates[x][1] = (short) (vertical ? step * x : 0);
            if (x == (level - 1)) {

                sizes[x][0] = (short) (vertical ? width :
                                       width - accumulateSize);
                sizes[x][1] = (short) (vertical ? height - accumulateSize :
                                       height);
//                System.out.println(x + " " + sizes[x][0] + " " + sizes[x][1]);
            } else {
                sizes[x][0] = (short) (vertical ? width : step);
                sizes[x][1] = (short) (vertical ? step : height);
            }
            if (fill && 0 != fillInLevel) {
                if (!vertical) {
                    coordinates[x][0] += fillCount;
                } else {
                    coordinates[x][1] += fillCount;
                }

                if ((x % fillInLevel) == 0) {
                    if (!vertical) {
                        sizes[x][0] += 1;
                    } else {
                        sizes[x][1] += 1;
                    }
                    fillCount++;
                }

            }

            if (!vertical) {
                accumulateSize += sizes[x][0];
            } else {
                accumulateSize += sizes[x][1];
            }

        }

        Graphics g = bufferedImage.getGraphics();
        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);

        for (int x = 0; x < level; x++) {
            short[] pixel = pixels[x];
            Color color = new Color(pixel[0], pixel[1], pixel[2]);
            //�_�I
            short[] coordinate = coordinates[x];
            //�j�p
            short[] xysize = sizes[x];
            g.setColor(color);
            g.fillRect(coordinate[0], coordinate[1], xysize[0], xysize[1]);
        }

        return bufferedImage;
    }
}
