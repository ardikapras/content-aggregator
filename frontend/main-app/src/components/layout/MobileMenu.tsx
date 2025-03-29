import { FC } from 'react';
import { Offcanvas, Nav } from 'react-bootstrap';
import { useLocation } from 'react-router-dom';
import NavItem from './NavItem';
import { ScraperButton } from './ScraperButton';
import { NavItemType } from '../../types/layoutTypes';

interface MobileMenuProps {
  show: boolean;
  onHide: () => void;
  navItems: NavItemType[];
  onRunScraper: () => void;
  isScraperRunning: boolean;
}

const MobileMenu: FC<MobileMenuProps> = ({
  show,
  onHide,
  navItems,
  onRunScraper,
  isScraperRunning,
}) => {
  const location = useLocation();

  return (
    <Offcanvas show={show} onHide={onHide} className="d-lg-none" placement="start">
      <Offcanvas.Header closeButton>
        <Offcanvas.Title>Menu</Offcanvas.Title>
      </Offcanvas.Header>
      <Offcanvas.Body>
        <Nav className="flex-column">
          {navItems.map(item => (
            <div key={item.path}>
              <NavItem
                path={item.path}
                label={item.label}
                icon={item.icon}
                isActive={location.pathname === item.path}
                onClick={onHide}
              />
            </div>
          ))}
          <div className="mt-3">
            <ScraperButton
              onRunScraper={() => {
                onHide();
                onRunScraper();
              }}
              isScraperRunning={isScraperRunning}
            />
          </div>
        </Nav>
      </Offcanvas.Body>
    </Offcanvas>
  );
};

export default MobileMenu;
