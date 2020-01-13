package com.jules.takemehomecountrytable.Tools.Cipher;

import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class CipherTools {
    public static String[] decipherCredentials(SharedPreferences prefs) {
        try {
            Encrypt.CipherTextIvMac logEncrypted = new Encrypt.CipherTextIvMac(prefs.getString("username", "empty"));
            Encrypt.CipherTextIvMac passEncrypted = new Encrypt.CipherTextIvMac(prefs.getString("password", "empty"));

            byte[] iv = Base64.decode(prefs.getString("iv", "empty"), Base64.DEFAULT);
            byte[] encryption = Base64.decode(prefs.getString("encryption_key", "empty"), Base64.DEFAULT);

            KeyStore ks = KeyStore.getInstance("AndroidKeyStore");
            ks.load(null);

            final KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) ks.getEntry("timekey", null);
            final SecretKey secretKey = secretKeyEntry.getSecretKey();

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            final GCMParameterSpec spec = new GCMParameterSpec(128, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            final byte[] key = cipher.doFinal(encryption);

            String[] cred = new String[2];

            cred[0] = Encrypt.decryptString(logEncrypted, Encrypt.keys(new String(key, StandardCharsets.UTF_8)));
            cred[1] = Encrypt.decryptString(passEncrypted, Encrypt.keys(new String(key, StandardCharsets.UTF_8)));

            return cred;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void cipherCredentials(SharedPreferences prefs, String[] cred) {
        try {
            KeyGenerator kpg = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            kpg.init(new KeyGenParameterSpec.Builder(
                    "timekey",
                    KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setDigests(KeyProperties.DIGEST_SHA256,
                            KeyProperties.DIGEST_SHA512)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build());

            final SecretKey secretKey = kpg.generateKey();

            final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] iv = cipher.getIV();

            Encrypt.SecretKeys key = Encrypt.generateKey();

            byte[] encryption = cipher.doFinal(key.toString().getBytes(StandardCharsets.UTF_8));

            Encrypt.CipherTextIvMac logEncrypted = Encrypt.encrypt(cred[0], key);
            Encrypt.CipherTextIvMac passEncrypted = Encrypt.encrypt(cred[1], key);

            prefs.edit().putString("username", logEncrypted.toString()).apply();
            prefs.edit().putString("password", passEncrypted.toString()).apply();
            prefs.edit().putString("iv", Base64.encodeToString(iv, Base64.DEFAULT)).apply();
            prefs.edit().putString("encryption_key", Base64.encodeToString(encryption, Base64.DEFAULT)).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
