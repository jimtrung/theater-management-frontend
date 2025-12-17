# Hệ Thống Quản Lý Rạp Chiếu Phim (Frontend)

[English](README.md)

Một hệ thống quản lý và đặt vé xem phim hiện đại, toàn diện được xây dựng bằng JavaFX. Ứng dụng mang đến trải nghiệm mượt mà cho cả khách hàng đặt vé và quản trị viên vận hành rạp.

## Tính Năng

### 🎬 Dành Cho Khách Hàng
-   **Xác Thực Người Dùng**: Đăng ký và Đăng nhập an toàn với tính năng kiểm tra dữ liệu theo thời gian thực.
-   **Duyệt Phim**: Xem danh sách phim "Đang Chiếu" với thông tin chi tiết bao gồm nội dung, diễn viên, đạo diễn, thời lượng và đánh giá.
-   **Chọn Suất Chiếu**: Lọc suất chiếu theo ngày và giờ.
-   **Đặt Vé Tương Tác**:
    -   Chọn ghế trực quan trên sơ đồ (Ghế Thường và VIP).
    -   Cập nhật trạng thái ghế theo thời gian thực (Trống, Đã đặt).
-   **Quản Lý Vé**: Xem lịch sử đặt vé và chi tiết vé đã đặt.
-   **Quản Lý Hồ Sơ**: Cập nhật thông tin cá nhân.
-   **Tin Tức & Sự Kiện**: Cập nhật tin tức điện ảnh và khuyến mãi mới nhất.

### 🛠️ Dành Cho Quản Trị Viên (Admin)
-   **Bảng Điều Khiển (Dashboard)**: Trung tâm quản lý tập trung.
-   **Quản Lý Phim**: Thêm, sửa, xóa phim.
-   **Quản Lý Phòng Chiếu**: Quản lý các phòng chiếu với sức chứa và loại phòng khác nhau.
-   **Quản Lý Suất Chiếu**: Lên lịch chiếu phim cho từng phòng và khung giờ cụ thể.
-   **Thống Kê**: Xem các chỉ số hoạt động quan trọng.

## Công Nghệ Sử Dụng

-   **Ngôn Ngữ**: Java 24
-   **GUI Framework**: JavaFX 25
-   **Công Cụ Build**: Maven
-   **Xử Lý Dữ Liệu**: Jackson (JSON)
-   **Giao Diện**: CSS (Tùy chỉnh giao diện hiện đại)
-   **Networking**: Java `HttpClient` để giao tiếp với Backend RESTful.

## Yêu Cầu Hệ Thống

-   Java Development Kit (JDK) 24 trở lên.
-   Maven 3.6.0 trở lên.
-   Backend **Theater Management Backend** đang chạy (mặc định tại `http://localhost:8080`).

## Cài Đặt & Chạy Ứng Dụng

1.  **Clone Repository**
    ```bash
    git clone https://github.com/jimtrung/theater-management-frontend.git
    cd theater-management-frontend
    ```

2.  **Cấu Hình**
    -   Đảm bảo file `.env` hoặc các biến môi trường được cấu hình đúng (ví dụ: URL API Backend).
    -   URL Backend mặc định: `http://localhost:8080`

3.  **Build Dự Án**
    ```bash
    mvn clean install
    ```

4.  **Chạy Ứng Dụng**
    ```bash
    mvn javafx:run
    ```

## Cấu Trúc Dự Án

-   `src/main/resources/fxml`: Chứa các file giao diện FXML (User và Admin).
-   `src/main/resources/styles`: Các file định kiểu CSS (`styles.css`).
-   `src/main/java/com/github/jimtrung/theater`:
    -   `model`: Các thực thể dữ liệu (Movie, User, Ticket,...).
    -   `view`: Các lớp Controller điều khiển giao diện.
    -   `service`: Lớp dịch vụ để giao tiếp API.
    -   `util`: Các tiện ích hỗ trợ (AlertHelper, AuthTokenUtil,...).

## Tác Giả

-   **Jim Trung** - *Khởi tạo và phát triển*

## Giấy Phép

Dự án này được cấp phép theo Giấy phép MIT - xem file [LICENSE](LICENSE) để biết thêm chi tiết.
