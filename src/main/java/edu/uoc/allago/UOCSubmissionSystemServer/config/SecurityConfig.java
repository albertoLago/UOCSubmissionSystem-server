package edu.uoc.allago.UOCSubmissionSystemServer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/css/**").permitAll() // Allow routes to CSS files
                .antMatchers("/create_pool.html").hasRole("ADMIN")
                .antMatchers("/").hasRole("ADMIN")
                .antMatchers("/upload/**").permitAll() // No authentication required
                .antMatchers("/create_pool/**").authenticated() // Require authentication
                .antMatchers("/pools/**").authenticated() // Require authentication
                .antMatchers("/viewFiles/**").authenticated() // Require authentication
                .antMatchers("/download/**").authenticated() // Require authentication
                .antMatchers("/downloadAll/**").authenticated() // Require authentication
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
                .and()
                .logout().permitAll()
                .and()
                .csrf().ignoringAntMatchers("/upload/**","/create_pool/**"); // Ignore CSRF for routes
        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        User.UserBuilder users = User.builder().passwordEncoder(encoder::encode);
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        manager.createUser(users
                .username("admin")
                .password("uoc31416")
                .roles("ADMIN")
                .build());
        return manager;
    }
}