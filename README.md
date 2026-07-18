# WeHear Backend

> Backend API của **WeHear** - nền tảng hỗ trợ học tập và giao tiếp bằng Ngôn ngữ ký hiệu Việt Nam.

🌐 Website: https://www.wehear.today

---

## 📌 Giới thiệu

WeHear Backend cung cấp REST API cho các chức năng xác thực, từ điển ký hiệu, bài học, quiz, cộng đồng, tin tức, đóng góp từ điển, quản trị hệ thống và các dịch vụ tích hợp như Cloudinary, Gemini AI, Google Cloud TTS và email.

Repository này chứa mã nguồn backend của hệ thống, được xây dựng bằng Spring Boot và kết nối với cơ sở dữ liệu MySQL.

---

## ✨ Tính năng

- 🔐 Đăng ký, đăng nhập, xác thực JWT và phân quyền người dùng
- 👤 Quản lý hồ sơ cá nhân và thông tin tài khoản
- 🔎 Cung cấp API tra cứu từ điển Ngôn ngữ ký hiệu Việt Nam
- 🎬 Quản lý media minh họa cho từ vựng ký hiệu
- 📚 Quản lý bài học theo chủ đề, cấp độ và khu vực
- 🧠 Quản lý quiz và câu hỏi kiểm tra sau bài học
- 💬 Cung cấp API cộng đồng: bài viết, bình luận, tương tác và báo cáo
- 📰 Quản lý tin tức, nguồn RSS và bài viết liên quan
- 📤 Upload ảnh/video qua Cloudinary
- 🤝 Tiếp nhận và xử lý đóng góp từ điển của người dùng
- 🗣️ Tích hợp Text-to-Speech cho nội dung tiếng Việt
- 🧩 Lưu dữ liệu hiệu chỉnh bản dịch/nhận diện VSL
- ⚙️ Trang quản trị: người dùng, bài học, quiz, từ điển, tin tức, cộng đồng và đóng góp

---

## 🛠️ Công nghệ

- ☕ **Java 17**
- 🍃 **Spring Boot 3.4.1**
- 🔒 **Spring Security**
- 🗄️ **Spring JDBC**
- 🐬 **MySQL**
- 🔑 **JWT**
- 🧰 **Lombok**
- ☁️ **Cloudinary**
- 🤖 **Gemini AI**
- 🗣️ **Google Cloud Text-to-Speech**
- 📩 **Resend Mail API**
- 📰 **Rome RSS**
- 🐳 **Docker**

---

## ⚙️ Cấu hình môi trường

Tạo file `.env` từ file mẫu:

```bash
cp .env.example .env
```

Các nhóm biến môi trường chính:

```env
DB_URL=
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=

CLOUDINARY_CLOUD_NAME=
CLOUDINARY_API_KEY=
CLOUDINARY_API_SECRET=

RESEND_API_KEY=
RESEND_FROM_EMAIL=

GOOGLE_AI_API_KEY=
GOOGLE_CLOUD_TTS_API_KEY=

FRONTEND_URL=
BACKEND_URL=
PORT=
```

---

## 🗄️ Cơ sở dữ liệu

Schema cơ sở dữ liệu nằm tại:

```text
db/schema.sql
```

Một số script bổ sung:

```text
db/migrate_to_cloudinary.sql
db/vsl_translation_corrections.sql
```

---

## 🚀 Chạy dự án

Trên Windows:

```bash
mvnw.cmd spring-boot:run
```

Trên macOS/Linux:

```bash
./mvnw spring-boot:run
```

Backend chạy theo `PORT` trong file `.env`. Nếu không cấu hình riêng, môi trường local thường dùng:

```text
http://localhost:8080
```

---

## 📦 Build

```bash
mvnw.cmd clean package
```

File build được tạo trong:

```text
target/
```

---

## 🚢 Triển khai

Backend có thể triển khai bằng Docker/Railway với file:

```text
Dockerfile
```
