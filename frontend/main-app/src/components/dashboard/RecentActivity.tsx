import { FC } from 'react';
import { Card, Badge } from 'react-bootstrap';
import { Activity } from '../../types/dashboardTypes';
import { formatDate } from '../../utils/dashboardUtils';

interface RecentActivityProps {
  activities: Activity[];
}

const RecentActivity: FC<RecentActivityProps> = ({ activities }) => {
  return (
    <Card className="shadow-sm h-100">
      <Card.Header>
        <h5 className="card-title mb-0">Recent Activity</h5>
      </Card.Header>
      <Card.Body className="p-0">
        <div className="list-group list-group-flush">
          {activities.map(activity => (
            <div key={activity.id} className="list-group-item">
              <div className="d-flex justify-content-between align-items-center">
                <div>
                  <h6 className="mb-1">{activity.action}</h6>
                  <small className="text-muted">
                    {formatDate(activity.timestamp)} • {activity.sourcesCount} sources •{' '}
                    {activity.articlesCount} articles
                  </small>
                </div>
                <Badge bg={activity.status === 'success' ? 'success' : 'danger'}>
                  {activity.status}
                </Badge>
              </div>
            </div>
          ))}
        </div>
      </Card.Body>
    </Card>
  );
};

export default RecentActivity;
