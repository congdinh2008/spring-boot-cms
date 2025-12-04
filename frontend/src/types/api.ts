// Generic API Response wrapper
export interface ApiResponse<T> {
  timestamp: string;
  status: number;
  message: string;
  data: T;
}

// Pagination types - matches backend PagedResponse
export interface PaginatedData<T> {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
  hasContent: boolean;
  hasNext: boolean;
  hasPrevious: boolean;
}

// For backward compatibility
export interface PageInfo {
  totalElements: number;
  totalPages: number;
}

export interface PaginatedResponse<T> {
  content: T[];
  page?: PageInfo;
  pageNumber?: number;
  pageSize?: number;
  totalElements?: number;
  totalPages?: number;
}

// Query parameters for list APIs
export interface ListQueryParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
  keyword?: string;
}

// Error response
export interface ApiError {
  success: boolean;
  message: string;
  errors?: Record<string, string>;
}
