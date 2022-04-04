package ar.latorraca.disneyapi.services.security;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
	
	UserDetails findByUsername(String username);
	
	UserDetails save(UserDetails userDetails);

}
