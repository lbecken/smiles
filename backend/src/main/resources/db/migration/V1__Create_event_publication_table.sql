-- Spring Modulith Event Publication table
-- This table is used by Spring Modulith to track event publications
-- and ensure reliable event processing across modules

CREATE TABLE IF NOT EXISTS event_publication (
    id UUID NOT NULL,
    listener_id VARCHAR(512) NOT NULL,
    event_type VARCHAR(512) NOT NULL,
    serialized_event TEXT NOT NULL,
    publication_date TIMESTAMP NOT NULL,
    completion_date TIMESTAMP,
    PRIMARY KEY (id)
);

-- Index for faster lookups of incomplete events
CREATE INDEX IF NOT EXISTS idx_event_publication_completion_date
    ON event_publication (completion_date);

-- Index for faster lookups by publication date
CREATE INDEX IF NOT EXISTS idx_event_publication_publication_date
    ON event_publication (publication_date);

-- Index for faster lookups by event type
CREATE INDEX IF NOT EXISTS idx_event_publication_event_type
    ON event_publication (event_type);

-- Comment on table
COMMENT ON TABLE event_publication IS 'Spring Modulith event publication log for tracking cross-module events';
