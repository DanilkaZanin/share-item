package com.example.demo.config;


import com.example.demo.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Конфигурационный класс безопасности Spring Security.
 * <p>
 * Этот класс определяет конфигурацию безопасности для приложения, включая:
 * <ul>
 *     <li>Настройку цепочки фильтров безопасности</li>
 *     <li>Конфигурацию аутентификации и авторизации</li>
 *     <li>Управление сессиями и CSRF защитой</li>
 *     <li>Регистрацию кастомных фильтров аутентификации</li>
 * </ul>
 *
 * <p>Аннотация {@code @EnableWebSecurity} включает поддержку безопасности Spring Security
 * и позволяет настраивать безопасность через компоненты Spring.</p>
 *
 * <p>Аннотация {@code @Configuration} указывает, что класс содержит конфигурацию Spring.</p>
 *
 * <p>Аннотация {@code @RequiredArgsConstructor} генерирует конструктор для всех final полей,
 * что обеспечивает инъекцию зависимостей через конструктор.</p>
 *
 * @see org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
 * @see org.springframework.context.annotation.Configuration
 * @see lombok.RequiredArgsConstructor
 *
 * @author [Zanin Danila]
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * Кастомный фильтр для JWT аутентификации.
     * <p>
     * Этот фильтр обрабатывает JWT токены в заголовках Authorization
     * и устанавливает контекст аутентификации для авторизованных пользователей.
     */
    private final JwtAuthFilter jwtAuthFilter;

    /**
     * Определяет цепочку фильтров безопасности для HTTP запросов.
     * <p>
     * Этот метод конфигурирует:
     * <ul>
     *     <li><b>CSRF защиту</b>: отключена, так как используется stateless JWT аутентификация</li>
     *     <li><b>Управление сессиями</b>: установлена политика STATELESS для отсутствия серверных сессий</li>
     *     <li><b>Авторизация запросов</b>: настраивает доступ к эндпоинтам</li>
     *     <li><b>Кастомные фильтры</b>: добавляет JWT фильтр перед стандартным фильтром аутентификации</li>
     * </ul>
     *
     * <p><b>Маршрутизация:</b>
     * <ul>
     *     <li>{@code /auth/**} - публичные эндпоинты аутентификации (логин, регистрация, refresh токен)</li>
     *     <li>{@code anyRequest().authenticated()} - все остальные запросы требуют аутентификации</li>
     * </ul>
     *
     * @param http объект {@link HttpSecurity} для настройки безопасности
     * @return настроенная цепочка фильтров безопасности {@link SecurityFilterChain}
     * @throws Exception если произошла ошибка при конфигурации безопасности
     *
     * @see org.springframework.security.web.SecurityFilterChain
     * @see org.springframework.security.config.annotation.web.builders.HttpSecurity
     * @see JwtAuthFilter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
