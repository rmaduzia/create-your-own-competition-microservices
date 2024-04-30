package pl.createcompetition.microserviceschanges;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import pl.createcompetition.microserviceschanges.JwtAuthConverter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true,
    jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthConverter customJwtAuthenticationConverter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/prometheus", "/actuator/health/**",
                    "/swagger-ui", "/swagger-ui/**", "/error", "/v3/api-docs/**").permitAll()
                .requestMatchers("/storefront/**").permitAll()
                .requestMatchers("/keycloak/user/create/**").permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(customJwtAuthenticationConverter)
                )
            )
            .build();
    }


//    protected void configure(HttpSecurity http) throws Exception {

//        http
//        .cors()
//        .and()
//        .sessionManagement()
//        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//        .and()
//        .csrf()
//        .disable()
//        .formLogin()
//        .disable()
//        .httpBasic()
//        .disable()
//        .exceptionHandling()
//        .authenticationEntryPoint(new RestAuthenticationEntryPoint())
//        .and()
//        .authorizeRequests()
//        .antMatchers("/",
//        "/error",
//        "/favicon.ico",
//        "/**/*.png",
//        "/**/*.gif",
//        "/**/*.svg",
//        "/**/*.jpg",
//        "/**/*.html",
//        "/**/*.css",
//        "/**/*.js")
//        .permitAll()
//        .antMatchers("/auth/**", "/oauth2/**")
//        .permitAll()
//        .anyRequest()
//        .authenticated()
//        .and()
//        .oauth2Login()
//        .authorizationEndpoint()
//        .baseUri("/oauth2/authorize")
//        .and()
//        .redirectionEndpoint()
//        .baseUri("/oauth2/callback/*")
//        .and()
//        .userInfoEndpoint();
//    }


    private static final String[] AUTH_WHITELIST = {
        "/swagger-resources/**",
        "/swagger-ui.html/",
        "/v2/api-docs",
        "/webjars/**",
        "swagger-ui.html#/",
        "/swagger-ui/**",

    };

//    @Override
//    public void configure(WebSecurity web) {
//        web.ignoring().antMatchers(AUTH_WHITELIST);
//    }


    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {

        return (web) -> {
            web.ignoring().requestMatchers(
                HttpMethod.POST,
                "/keycloak/user/create/**");

            web.ignoring().requestMatchers(HttpMethod.PUT, "keycloak/user/*/forgot-password");
            web.ignoring().requestMatchers(HttpMethod.POST, "keycloak/user/login/**");
            web.ignoring().requestMatchers(HttpMethod.POST, "/keycloak/user/create/**");


//            web.ignoring().requestMatchers(
//                    HttpMethod.OPTIONS,
//                    "/**"
//                )
//                .requestMatchers("/v3/api-docs/**", "/configuration/**", "/swagger-ui/**",
//                    "/swagger-resources/**", "/swagger-ui.html", "/webjars/**", "/api-docs/**");

        };
    }

}