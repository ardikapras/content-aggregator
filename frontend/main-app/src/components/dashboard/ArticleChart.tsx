import { FC } from 'react';
import { Card } from 'react-bootstrap';
import { Line } from 'react-chartjs-2';
import {
  Chart,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import { ChartData } from '../../types/dashboardTypes';

Chart.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

interface ArticleChartProps {
  chartData: ChartData;
}

const ArticleChart: FC<ArticleChartProps> = ({ chartData }) => {
  return (
    <Card className="shadow-sm h-100">
      <Card.Header>
        <h5 className="card-title mb-0">Articles Collection Trend</h5>
      </Card.Header>
      <Card.Body>
        <Line data={chartData} options={{ maintainAspectRatio: false, height: 250 }} />
      </Card.Body>
    </Card>
  );
};

export default ArticleChart;
