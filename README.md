# Banking Application

This project is a full-stack application that includes basic banking operations such as account management, money transfers, and transaction history. The project consists of both frontend and backend layers and uses a PostgreSQL database.


---

## 🔧 Teknolojiler

### Backend

- Java 17 (Spring Boot)
- Spring Security, Spring Data JPA
- PostgreSQL
- Maven

### Frontend

- React (Vite)
- Context API
- Ant Design
- Axios
- SCSS

---

### Frontend Kurulum ve Çalıştırma

```bash
npm install
npm run dev
```

## 🐳 Docker ile Veritabanı Başlatma

İsteğe bağlı olarak PostgreSQL veritabanını Docker ile başlatabilirsiniz:

```bash
docker-compose -f docker-compose.dev.yml up -d postgres_db
```
