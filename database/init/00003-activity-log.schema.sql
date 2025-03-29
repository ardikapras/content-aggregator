-- Set the schema
SET search_path TO news, public;

-- Create activity_logs table
CREATE TABLE activity_logs
(
    id         UUID PRIMARY KEY            DEFAULT uuid_generate_v4(),
    timestamp  TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    action     VARCHAR(50)                                           NOT NULL,
    status     VARCHAR(20)                                           NOT NULL,
    details    TEXT,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Create index on timestamp for faster queries based on time
CREATE INDEX idx_activity_logs_timestamp ON activity_logs (timestamp);

-- Create index on status for faster filtering by status
CREATE INDEX idx_activity_logs_status ON activity_logs (status);

-- Apply the update timestamp trigger 
CREATE TRIGGER update_activity_logs_timestamp
    BEFORE UPDATE
    ON activity_logs
    FOR EACH ROW
EXECUTE FUNCTION update_timestamp();

-- Add comment to the table
COMMENT ON TABLE activity_logs IS 'Logs of scraper activity and operations';
