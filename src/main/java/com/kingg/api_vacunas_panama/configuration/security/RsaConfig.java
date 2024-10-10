package com.kingg.api_vacunas_panama.configuration.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Configuration
public class RsaConfig {
    @Value("${security.jwt.public}")
    private String rsaPublicKey;
    @Value("${security.jwt.private}")
    private String rsaPrivateKey;
    @Value("${security.jwt.public.key}")
    private RSAPublicKey publicKey;
    @Value("${security.jwt.private.key}")
    private RSAPrivateKey privateKey;

    @Bean
    public RSAPrivateKey getPrivate() throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (privateKey != null) {
            log.info("Private key file found");
            return privateKey;
        } else if (rsaPrivateKey != null) {
            log.info("Private key String found");
            return getPrivateKeyFromString();
        }
        return null;
    }

    @Bean
    public RSAPublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (publicKey != null) {
            log.info("Public key file found");
            return publicKey;
        } else if (rsaPublicKey != null) {
            log.info("Public key String found");
            return getPublicKeyFromString();
        }
        return null;
    }

    public RSAPublicKey getPublicKeyFromString() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String key = rsaPublicKey
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(key);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    public RSAPrivateKey getPrivateKeyFromString() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String key = rsaPrivateKey
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
    }
}
