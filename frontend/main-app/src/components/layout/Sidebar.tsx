import { FC } from 'react';
import { Nav } from 'react-bootstrap';
import { useLocation } from 'react-router-dom';
import NavItem from './NavItem';
import { NavItemType } from '../../types/layoutTypes';

interface SidebarProps {
  navItems: NavItemType[];
  onRunScraper: () => void;
  isScraperRunning: boolean;
}

const Sidebar: FC<SidebarProps> = ({ navItems }) => {
  const location = useLocation();

  return (
    <div
      className="d-none d-lg-flex flex-column bg-light border-end"
      style={{ width: '240px', minHeight: 'calc(100vh - 56px)' }}
    >
      <div className="p-3 border-bottom">
        <h5 className="mb-0">Navigation</h5>
      </div>
      <Nav className="flex-column pt-2">
        {navItems.map(item => (
          <div key={item.path}>
            <NavItem
              path={item.path}
              label={item.label}
              icon={item.icon}
              isActive={location.pathname === item.path}
            />
          </div>
        ))}
      </Nav>
    </div>
  );
};

export default Sidebar;
