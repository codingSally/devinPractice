# Calligraphy Website

A full-stack calligraphy website with Spring Boot backend and Vue.js frontend.

## Features

- User authentication with JWT tokens
- Product management (add, edit, delete products)
- Product display with filtering by category and price
- Role-based access control (admin vs regular users)

## Project Structure

- `/devin` - Spring Boot backend
- `/calligraphy-frontend` - Vue.js frontend

## Running Locally

### Backend

```bash
cd devin
mvn spring-boot:run
```

The backend will start on port 8081.

### Frontend

```bash
cd calligraphy-frontend
npm install
npm run dev
```

The frontend will start on port 5174 (or another available port).

## Default Credentials

- Admin User:
  - Username: admin
  - Password: admin123

## Environment Variables for Production

For production deployment, set the following environment variables:

```bash
# JWT Configuration
export JWT_SECRET=your_secure_jwt_secret_here

# Admin User Configuration
export ADMIN_PASSWORD_HASH=your_bcrypt_password_hash_here
```

You can generate a BCrypt password hash using online tools or with Spring Security's BCryptPasswordEncoder.

## Security Considerations

- The development version uses hardcoded secrets for ease of setup
- For production, always use environment variables for sensitive information
- Consider using Spring profiles to separate development and production configurations
- Implement proper secret management using tools like HashiCorp Vault or AWS Secrets Manager

## API Endpoints

### Authentication

- `POST /api/auth/login` - Login with username and password
- `POST /api/auth/register` - Register a new user

### Products

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/category/{category}` - Get products by category
- `GET /api/products/price?min={min}&max={max}` - Get products by price range
- `POST /api/products` - Add a new product (admin only)
- `PUT /api/products/{id}` - Update a product (admin only)
- `DELETE /api/products/{id}` - Delete a product (admin only)
