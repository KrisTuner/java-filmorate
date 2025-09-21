package ru.yandex.practicum.filmorate.model;

public enum Genre {
    COMEDY("Комедия"),
    DRAMA("Драма"),
    CARTOON("Мультфильм"),
    THRILLER("Триллер"),
    DOCUMENTARY("Документальный"),
    ACTION("Боевик");

    private final String name;

    Genre(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Genre fromName(String name) {
        for (Genre genre : values()) {
            if (genre.name.equals(name)) {
                return genre;
            }
        }
        throw new IllegalArgumentException("Unknown genre name: " + name);
    }
}