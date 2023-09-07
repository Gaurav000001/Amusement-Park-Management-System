package com.amusement.securityConf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.amusement.DTO.CustomerDTO;
import com.amusement.exception.CustomerException;
import com.amusement.service.CustomerService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSecurity
public class AppConfig {

    @Bean
    public SecurityFilterChain springSecurityConfiguration(HttpSecurity http) throws Exception {

        http
                .authorizeHttpRequests(auth -> {
                    auth
		                    .requestMatchers("/swagger-ui*/**","/v3/api-docs/**").permitAll()                        
		                    .requestMatchers(HttpMethod.POST, "/customers/").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/customers/{customerId}/").hasRole("USER")
                            .anyRequest().authenticated();
                })
                .addFilterBefore(new JwtTokenValidationFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(new JwtTokenGeneratorFilter(), BasicAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public UserDetailsService customUserDetailsService() {

        return new UserDetailsService() {

            CustomerService customerService;

            @Autowired
            public void setCustomerService(CustomerService customerService) {
                this.customerService = customerService;
            }

            @Override
            public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

                CustomerDTO customerDTO = null;

                try {
                    customerDTO = customerService.getCustomerByEmail(email);
                } catch (CustomerException e) {
                    throw new UsernameNotFoundException(e.getMessage());
                }

                return User.builder()
                        .username(customerDTO.getEmail())
                        .password(customerDTO.getPassword())
                        .roles("USER")
                        .build();
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

}
