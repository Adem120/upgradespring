package com.adem.security;

import com.adem.Controllers.RedirectAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Autowired
    private RedirectAuthenticationSuccessHandler Redirect;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                        .authorizeRequests()
                .requestMatchers("/webjars/**", "/login", "/register", "/saveuser").permitAll().requestMatchers("ajoutermachine").hasAuthority("ADMIN").anyRequest().authenticated().and()

                                .formLogin(form -> form
                                        .loginPage("/login")
                                        .defaultSuccessUrl("/machines", true)
                                        .loginProcessingUrl("/login")
                                        .failureUrl("/login?error=true")
                                        .successHandler(Redirect) // add this line

                                        .permitAll()
                                ).logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).permitAll()
                        .invalidateHttpSession(true)
                        .logoutSuccessUrl("/login")
                ).exceptionHandling().accessDeniedPage("/access");
        return httpSecurity.build();
    }

    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }
}
