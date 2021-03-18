package com.rufino.server.constant;

import com.rufino.server.enumeration.Authority;

import static com.rufino.server.enumeration.Authority.*;

public class AuthorityConst {
    public static final Authority[] USER_AUTHORITIES = { READ };
    public static final Authority[] HR_AUTHORITIES = { READ, UPDATE };
    public static final Authority[] MANAGER_AUTHORITIES = { READ, UPDATE };
    public static final Authority[] ADMIN_AUTHORITIES = { READ, UPDATE, WRITE };
    public static final Authority[] SUPER_ADMIN_AUTHORITIES = { READ, UPDATE, WRITE, DELETE };
}
