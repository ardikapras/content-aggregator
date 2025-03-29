import { FC, useRef, useEffect } from 'react';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler,
  ChartArea
} from 'chart.js';
import { Line } from 'react-chartjs-2';
import { ArticleTrendDto } from '../../services/Api';

// Register Chart.js components
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

interface ArticleTrendChartProps {
  trendData: ArticleTrendDto[];
}

const ArticleTrendChart: FC<ArticleTrendChartProps> = ({ trendData }) => {
  const chartRef = useRef<ChartJS<'line', number[], string> | null>(null);

  // Creates gradient fill effect - safely check if context exists
  const createGradientBackground = (ctx: CanvasRenderingContext2D | null, chartArea: ChartArea) => {
    if (!ctx) return 'rgba(54, 162, 235, 0.5)';

    const gradient = ctx.createLinearGradient(0, chartArea.top, 0, chartArea.bottom);
    gradient.addColorStop(0, 'rgba(54, 162, 235, 0.5)');
    gradient.addColorStop(1, 'rgba(54, 162, 235, 0.05)');
    return gradient;
  };

  // Format dates more user-friendly
  const formatChartDates = (dates: string[]) => {
    return dates.map(date => {
      try {
        const dateObj = new Date(date);
        return dateObj.toLocaleDateString(undefined, {
          month: 'short',
          day: 'numeric'
        });
      } catch (e) {
        return date;
      }
    });
  };

  // Prepare chart data
  const chartData = {
    labels: formatChartDates(trendData.map(item => item.date)),
    datasets: [
      {
        label: 'Articles',
        data: trendData.map(item => item.count),
        borderColor: 'rgba(54, 162, 235, 1)',
        backgroundColor: 'rgba(54, 162, 235, 0.5)', // Default background color
        borderWidth: 2,
        fill: true,
        tension: 0.4, // Smooth curve
        pointRadius: 3,
        pointBackgroundColor: 'rgba(54, 162, 235, 1)',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointHoverRadius: 5,
        pointHoverBackgroundColor: 'rgba(54, 162, 235, 1)',
        pointHoverBorderColor: '#fff',
        pointHoverBorderWidth: 2,
      }
    ]
  };

  // Update chart background when component mounts and the chart is ready
  useEffect(() => {
    const chart = chartRef.current;

    if (!chart) return;

    // Apply gradient after chart is fully initialized
    const applyGradient = () => {
      if (!chart?.ctx || !chart?.chartArea) return;

      const gradient = createGradientBackground(chart.ctx, chart.chartArea);

      if (chart.data.datasets[0]) {
        chart.data.datasets[0].backgroundColor = gradient;
        chart.update('none'); // Update without animation
      }
    };

    // Apply gradient once chart is ready
    applyGradient();

    // Add event listener for chart render
    const resizeObserver = new ResizeObserver(() => {
      if (chart?.ctx && chart?.chartArea) {
        applyGradient();
      }
    });

    // Observe the chart canvas
    const canvas = chart.canvas;
    if (canvas) {
      resizeObserver.observe(canvas);
    }

    // Cleanup
    return () => {
      if (canvas) {
        resizeObserver.unobserve(canvas);
      }
      resizeObserver.disconnect();
    };
  }, [trendData]);

  // Chart options
  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        mode: 'index' as const,
        intersect: false,
        callbacks: {
          title: function(context: any) {
            return context[0]?.dataIndex !== undefined ?
              trendData[context[0].dataIndex]?.date : '';
          }
        }
      },
    },
    scales: {
      x: {
        grid: {
          display: false
        },
        ticks: {
          maxRotation: 0,
          autoSkip: true,
          maxTicksLimit: 10
        }
      },
      y: {
        beginAtZero: true,
        grid: {
          color: 'rgba(0, 0, 0, 0.05)'
        },
        ticks: {
          precision: 0
        }
      }
    },
    interaction: {
      mode: 'nearest' as const,
      axis: 'x' as const,
      intersect: false
    },
    animations: {
      tension: {
        duration: 1000,
        easing: 'linear'
      }
    }
  };

  return (
    <div style={{ height: '300px', width: '100%' }}>
      <Line
        data={chartData}
        options={options}
        ref={chartRef}
      />
    </div>
  );
};

export default ArticleTrendChart;
