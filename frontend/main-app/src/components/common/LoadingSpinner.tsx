import { FC } from 'react';
import { Spinner, Container } from 'react-bootstrap';

interface LoadingSpinnerProps {
  message?: string;
}

const LoadingSpinner: FC<LoadingSpinnerProps> = ({ message = 'Loading...' }) => {
  return (
    <Container className="text-center py-5">
      <Spinner animation="border" role="status">
        <span className="visually-hidden">{message}</span>
      </Spinner>
      <p className="mt-3">{message}</p>
    </Container>
  );
};

export default LoadingSpinner;
