package com.software.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    // Constructor injection for jwtAuthenticationFilter and userDetailsService
    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthenticationFilter, UserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
    }

    // SecurityFilterChain Bean
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // CSRF'yi devre dışı bırakıyoruz
                .authorizeHttpRequests(auth -> auth  // 'authorizeRequests()' yerine 'authorizeHttpRequests()' kullanıldı
                        .requestMatchers("/auth/**").permitAll()  // "/auth/**" yoluna izin veriyoruz
                        .anyRequest().authenticated()  // Diğer tüm yollar için kimlik doğrulama gerekli
                )
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless oturum yönetimi
                .and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT doğrulama için filtre ekliyoruz

        return http.build();  // Yeni Spring Security sürümlerinde bu gerekli
    }

    // AuthenticationManager Bean
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())  // AuthenticationProvider'ı buraya bağlıyoruz
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();  // AuthenticationManager'ı oluşturan kod
    }

    // AuthenticationProvider Bean
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService); // UserDetailsService'i set ediyoruz
        provider.setPasswordEncoder(passwordEncoder());    // Şifreyi BCrypt ile encode ediyoruz
        return provider;
    }

    // PasswordEncoder Bean: BCrypt ile şifrelerin hashlenmesi sağlanır
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Şifreleri bcrypt ile hashlemek için
    }
}
