package com.xenoamess.cyan_zip.forecastingRangeEncoding;

import com.github.jinahya.bit.io.BitOutput;
import com.github.jinahya.bit.io.DefaultBitOutput;
import com.github.jinahya.bit.io.StreamByteOutput;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class Test {
    public static void main(String args[]) throws Exception {
        File testFile = new File("tmp/test.txt");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(testFile));

        BitOutput bitOutput = new DefaultBitOutput<>(new StreamByteOutput(bos));
        for (int i = 0; i < 160000000; i++) {
            bitOutput.writeBoolean(false);
        }
        for (int i = 0; i < 160000000; i++) {
            bitOutput.writeBoolean(true);
        }

        bos.close();
    }
}
