import type { ReactNode } from 'react';
import { cn } from '@/lib/utils';

// Column definition
export interface TableColumn<T> {
  key: string;
  header: string;
  className?: string;
  headerClassName?: string;
  render?: (item: T, index: number) => ReactNode;
}

// Table Props
export interface TableProps<T> {
  columns: TableColumn<T>[];
  data: T[];
  keyExtractor: (item: T) => string | number;
  className?: string;
  emptyMessage?: string;
  isLoading?: boolean;
  onRowClick?: (item: T) => void;
  rowClassName?: string | ((item: T) => string);
}

export function Table<T>({
  columns,
  data,
  keyExtractor,
  className,
  emptyMessage = 'Không có dữ liệu',
  isLoading = false,
  onRowClick,
  rowClassName,
}: Readonly<TableProps<T>>) {
  if (isLoading) {
    return (
      <div className="flex justify-center py-8">
        <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600" />
      </div>
    );
  }

  return (
    <div className="overflow-x-auto">
      <table className={cn('w-full text-sm', className)}>
        <thead>
          <tr className="border-b border-gray-200 bg-gray-50">
            {columns.map((column) => (
              <th
                key={column.key}
                className={cn(
                  'text-left py-3 px-4 font-medium text-gray-700',
                  column.headerClassName
                )}
              >
                {column.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.length === 0 ? (
            <tr>
              <td
                colSpan={columns.length}
                className="py-8 text-center text-gray-500"
              >
                {emptyMessage}
              </td>
            </tr>
          ) : (
            data.map((item, index) => {
              const rowClass =
                typeof rowClassName === 'function'
                  ? rowClassName(item)
                  : rowClassName;

              return (
                <tr
                  key={keyExtractor(item)}
                  className={cn(
                    'border-b border-gray-100 hover:bg-gray-50 transition-colors',
                    onRowClick && 'cursor-pointer',
                    rowClass
                  )}
                  onClick={() => onRowClick?.(item)}
                >
                  {columns.map((column) => (
                    <td
                      key={column.key}
                      className={cn('py-3 px-4', column.className)}
                    >
                      {column.render
                        ? column.render(item, index)
                        : (item as Record<string, unknown>)[column.key] as ReactNode}
                    </td>
                  ))}
                </tr>
              );
            })
          )}
        </tbody>
      </table>
    </div>
  );
}

export default Table;
