import { FC } from 'react';

interface PaginationInfoProps {
  startItem: number;
  endItem: number;
  totalItems: number;
}

const PaginationInfo: FC<PaginationInfoProps> = ({ startItem, endItem, totalItems }) => {
  if (totalItems === 0) {
    return <div>No articles found</div>;
  }

  return (
    <div>
      Showing {startItem}-{endItem} of {totalItems} articles
    </div>
  );
};

export default PaginationInfo;
