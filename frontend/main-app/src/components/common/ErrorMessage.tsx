import { FC } from 'react';
import { Container } from 'react-bootstrap';

interface ErrorMessageProps {
  message: string;
}

const ErrorMessage: FC<ErrorMessageProps> = ({ message }) => {
  return (
    <Container className="text-center py-5 text-danger">
      <p>{message}</p>
    </Container>
  );
};

export default ErrorMessage;
