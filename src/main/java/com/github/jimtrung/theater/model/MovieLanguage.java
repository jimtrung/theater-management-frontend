package com.github.jimtrung.theater.model;

public enum MovieLanguage {
    vietnamese,
    english,
    korean,
    japanese,
    chinese,
    hindi,
    french,
    spanish,
    thai,
    other;

    public String toVietnamese() {
        return switch (this) {
            case vietnamese -> "Tiếng Việt";
            case english -> "Tiếng Anh";
            case korean -> "Tiếng Hàn";
            case japanese -> "Tiếng Nhật";
            case chinese -> "Tiếng Trung";
            case hindi -> "Tiếng Hindi";
            case french -> "Tiếng Pháp";
            case spanish -> "Tiếng Tây Ban Nha";
            case thai -> "Tiếng Thái";
            case other -> "Khác";
        };
    }

    public static MovieLanguage fromVietnamese(String text) {
        if (text == null) return other;
        return switch (text.trim()) {
            case "Tiếng Việt" -> vietnamese;
            case "Tiếng Anh" -> english;
            case "Tiếng Hàn" -> korean;
            case "Tiếng Nhật" -> japanese;
            case "Tiếng Trung" -> chinese;
            case "Tiếng Hindi" -> hindi;
            case "Tiếng Pháp" -> french;
            case "Tiếng Tây Ban Nha" -> spanish;
            case "Tiếng Thái" -> thai;
            default -> other;
        };
    }

    public String toString(MovieLanguage object) {
        return object != null ? object.toVietnamese() : "";
    }

    public MovieLanguage fromString(String string) {
        return MovieLanguage.fromVietnamese(string);
    }
}
