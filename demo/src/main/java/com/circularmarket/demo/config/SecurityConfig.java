package com.circularmarket.demo.config;

import com.circularmarket.demo.service.OAuth2UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   DaoAuthenticationProvider authenticationProvider,
                                                   OAuth2UsuarioService oAuth2UsuarioService) throws Exception {

        http.authenticationProvider(authenticationProvider);

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/login",
                        "/registro",
                        "/inicio",
                        "/buscar",
                        "/productos",
                        "/producto/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/img/**",
                        "/favicon.ico",
                        "/oauth2/**",
                        "/login/oauth2/**"
                ).permitAll()

                .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                .anyRequest().authenticated()
        );

        http.formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/inicio", true)
                .failureUrl("/login?error=1")
                .permitAll()
        );

        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .successHandler(oAuth2UsuarioService)
                .failureUrl("/login?error=google")
        );

        http.logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=1")
                .permitAll()
        );

        http.csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}