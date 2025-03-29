import { useMemo } from 'react';
import { ChartData } from '../types/dashboardTypes';

/**
 * Custom hook to provide chart data for the dashboard
 */
const useChartData = () => {
  return useMemo<ChartData>(
    () => ({
      labels: [
        '7 days ago',
        '6 days ago',
        '5 days ago',
        '4 days ago',
        '3 days ago',
        '2 days ago',
        'Yesterday',
        'Today',
      ],
      datasets: [
        {
          label: 'Articles Collected',
          data: [65, 78, 52, 91, 43, 58, 47, 36],
          fill: false,
          backgroundColor: 'rgba(75,192,192,0.4)',
          borderColor: 'rgba(75,192,192,1)',
        },
      ],
    }),
    []
  );
};

export default useChartData;
