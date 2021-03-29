package io.choerodon.workflow.api.vo;

import org.hzero.workflow.def.infra.feign.dto.UserDTO;
import org.hzero.workflow.engine.dao.entity.RunTaskHistory;

public class RunTaskHistoryVO {
    private UserDTO userDTO;
    private RunTaskHistory runTaskHistory;

    public RunTaskHistory getRunTaskHistory() {
        return runTaskHistory;
    }

    public void setRunTaskHistory(RunTaskHistory runTaskHistory) {
        this.runTaskHistory = runTaskHistory;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

    public void setUserDTO(UserDTO userDTO) {
        this.userDTO = userDTO;
    }
}
