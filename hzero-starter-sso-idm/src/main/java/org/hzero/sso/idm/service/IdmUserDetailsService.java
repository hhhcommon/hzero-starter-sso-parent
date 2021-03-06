package org.hzero.sso.idm.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import org.hzero.core.user.UserType;
import org.hzero.sso.core.domain.entity.SsoUser;
import org.hzero.sso.core.security.service.SsoUserAccountService;
import org.hzero.sso.core.security.service.SsoUserDetailsBuilder;
import org.hzero.sso.idm.token.IdmAuthenticationToken;
import org.hzero.sso.core.exception.LoginExceptions;

/**
*
* @author minghui.qiu@hand-china.com
*/
public class IdmUserDetailsService implements AuthenticationUserDetailsService<IdmAuthenticationToken> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdmUserDetailsService.class);

    private SsoUserAccountService userAccountService;
    private SsoUserDetailsBuilder userDetailsBuilder;

    public IdmUserDetailsService(SsoUserAccountService userAccountService,
                                   SsoUserDetailsBuilder userDetailsBuilder) {
        this.userAccountService = userAccountService;
        this.userDetailsBuilder = userDetailsBuilder;
    }

    @Override
    public UserDetails loadUserDetails(IdmAuthenticationToken token) throws UsernameNotFoundException {
    	String username = token.getName();
        Long tenantId = Long.valueOf(String.valueOf(token.getCredentials()));
        LOGGER.debug("load auth2 user, username={}, tenantId={},token={}", username, tenantId, token);
        SsoUser user = userAccountService.findLoginUser(username, UserType.ofDefault());
        Assert.notNull(user, "User is Not Exists");
        List<Long> organizationIdList = userAccountService.findUserLegalOrganization(user.getId());
        if (!organizationIdList.contains(tenantId)){
            throw new UsernameNotFoundException(LoginExceptions.USERNAME_NOT_FOUND.value());
        }
        return userDetailsBuilder.buildUserDetails(user);
    }

}
