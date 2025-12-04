import { Link } from 'react-router-dom';
import { useState } from 'react';
import { Search, Clock, User, ChevronRight } from 'lucide-react';
import { useNews, useCategories } from '@/hooks';
import {
  Button,
  Input,
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  Select,
  LoadingSpinner,
  Alert,
  Pagination,
  Badge,
} from '@/components/ui';
import { formatDate } from '@/lib/utils';

export function NewsListPage() {
  const [page, setPage] = useState(1);
  const [keyword, setKeyword] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [categoryId, setCategoryId] = useState<number | ''>('');

  const { data, isLoading, error } = useNews({
    page: page - 1,
    size: 12,
    keyword: keyword || undefined,
    categoryId: categoryId || undefined,
    sortBy: 'createdAt',
    sortDir: 'desc',
  });

  const { data: categoriesData } = useCategories({ size: 100 });

  const news = data?.data?.content || [];
  const totalPages = data?.data?.totalPages || 1;
  const categories = categoriesData?.data?.content || [];

  const handleSearch = () => {
    setKeyword(searchInput);
    setPage(1);
  };

  if (error) {
    return (
      <Alert variant="error" title="Lỗi">
        Không thể tải danh sách tin tức
      </Alert>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row justify-between gap-4">
        <h1 className="text-2xl font-bold">Tin tức</h1>
      </div>

      {/* Filters */}
      <Card>
        <CardContent className="pt-6">
          <div className="flex flex-col sm:flex-row gap-4">
            <div className="flex gap-2 flex-1 min-w-0">
              <div className="flex-1 min-w-0">
                <Input
                  placeholder="Tìm kiếm tin tức..."
                  value={searchInput}
                  onChange={(e) => setSearchInput(e.target.value)}
                  onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                />
              </div>
              <Button onClick={handleSearch} className="shrink-0">
                <Search className="h-4 w-4" />
              </Button>
            </div>
            <Select
              value={categoryId}
              onChange={(e) => {
                const val = e.target.value;
                setCategoryId(val === '' ? '' : Number(val));
                setPage(1);
              }}
              options={[
                { value: '', label: 'Tất cả danh mục' },
                ...categories.map((cat) => ({ value: cat.id, label: cat.name })),
              ]}
              className="w-full sm:w-48"
            />
          </div>
        </CardContent>
      </Card>

      {/* Loading */}
      {isLoading && (
        <div className="flex justify-center py-12">
          <LoadingSpinner size="lg" />
        </div>
      )}

      {/* News Grid */}
      {!isLoading && (
        <div className="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
          {news.map((item) => (
            <Card key={item.id} className="overflow-hidden hover:shadow-lg transition-shadow">
              <CardHeader>
                <div className="flex items-center gap-2 mb-2">
                  <Badge variant="secondary">{item.categoryName}</Badge>
                </div>
                <CardTitle className="text-lg line-clamp-2">
                  <Link
                    to={`/news/${item.id}`}
                    className="hover:text-blue-600 transition-colors"
                  >
                    {item.title}
                  </Link>
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="flex items-center justify-between text-sm text-gray-500">
                  <div className="flex items-center gap-4">
                    <span className="flex items-center gap-1">
                      <User className="h-4 w-4" />
                      {item.authorName}
                    </span>
                    <span className="flex items-center gap-1">
                      <Clock className="h-4 w-4" />
                      {formatDate(item.createdAt)}
                    </span>
                  </div>
                  <Link
                    to={`/news/${item.id}`}
                    className="text-blue-600 hover:underline flex items-center gap-1"
                  >
                    Xem thêm
                    <ChevronRight className="h-4 w-4" />
                  </Link>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      )}

      {!isLoading && news.length === 0 && (
        <Card>
          <CardContent className="py-12 text-center text-gray-500">
            Không có tin tức nào
          </CardContent>
        </Card>
      )}

      {/* Pagination */}
      {totalPages > 1 && (
        <Pagination
          currentPage={page}
          totalPages={totalPages}
          onPageChange={setPage}
        />
      )}
    </div>
  );
}
