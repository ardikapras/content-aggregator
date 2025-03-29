import { FC } from 'react';
import { Pagination } from 'react-bootstrap';

interface ArticlePaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
}

const ArticlePagination: FC<ArticlePaginationProps> = ({
  currentPage,
  totalPages,
  onPageChange,
}) => {
  const paginationItems = [];

  paginationItems.push(
    <Pagination.Prev
      key="prev"
      onClick={() => onPageChange(Math.max(0, currentPage - 1))}
      disabled={currentPage === 0}
    />
  );

  const startPage = Math.max(0, currentPage - 2);
  const endPage = Math.min(totalPages - 1, currentPage + 2);

  if (startPage > 0) {
    paginationItems.push(
      <Pagination.Item key={0} onClick={() => onPageChange(0)}>
        1
      </Pagination.Item>
    );
    if (startPage > 1) {
      paginationItems.push(<Pagination.Ellipsis key="ellipsis1" />);
    }
  }

  for (let i = startPage; i <= endPage; i++) {
    paginationItems.push(
      <Pagination.Item key={i} active={i === currentPage} onClick={() => onPageChange(i)}>
        {i + 1}
      </Pagination.Item>
    );
  }

  if (endPage < totalPages - 1) {
    if (endPage < totalPages - 2) {
      paginationItems.push(<Pagination.Ellipsis key="ellipsis2" />);
    }
    paginationItems.push(
      <Pagination.Item key={totalPages - 1} onClick={() => onPageChange(totalPages - 1)}>
        {totalPages}
      </Pagination.Item>
    );
  }

  paginationItems.push(
    <Pagination.Next
      key="next"
      onClick={() => onPageChange(Math.min(totalPages - 1, currentPage + 1))}
      disabled={currentPage >= totalPages - 1}
    />
  );

  return totalPages > 1 ? <Pagination>{paginationItems}</Pagination> : null;
};

export default ArticlePagination;
