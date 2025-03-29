import { FC } from 'react';
import { Card } from 'react-bootstrap';
import { LoadingSpinner, ErrorMessage } from './common';
import { ParserConfigTable, ParserConfigModal, ParserConfigHeader } from './parsers';
import { useParserConfigurations } from '../hooks';

const ParserConfigurations: FC = () => {
  const {
    parsers,
    loading,
    error,
    showModal,
    currentParser,
    formData,
    testUrl,
    testLoading,
    testResult,
    showTestResults,
    handleAddParser,
    handleEditParser,
    handleDeleteParser,
    handleCloseModal,
    handleInputChange,
    handleSubmit,
    setTestUrl,
    handleTestParser,
    formatDate,
  } = useParserConfigurations();

  if (loading) {
    return <LoadingSpinner message="Loading parser configurations..." />;
  }

  if (error) {
    return <ErrorMessage message={error} />;
  }

  return (
    <div className="parser-configs">
      <ParserConfigHeader onAddNew={handleAddParser} />

      {parsers.length === 0 ? (
        <Card body className="text-center py-5 text-muted">
          <p>No parser configurations found. Add a new parser to get started.</p>
        </Card>
      ) : (
        <ParserConfigTable
          parsers={parsers}
          onEdit={handleEditParser}
          onDelete={handleDeleteParser}
          formatDate={formatDate}
        />
      )}

      <ParserConfigModal
        show={showModal}
        onClose={handleCloseModal}
        currentParser={currentParser}
        formData={formData}
        onChange={handleInputChange}
        onSubmit={handleSubmit}
        testUrl={testUrl}
        setTestUrl={setTestUrl}
        onTest={handleTestParser}
        testLoading={testLoading}
        testResult={testResult}
        showTestResults={showTestResults}
      />
    </div>
  );
};

export default ParserConfigurations;
