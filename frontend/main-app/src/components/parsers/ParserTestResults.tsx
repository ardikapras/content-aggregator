import { FC } from 'react';
import { Alert } from 'react-bootstrap';
import { ParserTestResponse } from '../../types/parserTypes';

interface ParserTestResultsProps {
  testResult: ParserTestResponse | null;
  showResults: boolean;
}

const ParserTestResults: FC<ParserTestResultsProps> = ({ testResult, showResults }) => {
  if (!showResults || !testResult) return null;

  return (
    <Alert variant={testResult.success ? 'success' : 'danger'}>
      <Alert.Heading>
        {testResult.success ? 'Parser Test Successful' : 'Parser Test Failed'}
      </Alert.Heading>
      {testResult.message && <p>{testResult.message}</p>}

      {testResult.success && (
        <>
          <h6>Extracted Author:</h6>
          <p className="mb-3">{testResult.author || 'None'}</p>

          <h6>Content Preview:</h6>
          <div className="bg-light p-3 rounded" style={{ maxHeight: '200px', overflow: 'auto' }}>
            {testResult.contentPreview}
          </div>
        </>
      )}
    </Alert>
  );
};

export default ParserTestResults;
