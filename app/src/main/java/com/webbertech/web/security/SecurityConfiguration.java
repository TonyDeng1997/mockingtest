package com.webbertech.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	@Autowired
    @Qualifier("customUserDetailsService")
    UserDetailsService userDetailsService;
 
    @Autowired
    PersistentTokenRepository tokenRepository;
    
    @Autowired
    public void configureGlobalSecurity(AuthenticationManagerBuilder auth) throws Exception {
        //auth.inMemoryAuthentication().withUser("bill").password("abc123").roles("USER");
        //auth.inMemoryAuthentication().withUser("admin").password("root123").roles("ADMIN");
        auth.userDetailsService(userDetailsService);
        auth.authenticationProvider(authenticationProvider());
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
 
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
    
    @Bean
    public AuthenticationTrustResolver getAuthenticationTrustResolver() {
        return new AuthenticationTrustResolverImpl();
    }
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
      /*
      http.httpBasic().
      and().authorizeRequests()
        .antMatchers(HttpMethod.GET, "/", "/index").permitAll() 
        .antMatchers(HttpMethod.POST, "/employee/**").permitAll()
        .antMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
        .antMatchers(HttpMethod.GET, "/logout").authenticated()
        .and().formLogin()
        .and().exceptionHandling().accessDeniedPage("/Access_Denied")
        .and().csrf().disable();
      */
    	
      /**/
      http.authorizeRequests().antMatchers("/", "/list")
      .access("hasRole('USER') or hasRole('ADMIN') or hasRole('DBA')")
      .antMatchers(HttpMethod.GET, "/", "/index").permitAll() 
      .antMatchers(HttpMethod.POST, "/employee/**").permitAll()
      .antMatchers("/newuser/**", "/delete-user-*").access("hasRole('ADMIN')")
      .antMatchers("/edit-user-*").access("hasRole('ADMIN') or hasRole('DBA')").and().formLogin().loginPage("/login")
      .loginProcessingUrl("/login").usernameParameter("ssoId").passwordParameter("password").and()
      .rememberMe().rememberMeParameter("remember-me").tokenRepository(tokenRepository)
      .tokenValiditySeconds(86400).and().csrf().and().exceptionHandling().accessDeniedPage("/Access_Denied");
    }
}