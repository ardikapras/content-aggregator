import { useState, useEffect } from 'react';
import { ParserConfigDto, ParserFormData, ParserTestResponse } from '../types/parserTypes';

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

  useEffect(() => {
    fetchParsers();
  }, []);

  const fetchParsers = async () => {
    try {
      setLoading(true);
      setTimeout(() => {
        const mockParsers: ParserConfigDto[] = [
          {
            id: '1',
            name: 'ANTARA',
            description: 'Parser for Antara News',
            authorSelectors: ['script[type=application/ld+json]'],
            contentSelectors: ['div.post-content'],
            contentFilters: ['span.baca-juga', 'p.text-muted'],
            createdAt: '2023-06-15T10:30:00',
            updatedAt: '2023-12-20T14:45:00',
          },
          {
            id: '2',
            name: 'CNBC',
            description: 'Parser for CNBC Indonesia',
            authorSelectors: ['script[type=application/ld+json]'],
            contentSelectors: ['.detail-text', 'article p', '.article-content p'],
            contentFilters: ['.media-institusi'],
            createdAt: '2023-06-15T10:30:00',
            updatedAt: '2023-12-21T09:15:00',
          },
          {
            id: '3',
            name: 'CNN',
            description: 'Parser for CNN Indonesia',
            authorSelectors: ['meta[name=author]', 'meta[name=content_author]'],
            contentSelectors: ['.detail-text p', '.detail-wrap p', '.content-artikel p'],
            contentFilters: ['.para_caption'],
            createdAt: '2023-06-16T08:45:00',
            updatedAt: '2023-12-19T11:30:00',
          },
          {
            id: '4',
            name: 'DEFAULT',
            description: 'Default parser configuration',
            authorSelectors: ['script[type=application/ld+json]', 'meta[name=author]'],
            contentSelectors: ['.content p', 'main p', 'article p'],
            contentFilters: [],
            createdAt: '2023-06-14T09:00:00',
            updatedAt: '2023-12-18T16:20:00',
          },
        ];
        setParsers(mockParsers);
        setLoading(false);
      }, 1000);
    } catch (err) {
      console.error('Error fetching parser configurations:', err);
      setError('Failed to load parser configurations');
      setLoading(false);
    }
  };

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

  const handleDeleteParser = (parser: ParserConfigDto) => {
    if (window.confirm(`Are you sure you want to delete ${parser.name}?`)) {
      setParsers(prev => prev.filter(p => p.id !== parser.id));
    }
  };

  const handleCloseModal = () => {
    setShowModal(false);
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const newConfig: Partial<ParserConfigDto> = {
      name: formData.name,
      description: formData.description,
      authorSelectors: formData.authorSelectors.split('\n').filter(s => s.trim() !== ''),
      contentSelectors: formData.contentSelectors.split('\n').filter(s => s.trim() !== ''),
      nextPageSelector: formData.nextPageSelector || undefined,
      contentFilters: formData.contentFilters.split('\n').filter(s => s.trim() !== ''),
    };

    if (currentParser) {
      setParsers(prevParsers =>
        prevParsers.map(p =>
          p.id === currentParser.id
            ? { ...p, ...newConfig, updatedAt: new Date().toISOString() }
            : p
        )
      );
    } else {
      const newId = Math.max(...parsers.map(p => parseInt(p.id))) + 1;
      setParsers(prevParsers => [
        ...prevParsers,
        {
          id: newId.toString(),
          ...(newConfig as ParserConfigDto),
          createdAt: new Date().toISOString(),
          updatedAt: new Date().toISOString(),
        },
      ]);
    }

    handleCloseModal();
  };

  const handleTestParser = () => {
    if (!testUrl) {
      alert('Please enter a URL to test');
      return;
    }

    setTestLoading(true);
    setTestResult(null);

    setTimeout(() => {
      setTestResult({
        author: 'John Doe',
        contentPreview:
          'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nullam euismod, nisl eget ultricies ultricies, nunc nisl ultricies nunc, quis ultricies nisl nisl eget ultricies ultricies. Nullam euismod, nisl eget ultricies ultricies, nunc nisl ultricies nunc, quis ultricies nisl nisl eget ultricies ultricies.',
        success: true,
        message: 'Parser test successful',
      });
      setTestLoading(false);
      setShowTestResults(true);
    }, 2000);
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
