import { FC } from 'react';
import { Alert, Card, Tabs, Tab } from 'react-bootstrap';
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
          <p className="mb-3 p-2 bg-light rounded">{testResult.author || 'None'}</p>

          <h6>Content Preview:</h6>
          <Card>
            <Card.Body style={{ maxHeight: '400px', overflow: 'auto' }}>
              <Tabs defaultActiveKey="formatted" id="parser-result-tabs">
                <Tab eventKey="formatted" title="Formatted">
                  <div className="mt-3">
                    {testResult.contentPreview?.split('\n').map((paragraph, i) => (
                      <p key={i}>{paragraph}</p>
                    ))}
                  </div>
                </Tab>
                <Tab eventKey="raw" title="Raw">
                  <pre className="mt-3 p-2 bg-light rounded" style={{ whiteSpace: 'pre-wrap' }}>
                    {testResult.contentPreview}
                  </pre>
                </Tab>
              </Tabs>
            </Card.Body>
          </Card>
        </>
      )}
    </Alert>
  );
};

export default ParserTestResults;
