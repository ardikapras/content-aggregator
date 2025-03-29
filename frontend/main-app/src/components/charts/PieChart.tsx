import { FC, useRef, useEffect } from 'react';
import { Card } from 'react-bootstrap';
import { Chart as ChartJS, ArcElement, Tooltip, Legend, ChartData, ChartOptions } from 'chart.js';
import { Pie } from 'react-chartjs-2';

ChartJS.register(ArcElement, Tooltip, Legend);

interface PieChartProps {
  chartData: ChartData<'pie'>;
}

const PieChart: FC<PieChartProps> = ({ chartData }) => {
  const chartRef = useRef<ChartJS<'pie'> | null>(null);

  useEffect(() => {
    const chart = chartRef.current;

    return () => {
      if (chart) {
        chart.destroy();
      }
    };
  }, []);

  const options: ChartOptions<'pie'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'right',
      },
      title: {
        display: false,
      },
    },
  };

  return (
    <Card className="shadow-sm mb-4 h-100">
      <Card.Header>
        <h5 className="mb-0">Article Status Distribution</h5>
      </Card.Header>
      <Card.Body className="d-flex justify-content-center">
        <div style={{ height: '300px', width: '300px' }}>
          <Pie data={chartData} options={options} ref={chartRef} />
        </div>
      </Card.Body>
    </Card>
  );
};

export default PieChart;
