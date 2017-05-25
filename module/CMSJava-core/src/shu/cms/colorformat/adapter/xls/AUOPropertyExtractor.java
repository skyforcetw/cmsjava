package shu.cms.colorformat.adapter.xls;

import shu.io.files.ExcelFile;
import jxl.read.biff.BiffException;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import shu.cms.colorspace.depend.RGBBase;
import shu.cms.colorspace.independ.CIExyY;
import java.util.StringTokenizer;

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
public class AUOPropertyExtractor {
    private ExcelFile xls;
    private Map<String, String> propertyMap = new HashMap();
    public AUOPropertyExtractor(ExcelFile xls) {
        this.xls = xls;
        init(xls);
    }

    public AUOPropertyExtractor(AUOCPTableXLSAdapter adapter) {
        this.xls = adapter.xls;
        init(this.xls);
    }

    private void init(ExcelFile xls) {
        xls.selectSheet("Properties");
        int rows = xls.getRows();
        for (int x = 0; x < rows; x++) {
            String key = xls.getCellAsString(0, x);
            String value = xls.getCellAsString(1, x);
            propertyMap.put(key, value);
        }
    }

    public String getProperty(String key) {
        return propertyMap.get(key);
    }

    public CIExyY getNativePrimaryColor(RGBBase.Channel ch) {
        String key;
        switch (ch) {
        case R:
            key = "native primary R";
            break;
        case G:
            key = "native primary G";
            break;
        case B:
            key = "native primary B";
            break;
        default:
            throw new IllegalArgumentException("");
        }
        String value = getProperty(key);
        StringTokenizer tokenizer = new StringTokenizer(value, "[],");
        if (3 == tokenizer.countTokens()) {
            String x = tokenizer.nextToken();
            String y = tokenizer.nextToken();
            String Y = tokenizer.nextToken();
            return new CIExyY(Double.parseDouble(x), Double.parseDouble(y),
                              Double.parseDouble(Y));
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws BiffException, IOException {
        AUOCPTableXLSAdapter xls = new AUOCPTableXLSAdapter("debug.xls");
        AUOPropertyExtractor extractor = new AUOPropertyExtractor(xls.xls);
        System.out.println(extractor.getNativePrimaryColor(RGBBase.Channel.R));
        System.out.println(extractor.getNativePrimaryColor(RGBBase.Channel.G));
        System.out.println(extractor.getNativePrimaryColor(RGBBase.Channel.B));
    }
}
