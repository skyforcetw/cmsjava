package auo.cms.frc.impl;

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
public class FRC2_8PatternProducer {


    public static void main(String[] args) {
        FRCPatternProducer producer = new FRCPatternProducer();

        /**
         * 2/8是不用base pattern, 直接用rule去造出來的, 所以不用另外去讀FRC Pattern
         */
        ArtifactsAnalyzer.InversionMode inversion = ArtifactsAnalyzer.InversionMode._1V1H;
        boolean checkGreen = false;
        boolean check1FrameSlash = true;
        int _1FrameSlashLength = 4;
        boolean check4FrameSlash = true;
        int _4FrameSlashLength = 5;

        producer.getCheckedFRCPatternLv2_2(inversion, checkGreen, check1FrameSlash,
                                           _1FrameSlashLength, check4FrameSlash,
                                           _4FrameSlashLength);
    }
}
