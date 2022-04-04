package ar.latorraca.disneyapi.rest.security;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@ApiModel(value = "Authentication Request")
@Setter
@Getter
public class AuthRequest {

	@Email
	@NotBlank(message = "Email cannot be null or blank")
	private String email;
	
	@NotBlank(message = "Password cannot be null or blank")
	private String password;
	
}
