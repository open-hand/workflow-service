package io.choerodon.workflow.infra.feign.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import io.choerodon.core.utils.FeignFallbackUtil;
import io.choerodon.workflow.infra.feign.IamFeignClient;

/**
 * @author huaxin.deng@hand-china.com 2021-03-12 14:38:41
 */
@Component
public class IamFeignClientFallbackFactory implements FallbackFactory<IamFeignClient> {
    @Override
    public IamFeignClient create(Throwable cause) {
        return FeignFallbackUtil.get(cause, IamFeignClient.class);
    }
}
