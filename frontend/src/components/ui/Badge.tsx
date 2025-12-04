import { cn } from '@/lib/utils';

type BadgeVariant = 'default' | 'secondary' | 'success' | 'warning' | 'error' | 'outline';

interface BadgeProps {
  variant?: BadgeVariant;
  children: React.ReactNode;
  className?: string;
}

const variants: Record<BadgeVariant, string> = {
  default: 'bg-blue-600 text-white',
  secondary: 'bg-gray-100 text-gray-800',
  success: 'bg-green-100 text-green-800',
  warning: 'bg-yellow-100 text-yellow-800',
  error: 'bg-red-100 text-red-800',
  outline: 'border border-gray-200 bg-white text-gray-900',
};

export function Badge({ variant = 'default', children, className }: BadgeProps) {
  return (
    <span
      className={cn(
        'inline-flex items-center rounded-full px-2.5 py-0.5 text-xs font-semibold',
        variants[variant],
        className
      )}
    >
      {children}
    </span>
  );
}

// Status badge helper
interface StatusBadgeProps {
  status: 'DRAFT' | 'PUBLISHED' | 'ACTIVE' | 'INACTIVE';
}

const statusVariants: Record<StatusBadgeProps['status'], BadgeVariant> = {
  DRAFT: 'secondary',
  PUBLISHED: 'success',
  ACTIVE: 'success',
  INACTIVE: 'secondary',
};

const statusLabels: Record<StatusBadgeProps['status'], string> = {
  DRAFT: 'Bản nháp',
  PUBLISHED: 'Đã xuất bản',
  ACTIVE: 'Hoạt động',
  INACTIVE: 'Không hoạt động',
};

export function StatusBadge({ status }: StatusBadgeProps) {
  return (
    <Badge variant={statusVariants[status]}>
      {statusLabels[status]}
    </Badge>
  );
}
