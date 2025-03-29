import { useState } from 'react';
import { ArticleDto } from '../services/Api';

/**
 * Custom hook to manage article detail modal state
 */
const useArticleModal = () => {
  const [showModal, setShowModal] = useState(false);
  const [selectedArticle, setSelectedArticle] = useState<ArticleDto | null>(null);

  const openModal = (article: ArticleDto) => {
    setSelectedArticle(article);
    setShowModal(true);
  };

  const closeModal = () => {
    setShowModal(false);
    setSelectedArticle(null);
  };

  return {
    showModal,
    selectedArticle,
    openModal,
    closeModal,
  };
};

export default useArticleModal;
