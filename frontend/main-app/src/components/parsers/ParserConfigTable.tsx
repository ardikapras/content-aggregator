import { FC } from 'react';
import { Table, Badge, Button } from 'react-bootstrap';
import { ParserConfigDto } from '../../types/parserTypes';

interface ParserConfigTableProps {
  parsers: ParserConfigDto[];
  onEdit: (parser: ParserConfigDto) => void;
  onDelete: (parser: ParserConfigDto) => void;
  formatDate: (dateString: string) => string;
}

const ParserConfigTable: FC<ParserConfigTableProps> = ({
  parsers,
  onEdit,
  onDelete,
  formatDate,
}) => {
  return (
    <Table striped bordered hover responsive>
      <thead>
        <tr>
          <th>Name</th>
          <th>Description</th>
          <th>Selectors</th>
          <th>Last Updated</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        {parsers.map(parser => (
          <tr key={parser.id}>
            <td className="fw-bold">{parser.name}</td>
            <td>{parser.description}</td>
            <td>
              <Badge bg="info" className="me-1">
                {parser.authorSelectors.length} author
              </Badge>
              <Badge bg="success" className="me-1">
                {parser.contentSelectors.length} content
              </Badge>
              {parser.contentFilters.length > 0 && (
                <Badge bg="warning">{parser.contentFilters.length} filters</Badge>
              )}
            </td>
            <td>{formatDate(parser.updatedAt)}</td>
            <td>
              <div className="d-flex gap-2">
                <Button
                  variant="outline-secondary"
                  size="sm"
                  className="me-2"
                  onClick={() => onEdit(parser)}
                >
                  Edit
                </Button>
                <Button variant="outline-danger" size="sm" onClick={() => onDelete(parser)}>
                  Delete
                </Button>
              </div>
            </td>
          </tr>
        ))}
      </tbody>
    </Table>
  );
};

export default ParserConfigTable;
