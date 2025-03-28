# Content Aggregator Frontend

A modern React application for browsing and managing news content collected from various sources.

## Features

- **News Article Display**: Browse articles with filtering and search capabilities
- **Source Management**: View, add, edit, and toggle news sources
- **Scraper Control**: Trigger content collection operations
- **Responsive Design**: Works on desktop and mobile devices
- **Article Details**: View full article content with metadata

## Technology Stack

- **Framework**: React 19
- **UI Library**: React Bootstrap 2.10
- **Routing**: React Router 7
- **HTTP Client**: Axios
- **State Management**: React Query
- **Build Tool**: Vite
- **Type Safety**: TypeScript
- **Styling**: Bootstrap 5 with custom CSS

## Project Structure

```
main-app/
├── public/              # Static assets
├── src/
│   ├── components/      # React components
│   │   ├── ArticleList.tsx     # Article listing component
│   │   └── SourceList.tsx      # Source management component
│   ├── services/        # API services
│   │   └── Api.ts       # API client using Axios
│   ├── assets/          # Images and other assets
│   ├── App.tsx          # Main application component
│   └── main.tsx         # Application entry point
├── package.json         # Dependencies and scripts
└── vite.config.ts       # Vite configuration
```

## Setup and Running

### Prerequisites

- Node.js 18+
- npm 9+

### Installation

```bash
# Navigate to the frontend directory
cd frontend/main-app

# Install dependencies
npm install
```

### Development Server

```bash
# Start the development server
npm run dev
```

This will start the development server at [http://localhost:3000](http://localhost:3000)

### Build for Production

```bash
# Build the application
npm run build
```

The compiled application will be in the `dist` directory.

### Using Gradle

You can also use Gradle to manage the frontend:

```bash
# Install dependencies
./gradlew frontend:main-app:install

# Start development server
./gradlew frontend:main-app:runDev

# Build for production
./gradlew frontend:main-app:build
```

## API Integration

The application connects to the backend API using Axios. The API service is configured in `src/services/Api.ts`:

```typescript
const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});
```

To change the API URL, modify the `API_BASE_URL` constant.

## Component Overview

### App.tsx

The main application component that handles:
- Navigation with React Router
- Scraper operation triggering
- Overall layout structure

### ArticleList.tsx

Displays articles with features:
- Pagination
- Search filtering
- Article detail modal
- Sorting options

### SourceList.tsx

Manages news sources with capabilities:
- Viewing all sources
- Adding new sources
- Editing existing sources
- Toggling source active status

## Customization

### Styling

The application uses Bootstrap 5 with additional custom styling in `src/custom.css`. To modify the appearance:

- Edit Bootstrap variables before import for theme customization
- Add custom styles to `custom.css`
- Use React Bootstrap's theming capabilities

### Configuration

Key configuration options:

- **API URL**: Set in `src/services/Api.ts`
- **Dev Server**: Configure in `vite.config.ts`
- **Build Settings**: Modify in `package.json` scripts

## Browser Support

The application supports modern browsers:
- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Development Guidelines

- Follow the component structure for new features
- Use TypeScript interfaces for data models
- Leverage React Hooks for state management
- Use React Bootstrap components for UI consistency
