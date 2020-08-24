package com.xenoamess.cyan_potion.cyan_zip.forecastingRangeEncoding;

import java.io.IOException;
import java.io.InputStream;

public class ForcastingRangeEncodingInputStream extends InputStream {
    protected InputStream inputStream;
    protected ForcastingRangeEncodingDecoder forcastingRangeEncodingDecoder;

    public ForcastingRangeEncodingInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        forcastingRangeEncodingDecoder = new ForcastingRangeEncodingDecoder(inputStream);
    }

    @Override
    public int read() throws IOException {
        return forcastingRangeEncodingDecoder.decodeSingle();
    }

    @Override
    public void close() throws IOException {
        this.inputStream.close();
    }

}
