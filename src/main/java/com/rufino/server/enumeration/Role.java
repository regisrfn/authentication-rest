package com.rufino.server.enumeration;

import static com.rufino.server.constant.AuthorityConst.USER_AUTHORITIES;
import static com.rufino.server.constant.AuthorityConst.HR_AUTHORITIES;
import static com.rufino.server.constant.AuthorityConst.MANAGER_AUTHORITIES;
import static com.rufino.server.constant.AuthorityConst.ADMIN_AUTHORITIES;
import static com.rufino.server.constant.AuthorityConst.SUPER_ADMIN_AUTHORITIES;

public enum Role {

    ROLE_USER(USER_AUTHORITIES), ROLE_HR(HR_AUTHORITIES), ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES), ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    private Authority[] authorities;

    Role(Authority... authorities) {
        this.authorities = authorities;
    }

    public Authority[] getAuthorities() {
        return authorities;
    }

}
