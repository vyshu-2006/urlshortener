# URL Shortener Microservices

A scalable, distributed URL shortening service built with microservices architecture, featuring Cassandra for storage, Docker orchestration, and a real-time React analytics dashboard.

## 🌟 Features

- **Microservices Architecture**: Modular, independently deployable services
- **High-Performance Storage**: Cassandra database for distributed, scalable data management
- **Docker Orchestration**: Complete containerization for easy deployment and scaling
- **Real-Time Analytics Dashboard**: React-based UI for monitoring URL metrics and statistics
- **RESTful APIs**: Clean, well-designed API endpoints for URL shortening and retrieval
- **Scalability**: Designed to handle high-traffic scenarios with distributed architecture

## 🏗️ Architecture

### Services

The application consists of the following microservices:

- **URL Shortener Service**: Core service for creating and managing shortened URLs
- **Analytics Service**: Tracks and aggregates URL access metrics
- **API Gateway**: Routes requests to appropriate microservices
- **Database Service**: Cassandra-based persistence layer

### Tech Stack

| Component | Technology |
|-----------|------------|
| Backend | Java |
| Frontend | React.js |
| Database | Apache Cassandra |
| Containerization | Docker & Docker Compose |
| Build Tool | Maven/Gradle |
| Query Language | CQL (Cassandra Query Language) |

## 📊 Language Composition

- **Java**: 68.2% (Backend logic)
- **JavaScript**: 15.9% (Frontend & utilities)
- **CSS**: 12.1% (Styling)
- **CQL**: 2.9% (Database schemas)
- **HTML**: 0.9% (Markup)

## 🚀 Getting Started

### Prerequisites

- Docker & Docker Compose
- Java 11+
- Node.js 14+ (for frontend development)
- Apache Cassandra (or use Docker version)

### Quick Start with Docker

```bash
# Clone the repository
git clone https://github.com/vyshu-2006/urlshortener.git
cd urlshortener

# Start all services
docker-compose up -d

# Services will be available at:
# - API Gateway: http://localhost:8080
# - Analytics Dashboard: http://localhost:3000
# - Cassandra: localhost:9042
```

### Local Development

#### Backend Setup

```bash
# Navigate to backend directory
cd backend

# Build the project
mvn clean build

# Run the application
mvn spring-boot:run
```

#### Frontend Setup

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies
npm install

# Start development server
npm start

# The dashboard will be available at http://localhost:3000
```

## 📖 API Documentation

### Shorten URL

**Request:**
```http
POST /api/shorten
Content-Type: application/json

{
  "originalUrl": "https://www.example.com/very/long/url"
}
```

**Response:**
```json
{
  "shortCode": "abc123",
  "shortUrl": "http://short.url/abc123",
  "originalUrl": "https://www.example.com/very/long/url",
  "createdAt": "2025-05-23T10:30:00Z"
}
```

### Redirect to Original URL

**Request:**
```http
GET /api/redirect/{shortCode}
```

**Response:**
Redirects to the original URL (HTTP 301)

### Get URL Statistics

**Request:**
```http
GET /api/stats/{shortCode}
```

**Response:**
```json
{
  "shortCode": "abc123",
  "originalUrl": "https://www.example.com/very/long/url",
  "clicks": 1250,
  "createdAt": "2025-05-23T10:30:00Z",
  "lastAccessed": "2025-05-23T14:45:00Z"
}
```

## 🛠️ Configuration

### Environment Variables

Create a `.env` file in the root directory:

```env
# Cassandra Configuration
CASSANDRA_HOST=cassandra
CASSANDRA_PORT=9042
CASSANDRA_KEYSPACE=urlshortener

# API Configuration
API_PORT=8080
API_HOST=0.0.0.0

# Frontend Configuration
REACT_APP_API_URL=http://localhost:8080
```

### Database Schema

The application uses Cassandra with the following keyspace and tables:

```cql
CREATE KEYSPACE urlshortener WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 3};

CREATE TABLE urlshortener.urls (
  short_code TEXT PRIMARY KEY,
  original_url TEXT,
  created_at TIMESTAMP,
  last_accessed TIMESTAMP,
  click_count COUNTER
);

CREATE TABLE urlshortener.url_stats (
  short_code TEXT PRIMARY KEY,
  referrer_list LIST<TEXT>,
  user_agents LIST<TEXT>,
  countries LIST<TEXT>
);
```

## 📊 Analytics Dashboard

The React-based analytics dashboard provides real-time insights:

- **URL Performance**: View click statistics and trends
- **Geographic Data**: Track access by country
- **Device Analytics**: Monitor browser and device usage
- **Referrer Tracking**: See where traffic originates
- **Real-Time Updates**: WebSocket-based live data streaming

Access the dashboard at `http://localhost:3000`

## 🐳 Docker Compose Services

```yaml
services:
  cassandra:      # NoSQL Database
  api-gateway:    # Request Router
  shortener-api:  # URL Shortening Service
  analytics-api:  # Analytics Service
  frontend:       # React Dashboard
```

## 📁 Project Structure

```
urlshortener/
├── backend/
│   ├── api-gateway/
│   ├── shortener-service/
│   ├── analytics-service/
│   └── pom.xml
├── frontend/
│   ├── src/
│   ├── public/
│   └── package.json
├── docker-compose.yml
├── Dockerfile
└── README.md
```

## 🔒 Security Considerations

- Implement rate limiting on API endpoints
- Validate and sanitize all URL inputs
- Use HTTPS in production
- Implement API authentication (OAuth2/JWT recommended)
- Configure Cassandra authentication
- Use Docker secrets for sensitive data

## 📈 Performance Optimization

- Cassandra's distributed architecture handles millions of URLs
- Microservices allow independent scaling
- Docker containers ensure consistent deployment
- Caching strategies for frequently accessed URLs
- Database indexing on short_code and original_url

## 🧪 Testing

```bash
# Run backend tests
cd backend && mvn test

# Run frontend tests
cd frontend && npm test
```

## 📝 License

This project is open source and available under the MIT License.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Steps to Contribute

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Support

For issues, questions, or suggestions, please open an [issue](https://github.com/vyshu-2006/urlshortener/issues) on GitHub.

## 🚀 Deployment

### Production Deployment Checklist

- [ ] Configure environment variables
- [ ] Set up SSL/TLS certificates
- [ ] Configure database replication factor
- [ ] Set up monitoring and logging
- [ ] Configure auto-scaling policies
- [ ] Set up backup and disaster recovery
- [ ] Enable authentication and authorization
- [ ] Configure rate limiting
- [ ] Set up CI/CD pipeline
- [ ] Configure health checks

## 📚 Additional Resources

- [Apache Cassandra Documentation](https://cassandra.apache.org/doc/)
- [Docker Documentation](https://docs.docker.com/)
- [React Documentation](https://react.dev/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

---

**Last Updated**: May 23, 2025

Made with ❤️ by [vyshu-2006](https://github.com/vyshu-2006)
