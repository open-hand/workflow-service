package io.choerodon.workflow.domain.aspect;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.workflow.infra.util.ActivitiUserLoginUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;

/**
 * Created by Sheep on 2019/4/2.
 */
@Aspect
@Component
public class UserRegistryAspect {


    private Logger logger = LoggerFactory.getLogger(UserRegistryAspect.class);


    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private ActivitiUserLoginUtil activitiUserLoginUtil;

    /**
     * 为所有controller类下面增加一个activiti用户注册，注册到内存用户信息中，用于activiti7的登陆
     */
    @Pointcut("execution(public * io.choerodon.workflow.app.service.impl.ProcessInstanceServiceImpl.*(..))")
    public void userRegistry() {
    }

    /**
     * @param joinPoint
     * @throws Throwable
     */
    @Before("userRegistry()")
    public void userRegistry(JoinPoint joinPoint) {
        CustomUserDetails details = DetailsHelper.getUserDetails();
        String activitiGroup = "Group_activiti" + details.getOrganizationId();
        String[] user = {details.getUserId().toString(), "password", "ROLE_ACTIVITI_USER", activitiGroup};
        List<String> authoritiesStrings = Arrays.asList(Arrays.copyOfRange(user, 2, user.length));
        if (!((InMemoryUserDetailsManager) userDetailsService).userExists(details.getUserId().toString())) {
            ((InMemoryUserDetailsManager) userDetailsService).createUser(new User(user[0], passwordEncoder.encode(user[1]),
                    authoritiesStrings.stream().map(s -> new SimpleGrantedAuthority(s)).collect(Collectors.toList())));
        }
        activitiUserLoginUtil.logInAs(details.getUserId().toString());
    }
}
