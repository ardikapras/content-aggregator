import { RssSimple, NewspaperClipping, House, Gear, Database } from 'phosphor-react';
import { NavItemType } from '../types/layoutTypes';

/**
 * Application navigation items configuration
 */
export const navigationItems: NavItemType[] = [
  {
    path: '/',
    label: 'Dashboard',
    icon: <House size={20} />,
  },
  {
    path: '/articles',
    label: 'Articles',
    icon: <NewspaperClipping size={20} />,
  },
  {
    path: '/sources',
    label: 'Sources',
    icon: <RssSimple size={20} />,
  },
  {
    path: '/parsers',
    label: 'Parser Configs',
    icon: <Gear size={20} />,
  },
  {
    path: '/stats',
    label: 'Scraper Stats',
    icon: <Database size={20} />,
  },
];
