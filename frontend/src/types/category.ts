// Category response (CategoryResponseDTO from backend)
export interface Category {
  id: number;
  name: string;
  slug: string;
  createdAt: string;
}

// Request to create/update category (CategoryRequestDTO from backend)
export interface CategoryRequest {
  name: string;
}

export interface CategoryListParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
  keyword?: string;
}
