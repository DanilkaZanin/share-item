package com.example.demo.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Фильтр аутентификации на основе JWT (JSON Web Token).
 * <p>
 * Этот фильтр выполняет проверку JWT токенов для каждого входящего HTTP запроса.
 * Извлекает токен из заголовка Authorization, валидирует его и устанавливает
 * контекст аутентификации в {@link SecurityContextHolder}, если токен действителен.
 *
 * <p>Фильтр наследуется от {@link OncePerRequestFilter}, что гарантирует его однократное
 * выполнение для каждого HTTP запроса в цепочке фильтров.</p>
 *
 * <p>Аннотация {@code @Component} регистрирует этот класс как Spring bean,
 * что позволяет использовать его в цепочке фильтров безопасности.</p>
 *
 * <p>Аннотация {@code @RequiredArgsConstructor} генерирует конструктор для инъекции зависимостей.</p>
 *
 * <p><b>Работа фильтра:</b>
 * <ol>
 *   <li>Извлекает заголовок Authorization</li>
 *   <li>Проверяет наличие префикса "Bearer "</li>
 *   <li>Извлекает и валидирует JWT токен</li>
 *   <li>Загружает данные пользователя по имени пользователя из токена</li>
 *   <li>Устанавливает аутентификацию в контексте безопасности</li>
 * </ol>
 *
 * @see org.springframework.web.filter.OncePerRequestFilter
 * @see org.springframework.security.core.userdetails.UserDetailsService
 * @see JwtService
 * @see org.springframework.security.core.context.SecurityContextHolder
 *
 * @author [Ваше имя или команда]
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    /**
     * Сервис для загрузки данных пользователя.
     * <p>
     * Использует аннотацию {@code @Lazy} для отложенной инициализации,
     * что помогает избежать циклических зависимостей при конфигурации Spring Security.
     */
    private final @Lazy UserDetailsService userDetailsService;

    /**
     * Сервис для работы с JWT токенами.
     * <p>
     * Обеспечивает извлечение данных из токенов и их валидацию.
     */
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain)
            throws IOException, ServletException {

        String header = req.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        String token = header.substring(7);
        String username = jwtService.extractUsername(token);

        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails user = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateToken(token, user)) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                user, null, user.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        chain.doFilter(req, res);
    }
}
