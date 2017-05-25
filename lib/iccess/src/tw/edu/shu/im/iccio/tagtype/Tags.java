package tw.edu.shu.im.iccio.tagtype;

public final class Tags
{
	public static final int AToB0Tag = 0x41324230;	//A2B0, lut8Type or lut16Type or lutAtoBType
	public static final int AToB1Tag = 0x41324231;	//A2B1, lut8Type or lut16Type or lutAtoBType
	public static final int AToB2Tag = 0x41324232;	//A2B2, lut8Type or lut16Type or lutAtoBType
	public static final int blueMatrixColumnTag = 0x6258595A;	//bXYZ, XYZType
	public static final int blueTRCTag = 0x62545243;	//bTRC, curveType or parametricCurveType
	public static final int BToA0Tag = 0x42324130;	//B2A0, lut8Type or lut16Type or lutBtoAType
	public static final int BToA1Tag = 0x42324131;	//B2A1, lut8Type or lut16Type or lutBtoAType
	public static final int BToA2Tag = 0x42324132;	//B2A2, lut8Type or lut16Type or lutBtoAType
	public static final int calibrationDateTimeTag = 0x63616C74;	//calt, dateTimeType
	public static final int charTargetTag = 0x74617267;	//targ, textType
	public static final int chromaticAdaptationTag = 0x63686164;	//chad, s15Fixed16ArrayType
	public static final int chromaticityTag = 0x6368726D;	//chrm, chromaticityType
	public static final int colorantOrderTag = 0x636C726F;	//clro, colorantOrderType
	public static final int colorantTableTag = 0x636C7274;	//clrt, colorantTableType
	public static final int colorantTableOutTag = 0x636C6F74; //clot, colorantTableType
	public static final int copyrightTag = 0x63707274;	//cprt, multiLocalizedUnicodeType
	public static final int deviceMfgDescTag = 0x646D6E64;	//dmnd, multiLocalizedUnicodeType
	public static final int deviceModelDescTag = 0x646D6464;	//dmdd, multiLocalizedUnicodeType
	public static final int gamutTag = 0x67616D74;	//gamt, lut8Type or lut16Type or lutBtoAType
	public static final int grayTRCTag = 0x6B545243;	//kTRC, curveType or parametricCurveType
	public static final int greenMatrixColumnTag = 0x6758595A;	//gXYZ, XYZType
	public static final int greenTRCTag = 0x67545243;	//gTRC, curveType or parametricCurveType
	public static final int luminanceTag = 0x6C756D69;	//lumi, XYZType
	public static final int measurementTag = 0x6D656173;	//meas, measurementType
	public static final int mediaBlackPointTag = 0x626B7074;	//bkpt, XYZType
	public static final int mediaWhitePointTag = 0x77747074;	//wtpt, XYZType
	public static final int namedColor2Tag = 0x6E636C32;	//ncl2, namedColor2Type
	public static final int outputResponseTag = 0x72657370;	//resp,responseCurveSet16Type
	public static final int preview0Tag = 0x70726530;	//pre0, lut8Type or lut16Type or lutBtoAType
	public static final int preview1Tag = 0x70726531;	//pre1, lut8Type or lut16Type or lutBtoAType
	public static final int preview2Tag = 0x70726532;	//pre2, lut8Type or lut16Type or lutBtoAType
	public static final int profileDescriptionTag = 0x64657363;	//desc, multiLocalizedUnicodeType
	public static final int profileSequenceDescTag = 0x70736571;	//pseq, profileSequenceDescType
	public static final int redMatrixColumnTag = 0x7258595A;	//rXYZ, XYZType
	public static final int redTRCTag = 0x72545243;	//rTRC, curveType or parametricCurveType
	public static final int technologyTag = 0x74656368;	//tech, signatureType, see table on page 35 of spec v4.2
	public static final int viewingCondDescTag = 0x76756564;	//vued, multiLocalizedUnicodeType
	public static final int viewingConditionsTag = 0x76696577;	//view, viewingConditionsType
}

