package com.xenoamess.cyan_zip.forecastingRangeEncoding;

import java.io.IOException;
import java.io.OutputStream;

public class ForcastingRangeEncodingOutputStream extends OutputStream {
    public OutputStream outputStream;
    public ForcastingRangeEncodingEncoder forcastingRangeEncodingEncoder;

    /**
     * @param rawFileSize  size of the original file.
     * @param outputStream
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
