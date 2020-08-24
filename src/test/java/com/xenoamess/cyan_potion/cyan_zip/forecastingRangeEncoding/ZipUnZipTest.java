package com.xenoamess.cyan_potion.cyan_zip.forecastingRangeEncoding;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ZipUnZipTest {

    @Test
    public void test() throws URISyntaxException, IOException {
        String[] fileNames = {
                "/SourceHanSansK-Normal.7z",
                "/citylots.zip",
                "/testPom.xml",
        };
        for (String au : fileNames) {
            test(this.getClass().getResource(au).toURI().getPath());
        }
    }

    public static void testEncode(String filePath) throws IOException {
        File inputFile = new File(filePath);
        File outputFile = new File(filePath + ".zzz");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

        ForcastingRangeEncodingEncoder encoder = new ForcastingRangeEncodingEncoder(inputFile.length(), bos);
        encoder.encodeAll(bis);
        bis.close();
        bos.close();
    }

    public static void testDecode(String filePath) throws IOException {
        File inputFile = new File(filePath);
        File outputFile = new File(filePath + ".un");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

        ForcastingRangeEncodingDecoder decoder = new ForcastingRangeEncodingDecoder(bis);
        decoder.decodeAll(bos);
        bis.close();
        bos.close();
    }


    public static void testEncode2(String filePath) throws IOException {
        File inputFile = new File(filePath);
        File outputFile = new File(filePath + ".zzz");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

        ForcastingRangeEncodingOutputStream forcastingRangeEncodingOutputStream =
                new ForcastingRangeEncodingOutputStream(inputFile.length(), bos);

        while (true) {
            int b = bis.read();
            if (b == -1)
                break;
            forcastingRangeEncodingOutputStream.write(b);
        }

        bis.close();
        bos.close();
    }

    public static void testDecode2(String filePath) throws IOException {
        File inputFile = new File(filePath);
        File outputFile = new File(filePath + ".un");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

        ForcastingRangeEncodingInputStream forcastingRangeEncodingInputStream =
                new ForcastingRangeEncodingInputStream(bis);

        while (true) {
            int b = forcastingRangeEncodingInputStream.read();
            if (b == -1)
                break;
            bos.write(b);
        }

        bis.close();
        bos.close();
    }

    public static void test(String filePath) throws IOException {
        testEncode(filePath);
        testDecode(filePath + ".zzz");
        testEncode2(filePath + ".zzz.un");
        testDecode2(filePath + ".zzz.un.zzz");

        testEqual(filePath, filePath + ".zzz.un");
        testEqual(filePath, filePath + ".zzz.un.zzz.un");

        testEqual(filePath + ".zzz", filePath + ".zzz.un.zzz");
    }

    public static void testEqual(String a, String b) throws IOException {
        assertTrue(FileUtils.contentEquals(new File(a), new File(b)),
                "No, files not equal : <a>" + a + "> , <b>" + b + ">"
        );
    }
}
