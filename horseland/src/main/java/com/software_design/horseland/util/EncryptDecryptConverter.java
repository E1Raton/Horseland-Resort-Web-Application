package com.software_design.horseland.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;

@Converter
@AllArgsConstructor
public class EncryptDecryptConverter implements AttributeConverter<String, String> {

    private final CryptoUtil cryptoUtil;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        try {
            return cryptoUtil.encrypt(attribute);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        try {
            return cryptoUtil.decrypt(dbData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

