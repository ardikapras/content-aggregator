import { useState } from 'react';

/**
 * Custom hook to manage mobile sidebar state
 */
export const useSidebar = () => {
  const [show, setShow] = useState(false);

  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);

  return {
    show,
    handleClose,
    handleShow,
  };
};

export default useSidebar;
