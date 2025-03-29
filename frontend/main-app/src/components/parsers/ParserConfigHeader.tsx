import { FC } from 'react';
import { Button } from 'react-bootstrap';

interface ParserConfigHeaderProps {
  onAddNew: () => void;
}

const ParserConfigHeader: FC<ParserConfigHeaderProps> = ({ onAddNew }) => {
  return (
    <div className="d-flex justify-content-between align-items-center mb-4">
      <h2>Parser Configurations</h2>
      <Button variant="primary" onClick={onAddNew}>
        Add New Parser
      </Button>
    </div>
  );
};

export default ParserConfigHeader;
