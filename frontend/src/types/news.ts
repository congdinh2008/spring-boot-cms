export type NewsStatus = 'DRAFT' | 'PUBLISHED';

// News list item (NewsResponseDTO from backend)
export interface News {
  id: number;
  title: string;
  status: NewsStatus;
  categoryId: number;
  categoryName: string;
  authorId: number;
  authorName: string;
  createdAt: string;
  updatedAt: string | null;
}

// News detail (NewsDetailDTO from backend)
export interface NewsDetail extends News {
  content: string;
  categorySlug: string;
  authorEmail: string;
}

// Request to create/update news (NewsRequestDTO from backend)
export interface NewsRequest {
  title: string;
  content: string;
  categoryId: number;
}

export interface NewsListParams {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'asc' | 'desc';
  keyword?: string;
  status?: NewsStatus;
  categoryId?: number;
}
