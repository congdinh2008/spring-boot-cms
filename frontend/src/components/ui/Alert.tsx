import { cn } from '@/lib/utils';
import { AlertCircle, CheckCircle, Info, XCircle } from 'lucide-react';

interface AlertProps {
  variant?: 'default' | 'success' | 'warning' | 'error';
  title?: string;
  children: React.ReactNode;
  className?: string;
}

const variants = {
  default: {
    container: 'bg-blue-50 border-blue-200 text-blue-800',
    icon: Info,
    iconClass: 'text-blue-500',
  },
  success: {
    container: 'bg-green-50 border-green-200 text-green-800',
    icon: CheckCircle,
    iconClass: 'text-green-500',
  },
  warning: {
    container: 'bg-yellow-50 border-yellow-200 text-yellow-800',
    icon: AlertCircle,
    iconClass: 'text-yellow-500',
  },
  error: {
    container: 'bg-red-50 border-red-200 text-red-800',
    icon: XCircle,
    iconClass: 'text-red-500',
  },
};

export function Alert({ variant = 'default', title, children, className }: AlertProps) {
  const config = variants[variant];
  const Icon = config.icon;

  return (
    <div
      className={cn(
        'flex gap-3 rounded-lg border p-4',
        config.container,
        className
      )}
      role="alert"
    >
      <Icon className={cn('h-5 w-5 shrink-0', config.iconClass)} />
      <div className="flex-1">
        {title && <p className="font-medium">{title}</p>}
        <div className={cn(title && 'mt-1', 'text-sm')}>{children}</div>
      </div>
    </div>
  );
}
