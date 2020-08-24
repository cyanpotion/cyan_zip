package com.xenoamess.cyan_potion.cyan_zip.forecastingRangeEncoding;

import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.DefaultBitOutput;
import com.github.jinahya.bit.io.StreamByteOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

public class ForcastingRangeEncodingEncoder {
    protected static final int TRANSITION_NUM = 3;

    protected long rawFileSize;
    protected OutputStream outputStream;
    protected BitOutput bitOutput = null;

    protected Transition transition;

//    public int transition_[][];
//    public int transition_b[];

    protected long rangeL;
    protected long rangeR;
    protected static final long MaxLong = Long.parseUnsignedLong("18446744073709551615");
    protected static final long ByteBufferMax = 1L << 20;
    protected static final long RangeTime = 1L << 15;
    protected static final int MultiTime = 1 << 4;

    protected LinkedList<Integer> byteBuffer;
    protected static final int OverTail = 64;

    protected int byteRead;
    protected int overTail;

    public void clear() {
        this.byteBuffer = new LinkedList<>();
//        this.bitOutput = null;

        this.byteRead = 0;
        this.overTail = OverTail;

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

    public ForcastingRangeEncodingEncoder(long rawFileSize, OutputStream outputStream) {
        this.outputStream = outputStream;
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
                + transition.getSum(0, byte1, 255)
                + transition.getSum(1, byte2, 255)
                + transition.getSum(2, byte3, 255);

        while (Long.compareUnsigned(Long.divideUnsigned(nowRange, transitionSize), RangeTime) < 0) {
            int output = (int) (rangeL >>> 63);
            bitOutput.writeBoolean(output != 0);
            rangeL <<= 1;
            rangeR <<= 1;
//            rangeR |= 1;

            nowRange = rangeR - rangeL;
        }

        long seg = Long.divideUnsigned(nowRange, transitionSize);
        long tmpnum;
        tmpnum = 0
                + this.transition.getSum(0, byte1, currentByte - 1)
                + this.transition.getSum(1, byte2, currentByte - 1)
                + this.transition.getSum(2, byte3, currentByte - 1);

        long newRangeL = rangeL + tmpnum * seg;

        tmpnum = 0
                + this.transition.getSum(0, byte1, currentByte)
                + this.transition.getSum(1, byte2, currentByte)
                + this.transition.getSum(2, byte3, currentByte);

        long newRangeR = rangeL + tmpnum * seg;

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


    public void encodeSingle(int currentByte) throws IOException {
        if (bitOutput == null) {
            bitOutput = new DefaultBitOutput<>(new StreamByteOutput(outputStream));
            bitOutput.writeLong(false, 64, rawFileSize);
            this.clear();
        }

        if (this.byteRead >= this.rawFileSize) {
            return;
        }

        byteRead++;
//        if (byteRead % (1024 * 1024) == 0) {
//            System.out.println("byteRead : " + byteRead);
//        }
        this.calculateRange(bitOutput, currentByte);
        this.preTransition();
        this.updateTransition(currentByte);

        if (byteRead == this.rawFileSize) {
            for (int i = 0; i < this.overTail; i++) {
                this.calculateRange(bitOutput, currentByte);
                this.preTransition();
                this.updateTransition(currentByte);
            }
        }
    }

    public void encodeAll(InputStream inputStream) throws IOException {
        if (bitOutput == null) {
            bitOutput = new DefaultBitOutput<>(new StreamByteOutput(outputStream));
            bitOutput.writeLong(false, 64, rawFileSize);
            this.clear();
        }


        while (true) {
            int currentByte = inputStream.read();
            this.encodeSingle(currentByte);
            if (this.byteRead >= this.rawFileSize) {
                return;
            }
        }
//        while (this.rangeL != 0) {
//            int output = (int) (rangeL >>> 63);
//            bitOutput.writeBoolean(output != 0);
//            rangeL <<= 1;
//        }
    }


}
