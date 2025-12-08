package com.github.jimtrung.theater.model;

public enum MovieGenre {
    action,
    adventure,
    comedy,
    drama,
    romance,
    horror,
    thriller,
    mystery,
    science_fiction,
    fantasy,
    animation,
    family,
    musical,
    documentary,
    crime,
    war,
    western,
    historical,
    sports,
    biography;

  public String toVietnamese() {
    return switch (this) {
      case action -> "Hành động";
      case adventure -> "Phiêu lưu";
      case comedy -> "Hài";
      case drama -> "Chính kịch";
      case romance -> "Lãng mạn";
      case horror -> "Kinh dị";
      case thriller -> "Giật gân";
      case mystery -> "Bí ẩn";
      case science_fiction -> "Khoa học viễn tưởng";
      case fantasy -> "Giả tưởng";
      case animation -> "Hoạt hình";
      case family -> "Gia đình";
      case musical -> "Nhạc kịch";
      case documentary -> "Tài liệu";
      case crime -> "Tội phạm";
      case war -> "Chiến tranh";
      case western -> "Miền Tây";
      case historical -> "Lịch sử";
      case sports -> "Thể thao";
      case biography -> "Tiểu sử";
    };
  }

  public static MovieGenre fromVietnamese(String text) {
    if (text == null) return action;
    return switch (text.trim()) {
      case "Hành động" -> action;
      case "Phiêu lưu" -> adventure;
      case "Hài" -> comedy;
      case "Chính kịch" -> drama;
      case "Lãng mạn" -> romance;
      case "Kinh dị" -> horror;
      case "Giật gân" -> thriller;
      case "Bí ẩn" -> mystery;
      case "Khoa học viễn tưởng" -> science_fiction;
      case "Giả tưởng" -> fantasy;
      case "Hoạt hình" -> animation;
      case "Gia đình" -> family;
      case "Nhạc kịch" -> musical;
      case "Tài liệu" -> documentary;
      case "Tội phạm" -> crime;
      case "Chiến tranh" -> war;
      case "Miền Tây" -> western;
      case "Lịch sử" -> historical;
      case "Thể thao" -> sports;
      case "Tiểu sử" -> biography;
      default -> action;
    };
  }
}
