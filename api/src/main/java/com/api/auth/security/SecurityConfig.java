package com.api.auth.security;

import com.api.auth.security.details.service.ManagerDetailsService;
import com.api.auth.security.details.service.TpeDetailsService;
import com.api.auth.security.providers.ManagerAuthenticationProvider;
import com.api.auth.security.providers.TpeAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTFilter filter;
    private final ManagerDetailsService mds;
    private final TpeDetailsService tds;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ProviderManager authenticationManager) throws Exception {
        http.csrf().disable()
            .httpBasic().disable()
            .cors()
            .and()
            .authorizeHttpRequests()
            .antMatchers("/auth/**").permitAll()
            .antMatchers("/bank/tpe/**").hasRole("MANAGER")
            .antMatchers("/tpe-manager/**").hasRole("TPE")
            .and()
            .authenticationManager(authenticationManager)
            .exceptionHandling()
            .authenticationEntryPoint(
                (request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
            )
            .and()
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public ProviderManager authenticationManager() {
        return new ProviderManager(managerAuthenticationProvider(), tpeAuthenticationProvider());
    }

    @Bean
    public ManagerAuthenticationProvider managerAuthenticationProvider() {
        ManagerAuthenticationProvider managerAuthenticationProvider = new ManagerAuthenticationProvider();
        managerAuthenticationProvider.setUserDetailsService(mds);
        managerAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return managerAuthenticationProvider;
    }

    @Bean
    public TpeAuthenticationProvider tpeAuthenticationProvider() {
        TpeAuthenticationProvider tpeAuthenticationProvider = new TpeAuthenticationProvider();
        tpeAuthenticationProvider.setUserDetailsService(tds);
        tpeAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return tpeAuthenticationProvider;
    }
}
