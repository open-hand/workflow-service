package io.choerodon.workflow.infra.feign;

import io.choerodon.workflow.infra.feign.fallback.BaseFeignClientFallback;

import org.hzero.workflow.def.infra.feign.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 14:37:45
 */
@Component
@FeignClient(value = "choerodon-iam", fallback = BaseFeignClientFallback.class)
public interface BaseFeignClient {

    @PostMapping(value = "/choerodon/v1/users/ids")
    List<UserDTO> listUsersByIds(@RequestBody Long[] ids,
                                                 @RequestParam(name = "only_enabled") Boolean onlyEnabled);
}
