package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import java.time.LocalDate;
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
    void userValidation_ShouldFailWhenEmailIsInvalid() {
        User user = new User();
        user.setEmail("invalid-email");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Валидация должна провалиться при неверном email");
        assertEquals(1, violations.size());
        assertEquals("Email должен содержать символ @", violations.iterator().next().getMessage());
    }

    @Test
    void userValidation_ShouldFailWhenLoginContainsSpaces() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("login with spaces");
        user.setBirthday(LocalDate.now().minusYears(20));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Валидация должна провалиться при наличии пробелов в логине");
        assertEquals(1, violations.size());
        assertEquals("Логин не может содержать пробелы", violations.iterator().next().getMessage());
    }

    @Test
    void userValidation_ShouldSetNameAsLoginWhenNameIsEmpty() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("login");
        user.setBirthday(LocalDate.now().minusYears(20));

        // Проверяем, что логин устанавливается как имя, если имя пустое
        assertNull(user.getName());
        user = UserControllerTestHelper.createUserForTest(user);
        assertEquals("login", user.getName());
    }

    @Test
    void userValidation_ShouldFailWhenBirthdayInFuture() {
        User user = new User();
        user.setEmail("valid@email.com");
        user.setLogin("validLogin");
        user.setBirthday(LocalDate.now().plusDays(1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Валидация должна провалиться при дате рождения в будущем");
        assertEquals(1, violations.size());
        assertEquals("Дата рождения не может быть в будущем", violations.iterator().next().getMessage());
    }

    @Test
    void filmValidation_ShouldFailWhenNameIsBlank() {
        Film film = new Film();
        film.setName(" ");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Валидация должна провалиться при пустом названии");
        assertEquals(1, violations.size());
        assertEquals("Название не может быть пустым", violations.iterator().next().getMessage());
    }

    @Test
    void filmValidation_ShouldFailWhenDescriptionIsTooLong() {
        Film film = new Film();
        film.setName("Valid name");
        film.setDescription("a".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Валидация должна провалиться при слишком длинном описании");
        assertEquals(1, violations.size());
        assertEquals("Максимальная длина описания — 200 символов", violations.iterator().next().getMessage());
    }

    @Test
    void filmValidation_ShouldFailWhenReleaseDateIsBeforeCinemaBirthday() {
        Film film = new Film();
        film.setName("Valid name");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        assertThrows(ValidationException.class, () -> FilmControllerTestHelper.validateFilmForTest(film),
                "Должно выброситься исключение при дате релиза раньше 28 декабря 1895 года");
    }

    @Test
    void filmValidation_ShouldFailWhenDurationIsNegative() {
        Film film = new Film();
        film.setName("Valid name");
        film.setDescription("Valid description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(-120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Валидация должна провалиться при отрицательной продолжительности");
        assertEquals(1, violations.size());
        assertEquals("Продолжительность должна быть положительным числом", violations.iterator().next().getMessage());
    }

    @Test
    void filmValidation_ShouldFailWhenReleaseDateIsNull() {
        Film film = new Film();
        film.setName("Valid name");
        film.setDescription("Valid description");
        film.setReleaseDate(null);
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Валидация должна провалиться при null дате релиза");
        assertEquals(1, violations.size());
        assertEquals("Дата релиза обязательна", violations.iterator().next().getMessage());
    }
}