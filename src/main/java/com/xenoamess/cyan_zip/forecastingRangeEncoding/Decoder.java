package com.xenoamess.cyan_zip.forecastingRangeEncoding;

import com.github.jinahya.bit.io.BitInput;
import com.github.jinahya.bit.io.DefaultBitInput;
import com.github.jinahya.bit.io.StreamByteInput;

import java.io.*;
import java.util.LinkedList;

public class Decoder implements AutoCloseable {
    public long rawFileSize;

    public BitInput bitInput;
    public Transition transition;

    public long rangeL;
    public long rangeR;
    public long rangeN;

    public LinkedList<Integer> byteBuffer;

    boolean inputOver;
    int bitRead = 0;


    public int readBit() {
        bitRead++;
        if (bitRead % (8 * 1024 * 1024) == 0) {
            System.out.println("bitRead : " + bitRead / 8);
        }

        int res;
        try {
            res = this.bitInput.readBoolean() ? 1 : 0;
        } catch (IOException e) {
//            e.printStackTrace();
            res = 0;
            inputOver = true;
        }
        return res;
    }


    public void clear() {
        this.inputOver = false;

        byteBuffer = new LinkedList<>();

        rangeL = 0;
        rangeR = 255;
        rangeN = 0;
        for (int i = 0; i < 64; i++) {
            rangeN <<= 1;
            rangeN |= this.readBit();
        }

        transition = new Transition();

        for (int i = 0; i < Encoder.TRANSITION_NUM; i++) {
            for (int j = 0; j < 256; j++) {
                for (int k = 0; k < 256; k++) {
                    this.transition.inc(i, j, k);
                }
            }
        }
    }

    public Decoder(InputStream inputStream) {
        this.bitInput = new DefaultBitInput(new StreamByteInput(inputStream));
    }

    public void preTransition() {
        if (this.byteBuffer.size() > Encoder.ByteBufferMax) {
            int byte3 = byteBuffer.getFirst();
            int byte2 = byteBuffer.get(1);
            int byte1 = byteBuffer.get(2);
            int byte0 = byteBuffer.get(3);
            byteBuffer.removeFirst();

            this.transition.sub(0, byte3, byte2, (Encoder.MultiTime << (Encoder.TRANSITION_NUM - 1 - 0)));
            this.transition.sub(1, byte3, byte1, (Encoder.MultiTime << (Encoder.TRANSITION_NUM - 1 - 1)));
            this.transition.sub(2, byte3, byte0, (Encoder.MultiTime << (Encoder.TRANSITION_NUM - 1 - 2)));
        }
    }


    public int calculateByte() throws IOException {
        long nowRange;
        nowRange = rangeR - rangeL;
        int byte3 = byteBuffer.size() >= 3 ? byteBuffer.get(byteBuffer.size() - 3) : 0;
        int byte2 = byteBuffer.size() >= 2 ? byteBuffer.get(byteBuffer.size() - 2) : 0;
        int byte1 = byteBuffer.size() >= 1 ? byteBuffer.get(byteBuffer.size() - 1) : 0;
        long transitionSize = 0
                + transition.getsum(0, byte1, 255)
                + transition.getsum(1, byte2, 255)
                + transition.getsum(2, byte3, 255);


        while (Long.compareUnsigned(Long.divideUnsigned(nowRange, transitionSize), Encoder.RangeTime) < 0) {
            assert (rangeL != rangeR);
            rangeL <<= 1;
            rangeR <<= 1;
            rangeN <<= 1;
//            rangeR |= 1L;

            rangeN |= this.readBit();
            nowRange = rangeR - rangeL;
        }

        long seg = Long.divideUnsigned(nowRange, transitionSize);
        long tmpnum;


        int l = 0;
        int r = 256;
        int mid;
        while (true) {
            if (r - l == 1) {
                break;
            }
            mid = ((l + r) >>> 1);
            tmpnum = 0
                    + this.transition.getsum(0, byte1, mid - 1)
                    + this.transition.getsum(1, byte2, mid - 1)
                    + this.transition.getsum(2, byte3, mid - 1);
            long tmpRangeL = rangeL + tmpnum * seg;
            if (Long.compareUnsigned(tmpRangeL, rangeN) > 0) {
                r = mid;
            } else {
                l = mid;
            }
        }
        mid = l;


        tmpnum = 0
                + this.transition.getsum(0, byte1, mid - 1)
                + this.transition.getsum(1, byte2, mid - 1)
                + this.transition.getsum(2, byte3, mid - 1);

        long newRangeL = rangeL + tmpnum * seg;

        tmpnum = 0
                + this.transition.getsum(0, byte1, mid)
                + this.transition.getsum(1, byte2, mid)
                + this.transition.getsum(2, byte3, mid);

        long newRangeR = rangeL + tmpnum * seg;

        assert (Long.compareUnsigned(newRangeL, rangeL) > 0);
        assert (Long.compareUnsigned(newRangeR, rangeR) < 0);

        this.rangeL = newRangeL;
        this.rangeR = newRangeR;
        return mid;
    }

    public void updateTransition(int currentByte) {

        if (this.byteBuffer.size() >= 3) {
            int byte3 = byteBuffer.get(byteBuffer.size() - 3);
            this.transition.add(2, byte3, currentByte, (Encoder.MultiTime << (Encoder.TRANSITION_NUM - 1 - 2)));

//            this.transition_[byte3][currentByte] += MultiTime << (TRANSITION_NUM - 1 - 2);
//            this.transition_b[currentByte] += MultiTime << (TRANSITION_NUM - 1 - 2);
        }
        if (this.byteBuffer.size() >= 2) {
            int byte2 = byteBuffer.get(byteBuffer.size() - 2);
            this.transition.add(1, byte2, currentByte, (Encoder.MultiTime << (Encoder.TRANSITION_NUM - 1 - 1)));
//            this.transition_[byte2][currentByte] += MultiTime << (TRANSITION_NUM - 1 - 1);
//            this.transition_b[currentByte] += MultiTime << (TRANSITION_NUM - 1 - 1);
        }
        if (this.byteBuffer.size() >= 1) {
            int byte1 = byteBuffer.get(byteBuffer.size() - 1);
            this.transition.add(0, byte1, currentByte, (Encoder.MultiTime << (Encoder.TRANSITION_NUM - 1 - 0)));
//            this.transition_[byte1][currentByte] += MultiTime << (TRANSITION_NUM - 1 - 0);
//            this.transition_b[currentByte] += MultiTime << (TRANSITION_NUM - 1 - 0);
        }

        this.byteBuffer.addLast(currentByte);
    }

    public void decode(OutputStream outputStream) throws IOException {
        rawFileSize = bitInput.readLong(false, 64);
        this.clear();
        long outputFileSize = 0;
        int currentByte;
        while (true) {
            currentByte = this.calculateByte();
            this.preTransition();
            this.updateTransition(currentByte);

            if (outputFileSize < rawFileSize) {
                outputStream.write(currentByte);
            }
            outputFileSize++;
            if (outputFileSize >= rawFileSize + Encoder.OverTail) {
                break;
            }
        }
        outputStream.close();
    }


    @Override
    public void close() throws Exception {
//        if (bitInput != null) {
//            bitInput.();
//        }
        this.clear();
    }

    public static final void TestDecode(String filePath) throws IOException {
        File inputFile = new File(filePath);
        File outputFile = new File(filePath + ".un");
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(inputFile));
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFile));

        Decoder decoder = new Decoder(bis);
        decoder.decode(bos);
        bis.close();
        bos.close();
    }

    public static void main(String args[]) {
        try {
            TestDecode("D:\\workspace\\cyan_zip\\tmp\\a.txt.zzz");
        } catch (IOException e) {
            e.printStackTrace();
        }
//        try {
//            TestDecode("D:\\workspace\\cyan_zip\\tmp\\480P_600K_136195722.mp4");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            TestDecode("D:\\workspace\\cyan_zip\\tmp\\1.pdb");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            TestDecode("D:\\workspace\\cyan_zip\\tmp\\1.doc");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//            File inputFile = new File("D:\\workspace\\cyan_zip\\tmp\\480P_600K_136195722.mp4");
//            File outputFile = new File("D:\\workspace\\cyan_zip\\tmp\\480P_600K_136195722.z");
//            File inputFile = new File("D:\\workspace\\cyan_zip\\tmp\\1115277.epub");
//            File outputFile = new File("D:\\workspace\\cyan_zip\\tmp\\1115277.z");
//            File inputFile = new File("D:\\workspace\\cyan_zip\\tmp\\a.txt");
//            File outputFile = new File("D:\\workspace\\cyan_zip\\tmp\\a.z");

    }

}

