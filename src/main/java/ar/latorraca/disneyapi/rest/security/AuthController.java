package ar.latorraca.disneyapi.rest.security;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.latorraca.disneyapi.data.security.Authority;
import ar.latorraca.disneyapi.services.exception.FieldAlreadyExistException;
import ar.latorraca.disneyapi.services.security.UserService;
import ar.latorraca.disneyapi.services.security.jwt.JwtUtils;
import ar.latorraca.disneyapi.services.sendgrid.SendGridFacade;
import io.swagger.annotations.Api;

@RestController
@RequestMapping(AuthController.AUTH)
@Api(tags = "Authentication controller")
public class AuthController {

	protected static final String AUTH = "/auth";
	private static final String LOGIN = "/login";
	private static final String REGISTER = "/register";
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private SendGridFacade sendGridFacade;
	
	@PostMapping(LOGIN)
	public ResponseEntity<?> login(@Valid @RequestBody AuthRequest request) {
		try {
			Authentication authenticate = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
			User user = (User) authenticate.getPrincipal();
			return ResponseEntity.ok()
					.header(HttpHeaders.AUTHORIZATION, JwtUtils.generateAccessToken(user))
					.build();
		} catch (Exception e) {
			System.out.println();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}
	
	@PostMapping(REGISTER)
	public ResponseEntity<?> register(@Valid @RequestBody AuthRequest request) {
		if (userService.findByUsername(request.getEmail()) != null)
				throw new FieldAlreadyExistException("User " + request.getEmail() + " already exists.");
		UserDetails user = User.withUsername(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.authorities(Authority.ROLE_USER.toString())
				.build();
		sendGridFacade.sendMail("Welcome to Disney API", request.getEmail(),
				"Welcome to the Disney API.\r\n"
	    		+ "Start exploring the world of Disney.");
		return ResponseEntity.ok()
				.header(HttpHeaders.AUTHORIZATION, JwtUtils.generateAccessToken(userService.save(user)))
				.build();
	}
	
}
