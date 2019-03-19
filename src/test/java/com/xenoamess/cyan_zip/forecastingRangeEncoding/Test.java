package com.xenoamess.cyan_zip.forecastingRangeEncoding;

import java.io.*;

public class Test {
    public static void main(String args[]) throws Exception {
//        File testFile = new File("tmp/test.txt");
//        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(testFile));
//
//        BitOutput bitOutput = new DefaultBitOutput<>(new StreamByteOutput(bos));
//        for (int i = 0; i < 160000000; i++) {
//            bitOutput.writeBoolean(false);
//        }
//        for (int i = 0; i < 160000000; i++) {
//            bitOutput.writeBoolean(true);
//        }
//        bos.close();

//        try {
//            com.xenoamess.cyan_zip.forecastingRangeEncoding.Test("D:\\workspace\\cyan_zip\\tmp\\1.doc");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        try {
//            com.xenoamess.cyan_zip.forecastingRangeEncoding.Test("D:\\workspace\\cyan_zip\\tmp\\a.txt");
//            com.xenoamess.cyan_zip.forecastingRangeEncoding.Test("D:\\workspace\\cyan_zip\\tmp\\1.doc");
//            com.xenoamess.cyan_zip.forecastingRangeEncoding.Test("D:\\workspace\\cyan_zip\\tmp\\1.pdb");
//            com.xenoamess.cyan_zip.forecastingRangeEncoding.Test("D:\\workspace\\cyan_zip\\tmp\\test.txt");
            Test("D:\\workspace\\cyan_zip\\tmp\\OpenJDK11U-jdk_x64_windows_hotspot_11.0.2_9.zip");
//            com.xenoamess.cyan_zip.forecastingRangeEncoding.Test("D:\\workspace\\cyan_zip\\tmp\\SourceHanSansK-Normal.7z");

            //            com.xenoamess.cyan_zip.forecastingRangeEncoding.Test("D:\\workspace\\cyan_zip\\tmp\\480P_600K_136195722.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }


//        try {
//            com.xenoamess.cyan_zip.forecastingRangeEncoding.Test("D:\\workspace\\cyan_zip\\tmp\\1.pdb");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//


//            File inputFile = new File("D:\\workspace\\cyan_zip\\tmp\\480P_600K_136195722.mp4");
//            File outputFile = new File("D:\\workspace\\cyan_zip\\tmp\\480P_600K_136195722.z");
//            File inputFile = new File("D:\\workspace\\cyan_zip\\tmp\\1115277.epub");
//            File outputFile = new File("D:\\workspace\\cyan_zip\\tmp\\1115277.z");
//            File inputFile = new File("D:\\workspace\\cyan_zip\\tmp\\a.txt");
//            File outputFile = new File("D:\\workspace\\cyan_zip\\tmp\\a.z");


    }

    public static final void TestEncode(String filePath) throws IOException {
        File inputFile = new File(filePath);
        File outputFile = new File(filePath + ".zzz");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

        ForcastingRangeEncodingEncoder encoder = new ForcastingRangeEncodingEncoder(inputFile.length(), bos);
        encoder.encodeAll(bis);
        bis.close();
        bos.close();

//        TestDecode(filePath + ".zzz");
    }

    public static final void TestDecode(String filePath) throws IOException {
        File inputFile = new File(filePath);
        File outputFile = new File(filePath + ".un");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

        ForcastingRangeEncodingDecoder decoder = new ForcastingRangeEncodingDecoder(bis);
        decoder.decodeAll(bos);
        bis.close();
        bos.close();
    }


    public static final void TestEncode2(String filePath) throws IOException {
        File inputFile = new File(filePath);
        File outputFile = new File(filePath + ".zzz");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

        ForcastingRangeEncodingOutputStream forcastingRangeEncodingOutputStream = new ForcastingRangeEncodingOutputStream(inputFile.length(), bos);

        while (true) {
            int b = bis.read();
            if (b == -1) break;
            forcastingRangeEncodingOutputStream.write(b);
        }

        bis.close();
        bos.close();

//        TestDecode2(filePath + ".zzz");
    }

    public static final void TestDecode2(String filePath) throws IOException {
        File inputFile = new File(filePath);
        File outputFile = new File(filePath + ".un");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

        ForcastingRangeEncodingInputStream forcastingRangeEncodingInputStream = new ForcastingRangeEncodingInputStream(bis);

        while (true) {
            int b = forcastingRangeEncodingInputStream.read();
            if (b == -1) break;
            bos.write(b);
        }

        bis.close();
        bos.close();
    }

    public static final void Test(String filePath) throws IOException {
        TestEncode(filePath);
        TestDecode(filePath + ".zzz");
        TestEncode2(filePath + ".zzz.un");
        TestDecode2(filePath + ".zzz.un.zzz");
    }
}
