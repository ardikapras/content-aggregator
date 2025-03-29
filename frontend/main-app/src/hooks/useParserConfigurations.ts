import { useState, useEffect, ChangeEvent, FormEvent } from 'react';
import apiService, {
  ParserConfigDto,
  CreateParserConfigRequest,
  ParserTestRequest,
  ParserTestResponse
} from '../services/Api';

export interface ParserFormData {
  name: string;
  description: string;
  authorSelectors: string;
  contentSelectors: string;
  nextPageSelector: string;
  contentFilters: string;
}

const INITIAL_FORM_DATA: ParserFormData = {
  name: '',
  description: '',
  authorSelectors: '',
  contentSelectors: '',
  nextPageSelector: '',
  contentFilters: '',
};

export const useParserConfigurations = () => {
  const [parsers, setParsers] = useState<ParserConfigDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showModal, setShowModal] = useState(false);
  const [currentParser, setCurrentParser] = useState<ParserConfigDto | null>(null);
  const [formData, setFormData] = useState<ParserFormData>(INITIAL_FORM_DATA);
  const [testUrl, setTestUrl] = useState('');
  const [testLoading, setTestLoading] = useState(false);
  const [testResult, setTestResult] = useState<ParserTestResponse | null>(null);
  const [showTestResults, setShowTestResults] = useState(false);

  const fetchParsers = async () => {
    try {
      setLoading(true);
      const data = await apiService.getParserConfigs();
      setParsers(data);
      setError(null);
    } catch (err) {
      console.error('Error fetching parser configurations:', err);
      setError('Failed to load parser configurations');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    void fetchParsers();
  }, []);

  const handleAddParser = () => {
    setCurrentParser(null);
    setFormData(INITIAL_FORM_DATA);
    setTestUrl('');
    setTestResult(null);
    setShowTestResults(false);
    setShowModal(true);
  };

  const handleEditParser = (parser: ParserConfigDto) => {
    setCurrentParser(parser);
    setFormData({
      name: parser.name,
      description: parser.description || '',
      authorSelectors: parser.authorSelectors.join('\n'),
      contentSelectors: parser.contentSelectors.join('\n'),
      nextPageSelector: parser.nextPageSelector || '',
      contentFilters: parser.contentFilters.join('\n'),
    });
    setTestUrl('');
    setTestResult(null);
    setShowTestResults(false);
    setShowModal(true);
  };

  const handleDeleteParser = async (parser: ParserConfigDto) => {
    if (window.confirm(`Are you sure you want to delete ${parser.name}?`)) {
      try {
        setLoading(true);
        await apiService.deleteParserConfig(parser.id);
        setParsers(prev => prev.filter(p => p.id !== parser.id));
        setError(null);
      } catch (err) {
        console.error('Error deleting parser configuration:', err);
        setError('Failed to delete parser configuration');
      } finally {
        setLoading(false);
      }
    }
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const handleInputChange = (e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();

    try {
      setLoading(true);

      const parserConfig = {
        name: formData.name,
        description: formData.description,
        authorSelectors: formData.authorSelectors.split('\n').filter(s => s.trim() !== ''),
        contentSelectors: formData.contentSelectors.split('\n').filter(s => s.trim() !== ''),
        nextPageSelector: formData.nextPageSelector || undefined,
        contentFilters: formData.contentFilters.split('\n').filter(s => s.trim() !== ''),
      };

      if (currentParser) {
        await apiService.updateParserConfig(currentParser.id, parserConfig);
      } else {
        await apiService.createParserConfig(parserConfig as CreateParserConfigRequest);
      }

      setShowModal(false);
      await fetchParsers();
      setError(null);
    } catch (err) {
      console.error(`Failed to ${currentParser ? 'update' : 'create'} parser configuration:`, err);
      setError(`Failed to ${currentParser ? 'update' : 'create'} parser configuration`);
    } finally {
      setLoading(false);
    }
  };

  const handleTestParser = async () => {
    if (!testUrl) {
      alert('Please enter a URL to test');
      return;
    }

    setTestLoading(true);
    setTestResult(null);
    setShowTestResults(false);

    try {
      const testRequest: ParserTestRequest = {
        url: testUrl,
        config: {
          name: formData.name || 'Test Parser',
          description: formData.description || 'Temporary parser for testing',
          authorSelectors: formData.authorSelectors.split('\n').filter(s => s.trim() !== ''),
          contentSelectors: formData.contentSelectors.split('\n').filter(s => s.trim() !== ''),
          nextPageSelector: formData.nextPageSelector || undefined,
          contentFilters: formData.contentFilters.split('\n').filter(s => s.trim() !== ''),
        },
      };

      const result = await apiService.testParserConfig(testRequest);
      if (result.success && result.contentPreview) {
        setTestResult({
          ...result,
          contentPreview: result.contentPreview
        });
      } else {
        setTestResult(result);
      }
      setShowTestResults(true);
    } catch (err) {
      console.error('Error testing parser configuration:', err);
      setTestResult({
        success: false,
        message: 'Failed to test parser configuration. Check the URL and try again.',
      });
      setShowTestResults(true);
    } finally {
      setTestLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    try {
      const date = new Date(dateString);
      return date.toLocaleDateString() + ' ' + date.toLocaleTimeString();
    } catch (e) {
      console.error(e);
      return dateString;
    }
  };

  return {
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
  };
};

export default useParserConfigurations;
