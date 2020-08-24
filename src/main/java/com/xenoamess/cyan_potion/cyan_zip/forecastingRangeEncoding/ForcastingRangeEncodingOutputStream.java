package com.xenoamess.cyan_potion.cyan_zip.forecastingRangeEncoding;

import java.io.IOException;
import java.io.OutputStream;

public class ForcastingRangeEncodingOutputStream extends OutputStream {
    protected OutputStream outputStream;
    protected ForcastingRangeEncodingEncoder forcastingRangeEncodingEncoder;

    /**
     * @param rawFileSize  size of the original file.
     * @param outputStream outputStream
     */
    public ForcastingRangeEncodingOutputStream(long rawFileSize, OutputStream outputStream) {
        this.outputStream = outputStream;
        forcastingRangeEncodingEncoder = new ForcastingRangeEncodingEncoder(rawFileSize, outputStream);
    }

    @Override
    public void write(int b) throws IOException {
        forcastingRangeEncodingEncoder.encodeSingle(b);
    }

    @Override
    public void flush() throws IOException {
        this.outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        this.outputStream.close();
    }
}
