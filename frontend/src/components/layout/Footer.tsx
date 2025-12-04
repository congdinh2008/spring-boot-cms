import { Link } from 'react-router-dom';

export function Footer() {
  return (
    <footer className="border-t border-gray-200 bg-gray-50">
      <div className="mx-auto max-w-7xl px-4 py-8">
        <div className="grid gap-8 md:grid-cols-3">
          {/* About */}
          <div>
            <h3 className="text-lg font-semibold mb-4">CMS System</h3>
            <p className="text-sm text-gray-500">
              Hệ thống quản lý nội dung tin tức. Được xây dựng với Spring Boot
              và React TypeScript.
            </p>
          </div>

          {/* Quick Links */}
          <div>
            <h3 className="text-lg font-semibold mb-4">Liên kết nhanh</h3>
            <ul className="space-y-2 text-sm">
              <li>
                <Link
                  to="/"
                  className="text-gray-500 hover:text-blue-600 transition-colors"
                >
                  Trang chủ
                </Link>
              </li>
              <li>
                <Link
                  to="/categories"
                  className="text-gray-500 hover:text-blue-600 transition-colors"
                >
                  Danh mục
                </Link>
              </li>
              <li>
                <Link
                  to="/news"
                  className="text-gray-500 hover:text-blue-600 transition-colors"
                >
                  Tin tức
                </Link>
              </li>
            </ul>
          </div>

          {/* Contact */}
          <div>
            <h3 className="text-lg font-semibold mb-4">Liên hệ</h3>
            <ul className="space-y-2 text-sm text-gray-500">
              <li>Email: support@cms.example.com</li>
              <li>Phone: +84 123 456 789</li>
            </ul>
          </div>
        </div>

        <div className="mt-8 pt-8 border-t border-gray-200 text-center text-sm text-gray-500">
          <p>&copy; {new Date().getFullYear()} CMS System. All rights reserved.</p>
        </div>
      </div>
    </footer>
  );
}
