package com.xenoamess.cyan_potion.cyan_zip.forecastingRangeEncoding;

import java.io.IOException;
import java.io.InputStream;

/**
 * <p>ForcastingRangeEncodingInputStream class.</p>
 *
 * @author XenoAmess
 * @version 0.1.1
 */
public class ForcastingRangeEncodingInputStream extends InputStream {
    protected InputStream inputStream;
    protected ForcastingRangeEncodingDecoder forcastingRangeEncodingDecoder;

    /**
     * <p>Constructor for ForcastingRangeEncodingInputStream.</p>
     *
     * @param inputStream a {@link java.io.InputStream} object.
     */
    public ForcastingRangeEncodingInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        forcastingRangeEncodingDecoder = new ForcastingRangeEncodingDecoder(inputStream);
    }

    /** {@inheritDoc} */
    @Override
    public int read() throws IOException {
        return forcastingRangeEncodingDecoder.decodeSingle();
    }

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

}
