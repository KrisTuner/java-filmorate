package ru.yandex.practicum.filmorate.model;

public enum AgeRating {
    G("G"),
    PG("PG"),
    PG_13("PG-13"),
    R("R"),
    NC_17("NC-17");

    private final String code;

    AgeRating(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AgeRating fromCode(String code) {
        for (AgeRating rating : values()) {
            if (rating.code.equals(code)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Unknown rating code: " + code);
    }
}