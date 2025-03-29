import { ReactNode } from 'react';

export interface NavItemType {
  path: string;
  label: string;
  icon: ReactNode;
}

export interface NavItemProps {
  path: string;
  label: string;
  icon: ReactNode;
  isActive: boolean;
  onClick?: () => void;
}

export interface LayoutProps {
  children: ReactNode;
  onRunScraper: () => void;
  isScraperRunning: boolean;
}
