CREATE DATABASE IF NOT EXISTS finflow_auth
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS finflow_application
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS finflow_document
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS finflow_admin
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'finflow_auth_user'@'%' IDENTIFIED BY 'FinflowAuth@2026!';
CREATE USER IF NOT EXISTS 'finflow_application_user'@'%' IDENTIFIED BY 'FinflowApplication@2026!';
CREATE USER IF NOT EXISTS 'finflow_document_user'@'%' IDENTIFIED BY 'FinflowDocument@2026!';
CREATE USER IF NOT EXISTS 'finflow_admin_user'@'%' IDENTIFIED BY 'FinflowAdmin@2026!';

GRANT ALL PRIVILEGES ON finflow_auth.* TO 'finflow_auth_user'@'%';
GRANT ALL PRIVILEGES ON finflow_application.* TO 'finflow_application_user'@'%';
GRANT ALL PRIVILEGES ON finflow_document.* TO 'finflow_document_user'@'%';
GRANT ALL PRIVILEGES ON finflow_admin.* TO 'finflow_admin_user'@'%';

FLUSH PRIVILEGES;
