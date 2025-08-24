package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ModelValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setup() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void user_InvalidEmail_ShouldFail() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Email должен содержать символ @", violations.iterator().next().getMessage());
    }

    @Test
    void user_LoginWithSpaces_ShouldFail() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("login with spaces");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Логин не может содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    void user_EmptyLogin_ShouldFail() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());

        Optional<ConstraintViolation<User>> loginError = violations.stream()
                .filter(v -> v.getPropertyPath().toString().equals("login"))
                .findFirst();

        assertTrue(loginError.isPresent(), "Должна быть ошибка валидации для поля login");
    }

    @Test
    void user_NullBirthday_ShouldFail() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("validLogin");
        user.setBirthday(null);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Дата рождения обязательна", violations.iterator().next().getMessage());
    }

    @Test
    void user_FutureBirthday_ShouldFail() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void film_BlankName_ShouldFail() {
        Film film = new Film();
        film.setName("   ");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void film_DescriptionTooLong_ShouldFail() {
        Film film = new Film();
        film.setName("Valid name");
        film.setDescription("a".repeat(201)); // 201 characters
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Максимальная длина описания — 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void film_FutureReleaseDate_ShouldFail() {
        Film film = new Film();
        film.setName("Valid name");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.now().plusDays(1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Дата релиза не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void film_NegativeDuration_ShouldFail() {
        Film film = new Film();
        film.setName("Valid name");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("Продолжительность должна быть положительным числом", violations.iterator().next().getMessage());
    }

    @Test
    void film_ValidData_ShouldPass() {
        Film film = new Film();
        film.setName("Valid name");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void film_EmptyDescription_ShouldPass() {
        Film film = new Film();
        film.setName("Valid name");
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }
}