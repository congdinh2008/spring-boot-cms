# CMS Frontend

React + TypeScript frontend for the Content Management System.

## 🚀 Tech Stack

- **React** 19.2 with TypeScript 5.9
- **Build Tool**: Vite 7
- **State Management**:
  - TanStack Query v5 (Server State)
  - Zustand (Client State)
- **Styling**: Tailwind CSS 4.0
- **Routing**: React Router v7
- **HTTP Client**: Axios
- **Form Validation**: React Hook Form + Custom validation

## 📁 Project Structure

```
src/
├── api/                    # API client and endpoints
│   ├── client.ts          # Axios instance with interceptors
│   ├── auth.ts            # Auth API calls
│   ├── categories.ts      # Categories API
│   └── news.ts            # News API
├── components/
│   ├── ui/                # Reusable UI components
│   │   ├── Button.tsx
│   │   ├── Input.tsx
│   │   ├── Modal.tsx
│   │   ├── Table.tsx
│   │   ├── Pagination.tsx
│   │   └── ...
│   ├── layout/            # Layout components
│   │   ├── Header.tsx
│   │   ├── Footer.tsx
│   │   └── Layout.tsx
│   └── auth/              # Auth components
│       └── ProtectedRoute.tsx
├── hooks/                  # Custom React hooks
│   ├── useAuth.ts
│   ├── useCategories.ts
│   └── useNews.ts
├── pages/                  # Page components
│   ├── HomePage.tsx
│   ├── auth/
│   │   ├── LoginPage.tsx
│   │   └── RegisterPage.tsx
│   ├── news/
│   │   ├── NewsListPage.tsx
│   │   ├── NewsDetailPage.tsx
│   │   └── MyNewsPage.tsx
│   ├── category/
│   │   └── CategoryListPage.tsx
│   └── admin/
│       └── AdminDashboardPage.tsx
├── stores/                 # Zustand stores
│   └── authStore.ts
├── types/                  # TypeScript types
│   ├── auth.ts
│   ├── news.ts
│   └── category.ts
└── lib/
    └── utils.ts           # Utility functions
```

## 🛠️ Development

### Prerequisites

- Node.js 20+
- npm or yarn

### Setup

```bash
# Install dependencies
npm install

# Copy environment file
cp .env.example .env

# Start development server
npm run dev

# App runs at http://localhost:5173
```

### Available Scripts

```bash
npm run dev          # Start dev server with HMR
npm run build        # Build for production
npm run preview      # Preview production build
npm run lint         # Run ESLint
npm run type-check   # TypeScript type checking
```

## 🔧 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `VITE_API_BASE_URL` | Base URL for API calls | `/api` |

## 🐳 Docker

### Build Image

```bash
# For local testing
docker build -t cms-frontend .

# For Cloud Run (Mac M1/M2)
docker build --platform linux/amd64 -t cms-frontend .
```

### Run Container

```bash
docker run -p 80:80 \
  -e BACKEND_URL=http://backend:8080 \
  cms-frontend
```

## 🏛️ Architecture

### API Proxy (Production)

In production, nginx proxies `/api/*` requests to the backend:

```
User → nginx (frontend) → /api/* → Backend Cloud Run
                       → /* → Static React files
```

### CORS Handling

- **Development**: Vite proxy handles `/api` requests
- **Production**: nginx handles CORS headers and removes Origin header when proxying

### State Management

- **Server State** (TanStack Query): API data caching, background refetching
- **Client State** (Zustand): Auth state, UI state

## 📚 Key Features

- JWT authentication with auto-refresh
- Protected routes with role-based access
- Pagination, search, and filtering
- Responsive design with Tailwind CSS
- Form validation
- Error handling with toast notifications

## 🔗 API Integration

The frontend communicates with the backend via:

```typescript
// api/client.ts
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
});

// Auto-attach JWT token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

## 📄 License

MIT License
