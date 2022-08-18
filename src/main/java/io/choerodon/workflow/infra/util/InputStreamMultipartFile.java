package io.choerodon.workflow.infra.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;

import org.hzero.core.base.BaseConstants;

/**
 * 魔法MultipartFile
 * @author gaokuo.dai@zknow.com 2022-08-18
 */
public class InputStreamMultipartFile implements MultipartFile {

    private final String name;
    @Nullable
    private String originalFilename;
    @Nullable
    private String contentType;
    private final byte[] content;

    private InputStreamMultipartFile(String name, byte[] content) {
        this.name = name;
        this.content = content;
    }

    public static InputStreamMultipartFile create(String name, InputStream inputStream) {
        return create(name, null, inputStream);
    }

    public static InputStreamMultipartFile create(String name, String contentType, InputStream inputStream) {
        return create(name, name, contentType, inputStream);
    }

    public static InputStreamMultipartFile create(String name, @Nullable String originalFilename, @Nullable String contentType, InputStream contentStream) {
        Assert.hasText(name, BaseConstants.ErrorCode.NOT_NULL);
        Assert.notNull(contentStream, BaseConstants.ErrorCode.NOT_NULL);
        try {
            InputStreamMultipartFile result = new InputStreamMultipartFile(name, FileCopyUtils.copyToByteArray(contentStream));
            result.originalFilename = originalFilename;
            result.contentType = contentType;
            return result;
        } catch (IOException iex) {
            throw new CommonException(iex.getMessage(), iex);
        }
    }

    @Override
    @Nonnull
    public String getName() {
        return this.name;
    }

    @Override
    @Nullable
    public String getOriginalFilename() {
        return this.originalFilename;
    }

    @Override
    @Nullable
    public String getContentType() {
        return this.contentType;
    }

    @Override
    public boolean isEmpty() {
        return this.content.length == 0;
    }

    @Override
    public long getSize() {
        return (long) this.content.length;
    }

    @Override
    @Nonnull
    public byte[] getBytes() throws IOException {
        return this.content;
    }

    @Override
    @Nonnull
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.content);
    }

    @Override
    public void transferTo(@Nonnull File dest) throws IOException, IllegalStateException {
        FileCopyUtils.copy(this.content, dest);
    }
}
