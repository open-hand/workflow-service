package io.choerodon.workflow.infra.feign;

import java.util.List;
import java.util.Set;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import io.choerodon.workflow.infra.feign.fallback.IamFeignClientFallbackFactory;
import io.choerodon.workflow.infra.feign.vo.OrganizationInfoVO;

import org.hzero.workflow.def.infra.feign.dto.UserDTO;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 14:37:45
 */
@Component
@FeignClient(value = "zknow-iam", fallback = IamFeignClientFallbackFactory.class)
public interface IamFeignClient {

    /**
     *
     * @param ids
     * @param onlyEnabled
     * @return List<UserDTO>
     */
    @PostMapping(value = "/choerodon/v1/users/ids")
    ResponseEntity<String> listUsersByIds(@RequestBody Long[] ids, @RequestParam(name = "only_enabled") Boolean onlyEnabled);

    /**
     *
     * @param id
     * @return OrganizationInfoVO
     */
    @GetMapping(value = "/choerodon/v1/organizations/{organization_id}")
    ResponseEntity<OrganizationInfoVO> queryOrganizationInfo(@PathVariable(name = "organization_id") Long id);

    /**
     *
     * @param onlyEnabled
     * @param realNames
     * @return List<UserDTO>
     */
    @PostMapping(value = "/choerodon/v1/users/real_names")
    ResponseEntity<List<UserDTO>> listUsersByRealNames(@RequestParam(name = "only_enabled") Boolean onlyEnabled,
                                                       @RequestBody Set<String> realNames);
}
