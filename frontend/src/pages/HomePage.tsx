import { Link } from 'react-router-dom';
import { ArrowRight, Newspaper, Folder, Shield } from 'lucide-react';
import { useIsAuthenticated, useIsAdmin } from '@/stores';
import { Button, Card, CardHeader, CardTitle, CardDescription, CardContent } from '@/components/ui';

export function HomePage() {
  const isAuthenticated = useIsAuthenticated();
  const isAdmin = useIsAdmin();

  return (
    <div className="space-y-12">
      {/* Hero Section */}
      <section className="text-center py-12">
        <h1 className="text-4xl font-bold tracking-tight mb-4">
          Chào mừng đến với CMS System
        </h1>
        <p className="text-xl text-gray-500 max-w-2xl mx-auto mb-8">
          Hệ thống quản lý nội dung tin tức hiện đại, dễ sử dụng và mạnh mẽ.
          Được xây dựng với Spring Boot và React TypeScript.
        </p>
        {!isAuthenticated && (
          <div className="flex justify-center gap-4">
            <Link to="/login">
              <Button size="lg">Đăng nhập</Button>
            </Link>
            <Link to="/register">
              <Button size="lg" variant="outline">
                Đăng ký
              </Button>
            </Link>
          </div>
        )}
      </section>

      {/* Features Section */}
      <section>
        <h2 className="text-2xl font-bold text-center mb-8">Tính năng chính</h2>
        <div className="grid gap-6 md:grid-cols-3">
          <Card>
            <CardHeader>
              <div className="h-12 w-12 rounded-lg bg-blue-100 flex items-center justify-center mb-4">
                <Newspaper className="h-6 w-6 text-blue-600" />
              </div>
              <CardTitle>Quản lý Tin tức</CardTitle>
              <CardDescription>
                Tạo, chỉnh sửa và xuất bản tin tức với giao diện trực quan
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Link to="/news" className="text-blue-600 hover:underline flex items-center gap-2">
                Xem tin tức
                <ArrowRight className="h-4 w-4" />
              </Link>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <div className="h-12 w-12 rounded-lg bg-blue-100 flex items-center justify-center mb-4">
                <Folder className="h-6 w-6 text-blue-600" />
              </div>
              <CardTitle>Quản lý Danh mục</CardTitle>
              <CardDescription>
                Tổ chức tin tức theo danh mục với cấu trúc phân cấp
              </CardDescription>
            </CardHeader>
            <CardContent>
              <Link to="/categories" className="text-blue-600 hover:underline flex items-center gap-2">
                Xem danh mục
                <ArrowRight className="h-4 w-4" />
              </Link>
            </CardContent>
          </Card>

          <Card>
            <CardHeader>
              <div className="h-12 w-12 rounded-lg bg-blue-100 flex items-center justify-center mb-4">
                <Shield className="h-6 w-6 text-blue-600" />
              </div>
              <CardTitle>Phân quyền người dùng</CardTitle>
              <CardDescription>
                Hệ thống phân quyền với vai trò User và Admin
              </CardDescription>
            </CardHeader>
            <CardContent>
              {isAdmin ? (
                <Link to="/admin" className="text-blue-600 hover:underline flex items-center gap-2">
                  Trang quản trị
                  <ArrowRight className="h-4 w-4" />
                </Link>
              ) : (
                <span className="text-gray-500">Chỉ dành cho Admin</span>
              )}
            </CardContent>
          </Card>
        </div>
      </section>

      {/* Quick Actions for authenticated users */}
      {isAuthenticated && (
        <section className="bg-muted/50 rounded-lg p-8">
          <h2 className="text-2xl font-bold mb-6">Thao tác nhanh</h2>
          <div className="flex flex-wrap gap-4">
            <Link to="/my-news">
              <Button>
                <Newspaper className="h-4 w-4 mr-2" />
                Tin tức của tôi
              </Button>
            </Link>
            <Link to="/categories">
              <Button variant="outline">
                <Folder className="h-4 w-4 mr-2" />
                Quản lý danh mục
              </Button>
            </Link>
            {isAdmin && (
              <Link to="/admin">
                <Button variant="outline">
                  <Shield className="h-4 w-4 mr-2" />
                  Trang quản trị
                </Button>
              </Link>
            )}
          </div>
        </section>
      )}
    </div>
  );
}
