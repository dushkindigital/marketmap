package com.libereco.springsocial.etsy.api.impl;

import org.springframework.core.io.ByteArrayResource;

public class EtsyByteArrayResource extends ByteArrayResource {

    private String fileName;

    public EtsyByteArrayResource(byte[] byteArray, String fileName) {
        super(byteArray);
        this.fileName = fileName;
    }

    @Override
    public String getFilename() throws IllegalStateException {
        return this.fileName;
    }
}
