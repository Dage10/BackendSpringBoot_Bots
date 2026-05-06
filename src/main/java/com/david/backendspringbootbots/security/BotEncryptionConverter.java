package com.david.backendspringbootbots.security;

import com.david.backendspringbootbots.services.CryptoService;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BotEncryptionConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String plain) {
        return CryptoService.encrypt(plain);
    }

    @Override
    public String convertToEntityAttribute(String encrypted) {
        return CryptoService.decrypt(encrypted);
    }
}