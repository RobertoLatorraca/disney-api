package ar.latorraca.disneyapi.data.security;

import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {

	ROLE_ADMIN(Code.ROLE_ADMIN),
	ROLE_USER(Code.ROLE_USER);

	private String authority;
	
	Authority(String authority) {
		this.authority = authority;
	}	
	
	@Override
	public String getAuthority() {
		return authority;
	}

	private class Code {
        public static final String ROLE_ADMIN = "ROLE_ADMIN";
        public static final String ROLE_USER = "ROLE_USER";
	}

}
