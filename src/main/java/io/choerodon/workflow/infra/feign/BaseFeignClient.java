package io.choerodon.workflow.infra.feign;

import io.choerodon.workflow.infra.feign.fallback.BaseFeignClientFallback;
import io.choerodon.workflow.infra.feign.vo.OrganizationInfoVO;

import org.hzero.workflow.def.infra.feign.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 14:37:45
 */
@Component
@FeignClient(value = "choerodon-iam", fallback = BaseFeignClientFallback.class)
public interface BaseFeignClient {

    @PostMapping(value = "/choerodon/v1/users/ids")
    List<UserDTO> listUsersByIds(@RequestBody Long[] ids,
                                                 @RequestParam(name = "only_enabled") Boolean onlyEnabled);

    @GetMapping(value = "/choerodon/v1/organizations/{organization_id}")
    ResponseEntity<OrganizationInfoVO> queryOrganizationInfo(@PathVariable(name = "organization_id") Long id);

    @PostMapping(value = "/choerodon/v1/users/real_names")
    ResponseEntity<List<UserDTO>> listUsersByRealNames(@RequestParam(name = "only_enabled") Boolean onlyEnabled,
                                                       @RequestBody Set<String> realNames);
}
