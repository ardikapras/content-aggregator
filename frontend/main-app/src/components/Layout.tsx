import { FC } from 'react';
import { Container } from 'react-bootstrap';
import { MobileMenu, Sidebar, TopNavbar } from './layout';
import { useSidebar } from '../hooks';
import { navigationItems } from '../config/navigationConfig';
import { LayoutProps } from '../types/layoutTypes';

/**
 * Main application layout component
 */
const Layout: FC<LayoutProps> = ({ children, onRunScraper, isScraperRunning }) => {
  const { show, handleClose, handleShow } = useSidebar();

  return (
    <div className="d-flex flex-column min-vh-100">
      {/* Top Navbar - visible on all devices */}
      <TopNavbar
        onMenuClick={handleShow}
        onRunScraper={onRunScraper}
        isScraperRunning={isScraperRunning}
      />

      <div className="d-flex flex-grow-1">
        {/* Desktop Sidebar */}
        <Sidebar
          navItems={navigationItems}
          onRunScraper={onRunScraper}
          isScraperRunning={isScraperRunning}
        />

        {/* Mobile Sidebar */}
        <MobileMenu
          show={show}
          onHide={handleClose}
          navItems={navigationItems}
          onRunScraper={onRunScraper}
          isScraperRunning={isScraperRunning}
        />

        {/* Main content */}
        <Container fluid className="py-3 flex-grow-1">
          {children}
        </Container>
      </div>
    </div>
  );
};

export default Layout;
