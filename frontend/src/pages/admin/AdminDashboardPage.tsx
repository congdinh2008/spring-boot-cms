import { useState } from 'react';
import { Search, Trash2, FileText, CheckCircle, Archive } from 'lucide-react';
import { useNews, useDeleteNews, useDashboard } from '@/hooks';
import {
  Button,
  Input,
  Card,
  CardContent,
  ConfirmModal,
  LoadingSpinner,
  Alert,
  Pagination,
  Select,
  StatusBadge,
  Table,
} from '@/components/ui';
import type { TableColumn } from '@/components/ui';
import type { News, NewsStatus } from '@/types';
import { formatDate } from '@/lib/utils';

export function AdminDashboardPage() {
  const [page, setPage] = useState(1);
  const [keyword, setKeyword] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [statusFilter, setStatusFilter] = useState<NewsStatus | ''>('');
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedNews, setSelectedNews] = useState<News | null>(null);

  // Fetch dashboard stats
  const { data: dashboardData } = useDashboard();
  const stats = dashboardData?.data;

  const { data, isLoading, error } = useNews({
    page: page - 1,
    size: 10,
    keyword: keyword || undefined,
    status: statusFilter || undefined,
    sortBy: 'createdAt',
    sortDir: 'desc',
  });

  const deleteMutation = useDeleteNews();

  const newsList = data?.data?.content || [];
  const totalPages = data?.data?.totalPages || 1;

  const handleSearch = () => {
    setKeyword(searchInput);
    setPage(1);
  };

  const handleOpenDelete = (news: News) => {
    setSelectedNews(news);
    setIsDeleteModalOpen(true);
  };

  const handleDelete = async () => {
    if (!selectedNews) return;
    try {
      await deleteMutation.mutateAsync(selectedNews.id);
      setIsDeleteModalOpen(false);
    } catch {
      // Error handled by mutation
    }
  };

  // Table columns definition
  const columns: TableColumn<News>[] = [
    {
      key: 'title',
      header: 'Tiêu đề',
      className: 'font-medium max-w-xs truncate',
    },
    {
      key: 'authorName',
      header: 'Tác giả',
      className: 'text-gray-500',
    },
    {
      key: 'categoryName',
      header: 'Danh mục',
      className: 'text-gray-500',
    },
    {
      key: 'status',
      header: 'Trạng thái',
      render: (news) => <StatusBadge status={news.status} />,
    },
    {
      key: 'createdAt',
      header: 'Ngày tạo',
      className: 'text-gray-500',
      render: (news) => formatDate(news.createdAt),
    },
    {
      key: 'actions',
      header: 'Thao tác',
      headerClassName: 'text-right',
      className: 'text-right',
      render: (news) => (
        <div className="flex justify-end gap-1">
          <Button
            variant="ghost"
            size="icon"
            onClick={(e) => {
              e.stopPropagation();
              handleOpenDelete(news);
            }}
            title="Xóa"
          >
            <Trash2 className="h-4 w-4 text-red-600" />
          </Button>
        </div>
      ),
    },
  ];

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

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
        <h1 className="text-2xl font-bold">Quản trị - Dashboard</h1>
      </div>

      {/* Stats from Dashboard API */}
      <div className="grid gap-4 md:grid-cols-3">
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <FileText className="h-8 w-8 text-blue-500" />
              <div>
                <div className="text-2xl font-bold">{stats?.totalNews || 0}</div>
                <p className="text-gray-500 text-sm">Tổng số tin tức</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <CheckCircle className="h-8 w-8 text-green-500" />
              <div>
                <div className="text-2xl font-bold text-green-600">{stats?.newsByStatus?.PUBLISHED || 0}</div>
                <p className="text-gray-500 text-sm">Đã xuất bản</p>
              </div>
            </div>
          </CardContent>
        </Card>
        <Card>
          <CardContent className="pt-6">
            <div className="flex items-center gap-3">
              <Archive className="h-8 w-8 text-yellow-500" />
              <div>
                <div className="text-2xl font-bold text-yellow-600">{stats?.newsByStatus?.DRAFT || 0}</div>
                <p className="text-gray-500 text-sm">Bản nháp</p>
              </div>
            </div>
          </CardContent>
        </Card>
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
              value={statusFilter}
              onChange={(e) => {
                setStatusFilter(e.target.value as NewsStatus | '');
                setPage(1);
              }}
              options={[
                { value: '', label: 'Tất cả trạng thái' },
                { value: 'DRAFT', label: 'Bản nháp' },
                { value: 'PUBLISHED', label: 'Đã xuất bản' },
              ]}
              className="w-full sm:w-48"
            />
          </div>
        </CardContent>
      </Card>

      {/* Table */}
      <Card>
        <CardContent className="pt-6">
          <Table
            columns={columns}
            data={newsList}
            keyExtractor={(news) => news.id}
            emptyMessage="Không có tin tức nào"
          />

          {totalPages > 1 && (
            <div className="mt-6">
              <Pagination
                currentPage={page}
                totalPages={totalPages}
                onPageChange={setPage}
              />
            </div>
          )}
        </CardContent>
      </Card>

      {/* Delete Confirm Modal */}
      <ConfirmModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={handleDelete}
        title="Xóa tin tức"
        message={`Bạn có chắc chắn muốn xóa tin tức "${selectedNews?.title}"?`}
        confirmText="Xóa"
        variant="destructive"
        isLoading={deleteMutation.isPending}
      />
    </div>
  );
}
