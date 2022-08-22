package io.choerodon.workflow.infra.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;


/**
 * 预定义工作流数据工具
 * @author gaokuo.dai@zknow.com 2022-08-18
 */
public class PredefineWorkflowDataUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(PredefineWorkflowDataUtil.class);
    private static final String FILE_NAME = "predefine_workflow.zip";
    private static final String FILE_PATH = "/templates/" + FILE_NAME;

    private PredefineWorkflowDataUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * @return 预定义工作流zip文件
     */
    public static MultipartFile generateMultipartFile() {
        try{
            return InputStreamMultipartFile.create(
                    "predefine_workflow.zip",
                    PredefineWorkflowDataUtil.class.getResourceAsStream(FILE_PATH)
            );
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return null;
        }
    }

}
