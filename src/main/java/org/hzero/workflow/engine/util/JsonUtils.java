//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.hzero.workflow.engine.util;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.base.BaseConstants;
import org.hzero.starter.keyencrypt.core.EncryptContext;
import org.hzero.starter.keyencrypt.core.EncryptType;

/**
 * 覆盖工作流JsonUtil主键加密处理解析错误的问题
 * @author gaokuo.dai@zknow.com 2022-08-05
 */
public class JsonUtils {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);
    public static final String ZONE_DATE_FORMAT = BaseConstants.Pattern.DATETIME;
    static final ObjectMapper objectMapper = new ObjectMapper();

    public JsonUtils() {
    }

    public static String toJson(Object data) {
        try {
            return getObjectMapper().writeValueAsString(data);
        } catch (JsonProcessingException var3) {
            throw new CommonException(var3.getMessage());
        }
    }

    public static String toJson(ObjectMapper objectMapper, Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException var4) {
            throw new CommonException(var4.getMessage());
        }
    }

    public static <T> T toObject(ObjectMapper objectMapper, String json, Class<T> clazz) {
        EncryptContext.ignoreException();
        // ↓↓↓↓ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↓↓↓↓
        final EncryptType originEncryptType = EncryptContext.encryptType();
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        final Integer originApiEncryptFlag = Optional.ofNullable(userDetails).map(CustomUserDetails::getApiEncryptFlag).orElse(null);
        // ↑↑↑↑ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↑↑↑↑
        T t = null;
        try {
            t = objectMapper.readValue(json, clazz);
        } catch (Exception var8) {
            // ↓↓↓↓ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↓↓↓↓
            // 解析失败了
            if(EncryptType.ENCRYPT.equals(originEncryptType)) {
                // 如果当前开了主键加密, 说明数据不是加密数, 关闭主键加密再尝试一下
                EncryptContext.setEncryptType(EncryptType.DO_NOTHING.name());
                try {
                    t = objectMapper.readValue(json, clazz);
                } catch (Exception var9) {
                    throw new CommonException(var9.getMessage(), var9);
                }
            } else {
                // 如果当前没开主键加密, 说明数据是加密数, 开启主键加密再尝试一下
                if(userDetails == null) {
                    userDetails = DetailsHelper.getAnonymousDetails();
                }
                userDetails.setApiEncryptFlag(BaseConstants.Flag.YES);
                DetailsHelper.setCustomUserDetails(userDetails);

                EncryptContext.setEncryptType(EncryptType.ENCRYPT.name());
                try {
                    t = objectMapper.readValue(json, clazz);
                } catch (Exception var9) {
                    throw new CommonException(var9.getMessage(), var9);
                }
            }
            // ↑↑↑↑ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↑↑↑↑
        } finally {
            EncryptContext.clearIgnoreException();
            // ↓↓↓↓ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↓↓↓↓
            // 回复主键加密上下文
            if(originEncryptType == null) {
                EncryptContext.clear();
            } else {
                EncryptContext.setEncryptType(originEncryptType.name());
            }
            if(userDetails != null) {
                userDetails.setApiEncryptFlag(originApiEncryptFlag);
            }
            // ↑↑↑↑ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↑↑↑↑
        }

        return t;
    }

    public static <T> T toObject(ObjectMapper objectMapper, String json, TypeReference<T> typeReference) {
        EncryptContext.ignoreException();
        // ↓↓↓↓ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↓↓↓↓
        final EncryptType originEncryptType = EncryptContext.encryptType();
        final CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        final Integer originApiEncryptFlag = Optional.ofNullable(userDetails).map(CustomUserDetails::getApiEncryptFlag).orElse(null);
        // ↑↑↑↑ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↑↑↑↑
        T t = null;
        try {
            t = objectMapper.readValue(json, typeReference);
        } catch (Exception var8) {
            // ↓↓↓↓ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↓↓↓↓
            // 解析失败了
            if(EncryptType.ENCRYPT.equals(originEncryptType)) {
                // 如果当前开了主键加密, 说明数据不是加密数, 关闭主键加密再尝试一下
                EncryptContext.setEncryptType(EncryptType.DO_NOTHING.name());
                try {
                    t = objectMapper.readValue(json, typeReference);
                } catch (Exception var9) {
                    throw new CommonException(var9.getMessage(), var9);
                }
            } else {
                // 如果当前没开主键加密, 说明数据是加密数, 开启主键加密再尝试一下
                if(userDetails != null) {
                    userDetails.setApiEncryptFlag(BaseConstants.Flag.YES);
                }
                EncryptContext.setEncryptType(EncryptType.ENCRYPT.name());
                try {
                    t = objectMapper.readValue(json, typeReference);
                } catch (Exception var9) {
                    throw new CommonException(var9.getMessage(), var9);
                }
            }
            // ↑↑↑↑ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↑↑↑↑
        } finally {
            EncryptContext.clearIgnoreException();
            // ↓↓↓↓ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↓↓↓↓
            // 回复主键加密上下文
            if(originEncryptType == null) {
                EncryptContext.clear();
            } else {
                EncryptContext.setEncryptType(originEncryptType.name());
            }
            if(userDetails != null) {
                userDetails.setApiEncryptFlag(originApiEncryptFlag);
            }
            // ↑↑↑↑ 二开部分 gaokuo.dai@zknow.com 2022-08-05 ↑↑↑↑
        }

        return t;
    }

    public static JsonNode jsonToJsonNode(ObjectMapper objectMapper, String json) {
        try {
            return objectMapper.readTree(json);
        } catch (IOException var4) {
            throw new CommonException(var4.getMessage());
        }
    }

    public static JsonNode objectToJsonNode(ObjectMapper objectMapper, Object data) {
        try {
            return objectMapper.readTree(objectMapper.writeValueAsString(data));
        } catch (IOException var4) {
            throw new CommonException(var4.getMessage());
        }
    }

    public static Object spelValueFromJson(String json, String spelExpression, ObjectMapper objectMapper) {
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        Expression expression = spelExpressionParser.parseExpression(spelExpression);
        Object resultObj = null;

        try {
            resultObj = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception var8) {
            logger.debug("[Spel parse]Service result is not a Map");
        }

        if (Objects.isNull(resultObj)) {
            try {
                resultObj = objectMapper.readValue(json, new TypeReference<List<Object>>() {
                });
            } catch (Exception var7) {
                logger.debug("[Spel parse]Service result is not a List");
            }
        }

        if (Objects.isNull(resultObj)) {
            logger.debug("[Spel parse]Service result is a String");
            return json;
        } else {
            StandardEvaluationContext context = new StandardEvaluationContext(resultObj);
            context.addPropertyAccessor(new MapAccessor());
            return expression.getValue(context);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (!StringUtils.isBlank(json) && clazz != null) {
            try {
                return getObjectMapper().readValue(json, clazz);
            } catch (Exception var3) {
                logger.error(var3.getMessage(), var3);
                throw new CommonException(var3.getMessage());
            }
        } else {
            return null;
        }
    }

    public static <T> T fromJson(String json, TypeReference<T> valueTypeRef) {
        if (!StringUtils.isBlank(json) && valueTypeRef != null) {
            try {
                return getObjectMapper().readValue(json, valueTypeRef);
            } catch (Exception var3) {
                logger.error(var3.getMessage(), var3);
                return null;
            }
        } else {
            return null;
        }
    }

    public static <T> List<T> fromJsonList(String json, Class<T> clazz) {
        JavaType javaType = (new ObjectMapper()).getTypeFactory().constructParametricType(List.class, clazz);

        try {
            return getObjectMapper().readValue(json, javaType);
        } catch (IOException var4) {
            logger.error(var4.getMessage(), var4);
            return new ArrayList<>();
        }
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    static {
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Date.class, new DateSerializer());
        javaTimeModule.addDeserializer(Date.class, new DateDeserializer());
        objectMapper.registerModule(javaTimeModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static class DateSerializer extends JsonSerializer<Date> {
        final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

        public DateSerializer() {
        }

        public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
            jsonGenerator.writeString(this.dateFormat.format(date));
        }
    }

    public static class DateDeserializer extends JsonDeserializer<Date> {
        final Logger logger = LoggerFactory.getLogger(DateDeserializer.class);
        final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

        public DateDeserializer() {
        }

        public Date deserialize(JsonParser jsonParser, DeserializationContext context) throws IOException {
            try {
                return this.dateFormat.parse(jsonParser.getValueAsString());
            } catch (ParseException var4) {
                this.logger.warn("date format error : {}", ExceptionUtils.getStackTrace(var4));
                return null;
            }
        }
    }
}
