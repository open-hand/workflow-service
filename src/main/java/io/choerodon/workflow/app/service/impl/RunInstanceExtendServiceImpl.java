package io.choerodon.workflow.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.workflow.app.service.RunInstanceExtendService;
import io.choerodon.workflow.infra.dto.RunInstanceExtendDTO;
import io.choerodon.workflow.infra.mapper.RunInstanceExtendMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author zhaotianxin
 * @date 2021-04-09 17:06
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class RunInstanceExtendServiceImpl implements RunInstanceExtendService {
    @Autowired
    private RunInstanceExtendMapper runInstanceExtendMapper;

    @Override
    public RunInstanceExtendDTO create(Long organizationId, Long instanceId) {
        RunInstanceExtendDTO runInstanceExtendDTO = new RunInstanceExtendDTO();
        runInstanceExtendDTO.setInstanceId(instanceId);
        runInstanceExtendDTO.setOrganizationId(organizationId);
        List<RunInstanceExtendDTO> extendDTOS = runInstanceExtendMapper.select(runInstanceExtendDTO);
        if (CollectionUtils.isEmpty(extendDTOS)) {
            if (runInstanceExtendMapper.insertSelective(runInstanceExtendDTO) != 1) {
                throw new CommonException("error.run.instance.extend.insert");
            }
        }
        return runInstanceExtendDTO;
    }
}
