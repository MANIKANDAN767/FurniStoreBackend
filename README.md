# FurniStore Backend

Spring Boot 3.5.4 + Java 17 + MySQL backend for the FurniStore React/Vite frontend.

## 1. Requirements

- Java 17+
- Maven 3.9+
- MySQL 8+

## 2. Configure MySQL

Open `src/main/resources/application.properties` and change:

`spring.datasource.password=YOUR_MYSQL_PASSWORD`

The JDBC URL creates the `furnistore` database automatically if the MySQL user has permission.

## 3. Run

From this folder:

```bash
mvn clean spring-boot:run
```

Or in Eclipse/STS: Run `FurniStoreBackendApplication.java` as a Spring Boot App.

## 4. APIs

### Categories

- `GET http://localhost:8080/api/categories`
- `GET http://localhost:8080/api/categories/sofas`
- `POST http://localhost:8080/api/categories`
- `PUT http://localhost:8080/api/categories/{id}`
- `DELETE http://localhost:8080/api/categories/{id}`

### Products

- `GET http://localhost:8080/api/products`
- `GET http://localhost:8080/api/products/p1`
- `GET http://localhost:8080/api/products?category=sofas`
- `GET http://localhost:8080/api/products?minPrice=700&maxPrice=2000`
- `GET http://localhost:8080/api/products?search=sofa`
- `POST http://localhost:8080/api/products`
- `PUT http://localhost:8080/api/products/{id}`
- `DELETE http://localhost:8080/api/products/{id}`

The first startup seeds the four categories and the eight products from the current React `products.js`.

## 5. Example POST product

```json
{
  "id": "p9",
  "sku": "FS-TB-020",
  "name": "Example Table",
  "categoryId": "tables",
  "material": "Oak",
  "color": "Natural",
  "price": 1200,
  "compareAt": 1400,
  "rating": 4.5,
  "reviews": 0,
  "tag": "New",
  "images": ["https://example.com/table.jpg"],
  "description": "Example product",
  "specs": {
    "Dimensions": "60 W x 30 D x 30 H",
    "Material": "Solid oak"
  },
  "stock": 10
}
```

## 6. Frontend

CORS is configured for `http://localhost:5173`.
Do not modify the React localStorage store yet. First verify these APIs in Postman. Then connect the React app to them in a separate frontend step.
