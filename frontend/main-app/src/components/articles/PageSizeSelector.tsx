import { ChangeEvent, FC } from 'react';
import { Form } from 'react-bootstrap';

interface PageSizeSelectorProps {
  pageSize: number;
  onPageSizeChange: (size: number) => void;
}

const PageSizeSelector: FC<PageSizeSelectorProps> = ({ pageSize, onPageSizeChange }) => {
  const handleChange = (e: ChangeEvent<HTMLSelectElement>) => {
    onPageSizeChange(parseInt(e.target.value));
  };

  return (
    <Form.Group className="me-3">
      <Form.Label>Page Size</Form.Label>
      <Form.Select value={pageSize} onChange={handleChange} className="form-select-sm">
        <option value="5">5</option>
        <option value="10">10</option>
        <option value="20">20</option>
        <option value="50">50</option>
      </Form.Select>
    </Form.Group>
  );
};

export default PageSizeSelector;
