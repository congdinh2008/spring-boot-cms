import { useParams, Link } from 'react-router-dom';
import { ArrowLeft, Clock, User, Folder } from 'lucide-react';
import { useNewsDetail } from '@/hooks';
import { Button, Card, CardContent, LoadingSpinner, Alert, Badge } from '@/components/ui';
import { formatDate } from '@/lib/utils';

export function NewsDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { data, isLoading, error } = useNewsDetail(id!);

  const news = data?.data;

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <LoadingSpinner size="lg" />
      </div>
    );
  }

  if (error || !news) {
    return (
      <Alert variant="error" title="Lỗi">
        Không tìm thấy tin tức
      </Alert>
    );
  }

  return (
    <div className="max-w-4xl mx-auto">
      <Link to="/news">
        <Button variant="ghost" className="mb-6">
          <ArrowLeft className="h-4 w-4 mr-2" />
          Quay lại
        </Button>
      </Link>

      <article className="space-y-6">
        {/* Header */}
        <header className="space-y-4">
          <Badge variant="secondary" className="mb-2">
            <Folder className="h-3 w-3 mr-1" />
            {news.categoryName}
          </Badge>
          <h1 className="text-3xl font-bold">{news.title}</h1>
          <div className="flex items-center gap-4 text-gray-500">
            <span className="flex items-center gap-2">
              <User className="h-4 w-4" />
              {news.authorName}
            </span>
            <span className="flex items-center gap-2">
              <Clock className="h-4 w-4" />
              {formatDate(news.createdAt)}
            </span>
          </div>
        </header>

        {/* Content */}
        <Card>
          <CardContent className="py-6">
            <div
              className="prose prose-lg max-w-none"
              dangerouslySetInnerHTML={{ __html: news.content }}
            />
          </CardContent>
        </Card>
      </article>
    </div>
  );
}
