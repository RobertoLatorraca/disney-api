package ar.latorraca.disneyapi.configuration;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import ar.latorraca.disneyapi.services.security.jwt.JwtAuthenticationEntryPoint;
import ar.latorraca.disneyapi.services.security.jwt.JwtTokenFilter;

@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	
	@Autowired
	private JwtTokenFilter jwtTokenFilter;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Enable CORS and disable CSRF
		http.cors().and().csrf().disable()
		// Set session management to stateless
				.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
		// Set unauthorized requests exception handler
				.exceptionHandling()
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.and()
		// Set permissions on endpoints
				.authorizeRequests()
				// Public endpoints
				.antMatchers(HttpMethod.POST, "/auth/**").permitAll()
				.antMatchers("/swagger-ui/**").permitAll()
				// Private endpoints
				.anyRequest().authenticated()
				.and()
		// Add JWT Token Filter
				.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);		
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/v2/api-docs", "/swagger-ui.html", "/swagger-resources/**")
				.antMatchers(HttpMethod.OPTIONS, "/**");
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource sources = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfig = new CorsConfiguration();
		corsConfig.setAllowCredentials(true);
		corsConfig.setAllowedOriginPatterns(List.of("*"));
		corsConfig.setAllowedHeaders(List.of("*"));
		corsConfig.addAllowedMethod("*");
		sources.registerCorsConfiguration("/**", corsConfig);
		return new CorsFilter(sources);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}