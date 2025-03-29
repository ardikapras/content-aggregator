import { FC, useRef, useEffect, ChangeEvent } from 'react';
import { Card, Form } from 'react-bootstrap';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
  ChartData,
  ChartOptions,
} from 'chart.js';
import { Bar } from 'react-chartjs-2';

// Register ChartJS components
ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

interface BarChartProps {
  chartData: ChartData<'bar'>;
  dateRange: string;
  onDateRangeChange: (range: string) => void;
}

const BarChart: FC<BarChartProps> = ({ chartData, dateRange, onDateRangeChange }) => {
  const chartRef = useRef<ChartJS<'bar'> | null>(null);

  useEffect(() => {
    // Store the current value of the ref in a variable
    const chart = chartRef.current;

    // Return cleanup function
    return () => {
      if (chart) {
        chart.destroy();
      }
    };
  }, []);

  const handleDateRangeChange = (e: ChangeEvent<HTMLSelectElement>) => {
    onDateRangeChange(e.target.value);
  };

  const options: ChartOptions<'bar'> = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
      },
    },
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: false,
      },
    },
  };

  return (
    <Card className="shadow-sm mb-4">
      <Card.Header className="d-flex justify-content-between align-items-center">
        <h5 className="mb-0">Daily Scraping Activity</h5>
        <Form.Select value={dateRange} onChange={handleDateRangeChange} style={{ width: 'auto' }}>
          <option value="7">Last 7 days</option>
          <option value="14">Last 14 days</option>
          <option value="30">Last 30 days</option>
        </Form.Select>
      </Card.Header>
      <Card.Body>
        <div style={{ height: '300px' }}>
          <Bar
            data={chartData}
            options={options}
            ref={chartRef} // Set ref to access chart instance
          />
        </div>
      </Card.Body>
    </Card>
  );
};

export default BarChart;
