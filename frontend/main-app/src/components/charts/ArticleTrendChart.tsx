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
  ChartArea,
  ChartData,
  ChartOptions,
  TooltipItem,
} from 'chart.js';
import { Line } from 'react-chartjs-2';
import { ArticleTrendDto } from '../../services/Api';

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
  const chartRef = useRef<ChartJS<'line'> | null>(null);

  const createGradientBackground = (ctx: CanvasRenderingContext2D | null, chartArea: ChartArea) => {
    if (!ctx) return 'rgba(54, 162, 235, 0.5)';

    const gradient = ctx.createLinearGradient(0, chartArea.top, 0, chartArea.bottom);
    gradient.addColorStop(0, 'rgba(54, 162, 235, 0.5)');
    gradient.addColorStop(1, 'rgba(54, 162, 235, 0.05)');
    return gradient;
  };

  const formatChartDates = (dates: string[]) => {
    return dates.map(date => {
      try {
        const dateObj = new Date(date);
        return dateObj.toLocaleDateString(undefined, {
          month: 'short',
          day: 'numeric',
        });
      } catch (e) {
        console.error(e);
        return date;
      }
    });
  };

  const chartData = {
    labels: formatChartDates(trendData.map(item => item.date)),
    datasets: [
      {
        label: 'Articles',
        data: trendData.map(item => item.count),
        borderColor: 'rgba(54, 162, 235, 1)',
        backgroundColor: 'rgba(54, 162, 235, 0.5)',
        borderWidth: 2,
        fill: true,
        tension: 0.4,
        pointRadius: 3,
        pointBackgroundColor: 'rgba(54, 162, 235, 1)',
        pointBorderColor: '#fff',
        pointBorderWidth: 2,
        pointHoverRadius: 5,
        pointHoverBackgroundColor: 'rgba(54, 162, 235, 1)',
        pointHoverBorderColor: '#fff',
        pointHoverBorderWidth: 2,
      },
    ],
  } as ChartData<'line', number[]>;

  useEffect(() => {
    const chart = chartRef.current;

    if (!chart) return;

    const applyGradient = () => {
      if (!chart?.ctx || !chart?.chartArea) return;

      const gradient = createGradientBackground(chart.ctx, chart.chartArea);

      if (chart.data.datasets[0]) {
        const dataset = chart.data.datasets[0] as {
          backgroundColor: string | CanvasGradient;
        };
        dataset.backgroundColor = gradient;
        chart.update('none');
      }
    };

    applyGradient();

    const resizeObserver = new ResizeObserver(() => {
      if (chart?.ctx && chart?.chartArea) {
        applyGradient();
      }
    });

    const canvas = chart.canvas;
    if (canvas) {
      resizeObserver.observe(canvas);
    }

    return () => {
      if (canvas) {
        resizeObserver.unobserve(canvas);
      }
      resizeObserver.disconnect();
    };
  }, [trendData]);

  const options: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
      tooltip: {
        mode: 'index',
        intersect: false,
        callbacks: {
          title: function (context: TooltipItem<'line'>[]) {
            return context[0]?.dataIndex !== undefined ? trendData[context[0].dataIndex]?.date : '';
          },
        },
      },
    },
    scales: {
      x: {
        grid: {
          display: false,
        },
        ticks: {
          maxRotation: 0,
          autoSkip: true,
          maxTicksLimit: 10,
        },
      },
      y: {
        beginAtZero: true,
        grid: {
          color: 'rgba(0, 0, 0, 0.05)',
        },
        ticks: {
          precision: 0,
        },
      },
    },
    interaction: {
      mode: 'nearest',
      axis: 'x',
      intersect: false,
    },
    animations: {
      tension: {
        duration: 1000,
        easing: 'linear',
      },
    },
  };

  return (
    <div style={{ height: '300px', width: '100%' }}>
      <Line data={chartData} options={options} ref={chartRef} />
    </div>
  );
};

export default ArticleTrendChart;
