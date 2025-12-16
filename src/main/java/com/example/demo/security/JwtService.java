package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Сервис для работы с JSON Web Tokens (JWT).
 * <p>
 * Этот сервис предоставляет функционал для создания, валидации и обработки JWT токенов,
 * используемых для аутентификации и авторизации в приложении.
 * <p>
 * Токены подписываются с использованием алгоритма HMAC-SHA, что обеспечивает целостность данных.
 * Срок действия токенов ограничен для повышения безопасности.
 *
 * <p><b>Основные функции:</b></p>
 * <ul>
 *   <li>Генерация JWT токенов на основе данных пользователя</li>
 *   <li>Извлечение информации из токенов (имя пользователя)</li>
 *   <li>Валидация токенов по имени пользователя и сроку действия</li>
 * </ul>
 *
 * <p><b>Конфигурация:</b></p>
 * <p>Параметры работы сервиса настраиваются через application.properties/application.yml:</p>
 * <pre>
 * testing.app.secret=ваш-секретный-ключ-для-подписи-токенов
 * testing.app.lifetime=86400000 # время жизни токена в миллисекундах (например, 24 часа)
 * </pre>
 *
 * <p><b>Используемые библиотеки:</b></p>
 * <ul>
 *   <li>io.jsonwebtoken:jjwt-api для работы с JWT</li>
 *   <li>io.jsonwebtoken:jjwt-impl для реализации</li>
 *   <li>io.jsonwebtoken:jjwt-jackson для сериализации</li>
 * </ul>
 *
 * @see org.springframework.security.core.userdetails.UserDetails
 * @see io.jsonwebtoken.Jwts
 * @see io.jsonwebtoken.security.Keys
 *
 * @author [Ваше имя или команда]
 * @version 1.0
 * @since [версия приложения]
 */
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtService {
    /**
     * Секретный ключ для подписи JWT токенов.
     * <p>
     * Должен быть достаточно длинным и сложным для обеспечения безопасности.
     * Инжектируется из конфигурации приложения.
     */
    String secret;

    /**
     * Время жизни токена в миллисекундах.
     * <p>
     * Определяет, как долго токен будет действителен после создания.
     * Инжектируется из конфигурации приложения.
     */
    Long expiration;

    /**
     * Конструктор сервиса для работы с JWT.
     * <p>
     * Принимает параметры конфигурации, необходимые для генерации и валидации токенов.
     *
     * @param secret секретный ключ для подписи токенов, полученный из конфигурации
     *               приложения через ${testing.app.secret}
     * @param expiration время жизни токена в миллисекундах, полученное из конфигурации
     *                   приложения через ${testing.app.lifetime}
     * @throws IllegalArgumentException если secret пустой или null, либо expiration
     *                                  имеет некорректное значение
     *
     * @see org.springframework.beans.factory.annotation.Value
     */
    public JwtService(@Value("${testing.app.secret}") String secret,
                      @Value("${testing.app.lifetime}") Long expiration) {
        this.secret = secret;
        this.expiration = expiration;
    }

    /**
     * Генерирует JWT токен для указанного пользователя.
     * <p>
     * Создает токен со следующими claims (утверждениями):
     * <ul>
     *   <li>subject (sub) - имя пользователя (username)</li>
     *   <li>issuedAt (iat) - время создания токена (текущее время)</li>
     *   <li>expiration (exp) - время истечения токена (текущее время + expiration)</li>
     * </ul>
     * <p>
     * Токен подписывается с использованием HMAC-SHA алгоритма с секретным ключом.
     *
     * @param user данные пользователя, для которого генерируется токен
     * @return строковое представление JWT токена в формате Base64Url
     * @throws IllegalArgumentException если user или user.getUsername() равен null
     * @throws io.jsonwebtoken.security.SecurityException при ошибках подписи токена
     *
     * @see org.springframework.security.core.userdetails.UserDetails#getUsername()
     * @see io.jsonwebtoken.Jwts#builder()
     */
    public String generateToken(UserDetails user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    /**
     * Извлекает имя пользователя из JWT токена.
     * <p>
     * Парсит токен и возвращает значение subject (sub) claim.
     *
     * @param token JWT токен в формате Base64Url
     * @return имя пользователя, содержащееся в токене
     * @throws io.jsonwebtoken.ExpiredJwtException если токен просрочен
     * @throws io.jsonwebtoken.security.SignatureException если подпись токена недействительна
     * @throws io.jsonwebtoken.MalformedJwtException если токен имеет неверный формат
     * @throws IllegalArgumentException если token равен null или пустой
     *
     * @see #getClaims(String)
     */
    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    /**
     * Проверяет валидность JWT токена для указанного пользователя.
     * <p>
     * Выполняет две проверки:
     * <ol>
     *   <li>Имя пользователя в токене совпадает с именем пользователя в UserDetails</li>
     *   <li>Срок действия токена не истек</li>
     * </ol>
     *
     * @param token JWT токен для проверки
     * @param user данные пользователя для сравнения
     * @return true если токен действителен для указанного пользователя, false в противном случае
     * @throws io.jsonwebtoken.ExpiredJwtException если токен просрочен
     * @throws io.jsonwebtoken.security.SignatureException если подпись токена недействительна
     * @throws io.jsonwebtoken.MalformedJwtException если токен имеет неверный формат
     *
     * @see #extractUsername(String)
     * @see #getClaims(String)
     */
    public boolean validateToken(String token, UserDetails user) {
        return extractUsername(token).equals(user.getUsername())
                && !getClaims(token).getExpiration().before(new Date());
    }

    /**
     * Извлекает все claims (утверждения) из JWT токена.
     * <p>
     * Этот метод парсит токен, проверяет его подпись и возвращает все содержащиеся в нем claims.
     * Является приватным вспомогательным методом для внутреннего использования в классе.
     *
     * @param token JWT токен для парсинга
     * @return объект Claims, содержащий все утверждения из токена
     * @throws io.jsonwebtoken.ExpiredJwtException если токен просрочен
     * @throws io.jsonwebtoken.security.SignatureException если подпись токена недействительна
     * @throws io.jsonwebtoken.MalformedJwtException если токен имеет неверный формат
     * @throws IllegalArgumentException если token равен null или пустой
     *
     * @see io.jsonwebtoken.Claims
     * @see io.jsonwebtoken.Jwts#parserBuilder()
     */
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
