CREATE TABLE favorite_apps (
  app_id BIGINT NOT NULL REFERENCES apps (id),
  user_id BIGINT NOT NULL REFERENCES users (id)
);
