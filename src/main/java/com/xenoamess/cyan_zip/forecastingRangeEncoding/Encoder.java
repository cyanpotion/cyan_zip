package com.xenoamess.cyan_zip.forecastingRangeEncoding;

import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.DefaultBitOutput;
import com.github.jinahya.bit.io.StreamByteOutput;

import java.io.*;
import java.util.LinkedList;

public class Encoder implements AutoCloseable {
    public static final int TRANSITION_NUM = 3;

    public long rawFileSize;
    public InputStream inputStream;

    public Transition transition;

//    public int transition_[][];
//    public int transition_b[];

    public long rangeL;
    public long rangeR;
    public static final long MaxLong = Long.parseUnsignedLong("18446744073709551615");
    public static final long ByteBufferMax = 1L << 20;
    public static final long RangeTime = 1L << 15;
    public static final int MultiTime = 1 << 4;

    public LinkedList<Integer> byteBuffer;
    public static final int OverTail = 64;

    public void clear() {
        byteBuffer = new LinkedList<>();

        rangeL = 0;
        rangeR = 255;

        transition = new Transition();

//        transition_ = new int[300][300];
//        transition_b = new int[300];

        for (int i = 0; i < TRANSITION_NUM; i++) {
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    this.transition.inc(i, j, k);
                }
            }
        }
    }

    public Encoder(InputStream inputStream, long rawFileSize) {
        this.inputStream = inputStream;
        this.rawFileSize = rawFileSize;
    }

    public void preTransition() {
        if (this.byteBuffer.size() > ByteBufferMax) {
            int byte3 = byteBuffer.getFirst();
            int byte2 = byteBuffer.get(1);
            int byte1 = byteBuffer.get(2);
            int byte0 = byteBuffer.get(3);
            byteBuffer.removeFirst();

            this.transition.sub(0, byte3, byte2, (MultiTime << (TRANSITION_NUM - 1 - 0)));
            this.transition.sub(1, byte3, byte1, (MultiTime << (TRANSITION_NUM - 1 - 1)));
            this.transition.sub(2, byte3, byte0, (MultiTime << (TRANSITION_NUM - 1 - 2)));
        }
    }


    public void calculateRange(BitOutput bitOutput, int currentByte) throws IOException {
        long nowRange;
        nowRange = rangeR - rangeL;
        int byte3 = byteBuffer.size() >= 3 ? byteBuffer.get(byteBuffer.size() - 3) : 0;
        int byte2 = byteBuffer.size() >= 2 ? byteBuffer.get(byteBuffer.size() - 2) : 0;
        int byte1 = byteBuffer.size() >= 1 ? byteBuffer.get(byteBuffer.size() - 1) : 0;
        long transitionSize = 0
                + transition.getsum(0, byte1, 255)
                + transition.getsum(1, byte2, 255)
                + transition.getsum(2, byte3, 255);

        while (Long.compareUnsigned(Long.divideUnsigned(nowRange, transitionSize), RangeTime) < 0) {
            int output = (int) (rangeL >>> 63);
            assert ((rangeL >>> 63) == (rangeR >>> 63));
            bitOutput.writeBoolean(output != 0);
            rangeL <<= 1;
            rangeR <<= 1;
//            rangeR |= 1;

            nowRange = rangeR - rangeL;
        }

        long seg = Long.divideUnsigned(nowRange, transitionSize);
        long tmpnum;
        tmpnum = 0
                + this.transition.getsum(0, byte1, currentByte - 1)
                + this.transition.getsum(1, byte2, currentByte - 1)
                + this.transition.getsum(2, byte3, currentByte - 1);

        long newRangeL = rangeL + tmpnum * seg;

        tmpnum = 0
                + this.transition.getsum(0, byte1, currentByte)
                + this.transition.getsum(1, byte2, currentByte)
                + this.transition.getsum(2, byte3, currentByte);

        long newRangeR = rangeL + tmpnum * seg;

        assert (Long.compareUnsigned(newRangeL, rangeL) > 0);
        assert (Long.compareUnsigned(newRangeR, rangeR) < 0);

        this.rangeL = newRangeL;
        this.rangeR = newRangeR;
    }

    public void updateTransition(int currentByte) {

        if (this.byteBuffer.size() >= 3) {
            int byte3 = byteBuffer.get(byteBuffer.size() - 3);
            this.transition.add(2, byte3, currentByte, (MultiTime << (TRANSITION_NUM - 1 - 2)));
//            this.transition_[byte3][currentByte] += MultiTime << (TRANSITION_NUM - 1 - 2);
//            this.transition_b[currentByte] += MultiTime << (TRANSITION_NUM - 1 - 2);
        }
        if (this.byteBuffer.size() >= 2) {
            int byte2 = byteBuffer.get(byteBuffer.size() - 2);
            this.transition.add(1, byte2, currentByte, (MultiTime << (TRANSITION_NUM - 1 - 1)));
//            this.transition_[byte2][currentByte] += MultiTime << (TRANSITION_NUM - 1 - 1);
//            this.transition_b[currentByte] += MultiTime << (TRANSITION_NUM - 1 - 1);
        }
        if (this.byteBuffer.size() >= 1) {
            int byte1 = byteBuffer.get(byteBuffer.size() - 1);
            this.transition.add(0, byte1, currentByte, (MultiTime << (TRANSITION_NUM - 1 - 0)));
//            this.transition_[byte1][currentByte] += MultiTime << (TRANSITION_NUM - 1 - 0);
//            this.transition_b[currentByte] += MultiTime << (TRANSITION_NUM - 1 - 0);
        }

        this.byteBuffer.addLast(currentByte);
    }


    public void encode(OutputStream outputStream) throws IOException {
        BitOutput bitOutput = new DefaultBitOutput<>(new StreamByteOutput(outputStream));
        bitOutput.writeLong(false, 64, rawFileSize);

        this.clear();

        int byteRead = 0;

        int currentByte;

        int overTail = OverTail;
        while (true) {
            currentByte = inputStream.read();
            if (currentByte == -1) {
                if (overTail == 0) {
                    break;
                } else {
                    overTail--;
                    currentByte = 0;
                }
//                break;
            }
            byteRead++;

//            System.out.println("byteRead : " + byteRead);
            if (byteRead % (1024 * 1024) == 0) {
                System.out.println("byteRead : " + byteRead);
            }
            this.calculateRange(bitOutput, currentByte);
            this.preTransition();
            this.updateTransition(currentByte);
        }
//        while (this.rangeL != 0) {
//            int output = (int) (rangeL >>> 63);
//            bitOutput.writeBoolean(output != 0);
//            rangeL <<= 1;
//        }
    }


    @Override
    public void close() throws Exception {
        if (inputStream != null) {
            inputStream.close();
        }
        this.clear();
    }

    public static final void Test(String filePath) throws IOException {
        File inputFile = new File(filePath);
        File outputFile = new File(filePath + ".zzz");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

        Encoder encoder = new Encoder(bis, inputFile.length());
        encoder.encode(bos);
        bis.close();
        bos.close();

        Decoder.TestDecode(filePath + ".zzz");
    }

    public static void main(String args[]) {
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

}
