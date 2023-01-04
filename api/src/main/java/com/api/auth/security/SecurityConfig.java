package com.api.auth.security;

import com.api.auth.evaluator.FixedTokenPermissionEvaluator;
import com.api.auth.security.details.service.ManagerDetailsService;
import com.api.auth.security.details.service.ShopDetailsService;
import com.api.auth.security.details.service.TpeDetailsService;
import com.api.auth.security.providers.ManagerAuthenticationProvider;
import com.api.auth.security.providers.ShopAuthenticationProvider;
import com.api.auth.security.providers.TpeAuthenticationProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
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
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(value = "com.api.auth")
public class SecurityConfig extends GlobalMethodSecurityConfiguration {

    private final JWTFilter filter;
    private final ManagerDetailsService mds;
    private final TpeDetailsService tds;
    private final ShopDetailsService sds;
    @Autowired
    private FixedTokenPermissionEvaluator fixedTokenPermissionEvaluator;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(fixedTokenPermissionEvaluator);
        return expressionHandler;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Main security filter chain, with restricted access to all endpoints except for
     * /auth and /download, but also admin interfaces (permission is checked manually by javascript).
     *
     * @param authenticationManager uses the ProviderManager to authenticate the user; declared below
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, ProviderManager authenticationManager) throws Exception {
        http.csrf().disable()
            .httpBasic().disable()
            .cors()
            .and()
            .authorizeHttpRequests()
            // Endpoints
            .antMatchers("/auth/**").permitAll()
            .antMatchers("/bank/**").hasRole("MANAGER")
            .antMatchers("/qr-code/**").hasRole("MANAGER")
            // Endpoints to get Redis data
            .antMatchers("/tpe-manager-redis/**").hasRole("MANAGER")
            .antMatchers("/transaction-request-redis/**").hasRole("MANAGER")
            // Endpoints to connect to websockets
            .antMatchers("/websocket-manager/tpe/**").hasRole("TPE")
            .antMatchers("/websocket-manager/shop/**").hasRole("SHOP")
            // Interfaces for downloading apps
            .antMatchers("/download/tpe.apk").permitAll()
            .antMatchers("/download/shop.apk").permitAll()
            // Interfaces for Admin
            .antMatchers("/admin/login").permitAll()
            .antMatchers("/admin/dashboard").permitAll()
            .antMatchers("/admin/qr-code/**").permitAll()
            .antMatchers("/admin/whitelist/**").permitAll()
            //.anyRequest().hasRole("MANAGER")
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

    /**
     * Gathers all the authentication providers
     * @return ProviderManager which manages all the authentication providers
     */
    @Bean
    public ProviderManager authenticationManager() {
        return new ProviderManager(
            managerAuthenticationProvider(),
            tpeAuthenticationProvider(),
            shopAuthenticationProvider()
        );
    }

    /**
     * Manager Authentication Provider, manages how Manager is authenticated
     * @return ManagerAuthenticationProvider
     */
    @Bean
    public ManagerAuthenticationProvider managerAuthenticationProvider() {
        ManagerAuthenticationProvider managerAuthenticationProvider = new ManagerAuthenticationProvider();
        managerAuthenticationProvider.setUserDetailsService(mds);
        managerAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return managerAuthenticationProvider;
    }

    /**
     * Tpe Authentication Provider, manages how Tpe is authenticated
     * @return TpeAuthenticationProvider
     */
    @Bean
    public TpeAuthenticationProvider tpeAuthenticationProvider() {
        TpeAuthenticationProvider tpeAuthenticationProvider = new TpeAuthenticationProvider();
        tpeAuthenticationProvider.setUserDetailsService(tds);
        tpeAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return tpeAuthenticationProvider;
    }

    /**
     * Shop Authentication Provider, manages how Shop is authenticated
     * @return ShopAuthenticationProvider
     */
    @Bean
    public ShopAuthenticationProvider shopAuthenticationProvider() {
        ShopAuthenticationProvider shopAuthenticationProvider = new ShopAuthenticationProvider();
        shopAuthenticationProvider.setUserDetailsService(sds);
        shopAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return shopAuthenticationProvider;
    }
}
