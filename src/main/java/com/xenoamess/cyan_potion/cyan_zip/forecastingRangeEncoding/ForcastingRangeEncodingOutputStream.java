package com.xenoamess.cyan_potion.cyan_zip.forecastingRangeEncoding;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>ForcastingRangeEncodingOutputStream class.</p>
 *
 * @author XenoAmess
 * @version 0.1.1
 */
public class ForcastingRangeEncodingOutputStream extends OutputStream {
    protected OutputStream outputStream;
    protected ForcastingRangeEncodingEncoder forcastingRangeEncodingEncoder;

    /**
     * <p>Constructor for ForcastingRangeEncodingOutputStream.</p>
     *
     * @param rawFileSize  size of the original file.
     * @param outputStream outputStream
     */
    public ForcastingRangeEncodingOutputStream(long rawFileSize, OutputStream outputStream) {
        this.outputStream = outputStream;
        forcastingRangeEncodingEncoder = new ForcastingRangeEncodingEncoder(rawFileSize, outputStream);
    }

    /** {@inheritDoc} */
    @Override
    public void write(int b) throws IOException {
        forcastingRangeEncodingEncoder.encodeSingle(b);
    }

    /** {@inheritDoc} */
    @Override
    public void flush() throws IOException {
        this.outputStream.flush();
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        this.outputStream.close();
    }
}
