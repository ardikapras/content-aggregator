import { ChangeEvent, FC } from 'react';
import { InputGroup, Form, Button } from 'react-bootstrap';

interface SearchBarProps {
  searchTerm: string;
  onSearchChange: (term: string) => void;
  onClear: () => void;
}

const SearchBar: FC<SearchBarProps> = ({ searchTerm, onSearchChange, onClear }) => {
  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    onSearchChange(e.target.value);
  };

  return (
    <InputGroup className="mb-3">
      <Form.Control placeholder="Search articles..." value={searchTerm} onChange={handleChange} />
      {searchTerm && (
        <Button variant="outline-secondary" onClick={onClear}>
          Clear
        </Button>
      )}
    </InputGroup>
  );
};

export default SearchBar;
