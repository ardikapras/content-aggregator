import { ReactNode } from 'react';
import { Link } from 'react-router-dom';

interface NavItemProps {
  path: string;
  label: string;
  icon: ReactNode;
  isActive: boolean;
  onClick?: () => void;
  key?: string;
}

const NavItem = ({ path, label, icon, isActive, onClick }: NavItemProps) => {
  return (
    <Link
      to={path}
      className={`nav-link d-flex align-items-center py-2 ${isActive ? 'active' : ''}`}
      onClick={onClick}
    >
      <span className="me-2">{icon}</span>
      {label}
    </Link>
  );
};

export default NavItem;
