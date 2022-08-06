package io.choerodon.workflow.api.vo;

import org.hzero.workflow.def.infra.feign.dto.UserDTO;
import org.hzero.workflow.engine.dao.entity.RunTaskHistory;

public class RunTaskHistoryVO {
    private UserDTO userDTO;
    private RunTaskHistory runTaskHistory;

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public RunTaskHistoryVO setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
        return this;
    }

    public RunTaskHistory getRunTaskHistory() {
        return runTaskHistory;
    }

    public RunTaskHistoryVO setRunTaskHistory(RunTaskHistory runTaskHistory) {
        this.runTaskHistory = runTaskHistory;
        return this;
    }
}
