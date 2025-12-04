import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Search, Edit, Trash2, Eye, Send } from 'lucide-react';
import { useNews, useCreateNews, useUpdateNews, useDeleteNews, usePublishNews, useCategories, useNewsDetail } from '@/hooks';
import {
  Button,
  Input,
  Card,
  CardContent,
  Modal,
  ConfirmModal,
  LoadingSpinner,
  Alert,
  Pagination,
  Textarea,
  Select,
  StatusBadge,
  Table,
} from '@/components/ui';
import type { TableColumn } from '@/components/ui';
import type { News, NewsRequest, NewsStatus } from '@/types';

export function MyNewsPage() {
  const [page, setPage] = useState(1);
  const [keyword, setKeyword] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [statusFilter, setStatusFilter] = useState<NewsStatus | ''>('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [selectedNews, setSelectedNews] = useState<News | null>(null);
  const [editingNewsId, setEditingNewsId] = useState<number | null>(null);
  const [formData, setFormData] = useState<NewsRequest>({
    title: '',
    content: '',
    categoryId: 0,
  });

  const { data, isLoading, error } = useNews({
    page: page - 1,
    size: 10,
    keyword: keyword || undefined,
    status: statusFilter || undefined,
    sortBy: 'createdAt',
    sortDir: 'desc',
  });

  const { data: categoriesData } = useCategories({ size: 100 });
  
  // Fetch news detail when editing
  const { data: newsDetailData, isLoading: isLoadingDetail } = useNewsDetail(editingNewsId || 0);

  const createMutation = useCreateNews();
  const updateMutation = useUpdateNews();
  const deleteMutation = useDeleteNews();
  const publishMutation = usePublishNews();

  const newsList = data?.data?.content || [];
  const totalPages = data?.data?.totalPages || 1;
  const categories = categoriesData?.data?.content || [];

  // Update form when news detail is loaded
  useEffect(() => {
    if (newsDetailData?.data && editingNewsId) {
      const detail = newsDetailData.data;
      setFormData({
        title: detail.title,
        content: detail.content,
        categoryId: detail.categoryId,
      });
    }
  }, [newsDetailData, editingNewsId]);

  const handleSearch = () => {
    setKeyword(searchInput);
    setPage(1);
  };

  const handleOpenCreate = () => {
    setSelectedNews(null);
    setEditingNewsId(null);
    setFormData({
      title: '',
      content: '',
      categoryId: categories[0]?.id || 0,
    });
    setIsModalOpen(true);
  };

  const handleOpenEdit = (news: News) => {
    setSelectedNews(news);
    setEditingNewsId(news.id);
    // Set initial values, content will be loaded from detail
    setFormData({
      title: news.title,
      content: '',
      categoryId: news.categoryId,
    });
    setIsModalOpen(true);
  };

  const handleOpenDelete = (news: News) => {
    setSelectedNews(news);
    setIsDeleteModalOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (selectedNews) {
        await updateMutation.mutateAsync({ id: selectedNews.id, data: formData });
      } else {
        await createMutation.mutateAsync(formData);
      }
      setIsModalOpen(false);
      setEditingNewsId(null);
    } catch {
      // Error handled by mutation
    }
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

  const handlePublish = async (id: number) => {
    await publishMutation.mutateAsync(id);
  };

  // Table columns definition
  const columns: TableColumn<News>[] = [
    {
      key: 'title',
      header: 'Tiêu đề',
      className: 'font-medium max-w-xs truncate',
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
      key: 'actions',
      header: 'Thao tác',
      headerClassName: 'text-right',
      className: 'text-right',
      render: (news) => (
        <div className="flex justify-end gap-1">
          {news.status === 'PUBLISHED' && (
            <Link to={`/news/${news.id}`}>
              <Button variant="ghost" size="icon" title="Xem">
                <Eye className="h-4 w-4" />
              </Button>
            </Link>
          )}
          {news.status === 'DRAFT' && (
            <Button
              variant="ghost"
              size="icon"
              onClick={(e) => {
                e.stopPropagation();
                handlePublish(news.id);
              }}
              disabled={publishMutation.isPending}
              title="Xuất bản"
            >
              <Send className="h-4 w-4 text-green-600" />
            </Button>
          )}
          <Button
            variant="ghost"
            size="icon"
            onClick={(e) => {
              e.stopPropagation();
              handleOpenEdit(news);
            }}
            title="Sửa"
          >
            <Edit className="h-4 w-4" />
          </Button>
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
        <h1 className="text-2xl font-bold">Tin tức của tôi</h1>
        <Button onClick={handleOpenCreate}>
          <Plus className="h-4 w-4 mr-2" />
          Tạo tin tức
        </Button>
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

      {/* Create/Edit Modal */}
      <Modal
        isOpen={isModalOpen}
        onClose={() => {
          setIsModalOpen(false);
          setEditingNewsId(null);
        }}
        title={selectedNews ? 'Sửa tin tức' : 'Tạo tin tức'}
        size="lg"
      >
        {isLoadingDetail && selectedNews ? (
          <div className="flex justify-center py-8">
            <LoadingSpinner />
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            <Input
              label="Tiêu đề"
              value={formData.title}
              onChange={(e) => setFormData((prev) => ({ ...prev, title: e.target.value }))}
              required
            />
            <Select
              label="Danh mục"
              value={formData.categoryId}
              onChange={(e) => setFormData((prev) => ({ ...prev, categoryId: Number(e.target.value) }))}
              options={categories.map((cat) => ({ value: cat.id, label: cat.name }))}
              required
            />
            <Textarea
              label="Nội dung"
              value={formData.content}
              onChange={(e) => setFormData((prev) => ({ ...prev, content: e.target.value }))}
              rows={8}
              required
            />
            <div className="flex justify-end gap-3">
              <Button
                type="button"
                variant="outline"
                onClick={() => {
                  setIsModalOpen(false);
                  setEditingNewsId(null);
                }}
              >
                Hủy
              </Button>
              <Button
                type="submit"
                isLoading={createMutation.isPending || updateMutation.isPending}
              >
                {selectedNews ? 'Cập nhật' : 'Tạo mới'}
              </Button>
            </div>
          </form>
        )}
      </Modal>

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
