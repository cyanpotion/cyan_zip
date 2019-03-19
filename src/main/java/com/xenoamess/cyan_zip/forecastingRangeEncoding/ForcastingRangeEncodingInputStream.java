package com.xenoamess.cyan_zip.forecastingRangeEncoding;

import java.io.IOException;
import java.io.InputStream;

public class ForcastingRangeEncodingInputStream extends InputStream {
    public InputStream inputStream;
    public ForcastingRangeEncodingDecoder forcastingRangeEncodingDecoder;

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
