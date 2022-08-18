package org.hzero.starter.keyencrypt.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;

import org.hzero.core.util.TokenUtils;

/**
 * @author xiangyu.qi01@hand-china.com on 2019-11-29.
 * <p color="red">覆盖解密部分, 解决工作流json解密报错问题</p>
 */
public interface IEncryptionService {

    /**
     * 只是为了兼容旧代码，不应该在使用该变量
     */
    String WORKFLOW_TABLE = "8ac987c65ffa4974bfd9460058468706";

    String IGNORE_DECRYPT = "8ac987c65ffa4974bfd9460058468706";

    String NO_TOKEN = "NO_TOKEN";

    default String decrypt(String value) {
        // ↓↓↓↓二开部分, 覆盖accessToken强制为null, 覆盖ignoreUserConflict强制为true↓↓↓↓
        return decrypt(value, StringUtils.EMPTY, null, true);
        // ↑↑↑↑二开部分, 覆盖accessToken强制为null, 覆盖ignoreUserConflict强制为true↑↑↑↑
    }

    /**
     * Decrypt an encrypted JSON object. Contains salt and iv as fields
     *
     * @param value JSON data
     * @return Decrypted byte array
     */
    default String decrypt(String value, String tableName) {
        // ↓↓↓↓二开部分, 覆盖accessToken强制为null, 覆盖ignoreUserConflict强制为true↓↓↓↓
        return decrypt(value, tableName, null, true);
        // ↑↑↑↑二开部分, 覆盖accessToken强制为null, 覆盖ignoreUserConflict强制为true↑↑↑↑
    }

    default String decrypt(String value, String tableName, boolean ignoreUserConflict) {
        // ↓↓↓↓二开部分, 覆盖当ignoreUserConflict=true时accessToken强制为null↓↓↓↓
        return decrypt(value, tableName, ignoreUserConflict ? null : TokenUtils.getToken(), ignoreUserConflict);
        // ↑↑↑↑二开部分, 覆盖当ignoreUserConflict=true时accessToken强制为null↑↑↑↑
    }

    default String decrypt(String value, String tableName, String accessToken) {
        return decrypt(value, tableName, accessToken, false);
    }

    String decrypt(String value, String tableName, String accessToken, boolean ignoreUserConflict);

    Object decrypt(final JsonParser parser, final JsonDeserializer<?> deserializer, final DeserializationContext context, JavaType javaType, Encrypt encrypt);

    default String encrypt(String id) {
        return encrypt(id, StringUtils.EMPTY, TokenUtils.getToken());
    }

    /**
     * Encrypted a string as a byte array and encode using base 64
     *
     * @param tableName tableName
     * @param id        Byte array to be encrypted
     * @return Encrypted data
     */
    String encrypt(String id, String tableName);

    String encrypt(String id, String tableName, String accessToken);

    void setObjectMapper(ObjectMapper objectMapper);

    boolean isCipher(String content);
}
