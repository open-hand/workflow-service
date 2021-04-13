package io.choerodon.workflow.app.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.workflow.app.service.BacklogInstanceRelService;
import io.choerodon.workflow.infra.dto.BacklogInstanceRelDTO;
import io.choerodon.workflow.infra.mapper.BacklogInstanceRelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author zhaotianxin
 * @date 2021-04-12 16:22
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BacklogInstanceRelServiceImpl implements BacklogInstanceRelService {
    @Autowired
    private BacklogInstanceRelMapper backlogInstanceRelMapper;

    @Override
    public BacklogInstanceRelDTO create(Long organizationId, Long instanceId, Long backlogId) {
        BacklogInstanceRelDTO backlogInstanceRelDTO = new BacklogInstanceRelDTO(instanceId, organizationId, backlogId);
        List<BacklogInstanceRelDTO> businessKeyRelDTOS = backlogInstanceRelMapper.select(backlogInstanceRelDTO);
        if (CollectionUtils.isEmpty(businessKeyRelDTOS)) {
            if (backlogInstanceRelMapper.insertSelective(backlogInstanceRelDTO) != 1) {
                throw new CommonException("error.backlog.business.key.rel.insert");
            }
        }
        return backlogInstanceRelDTO;
    }
}
