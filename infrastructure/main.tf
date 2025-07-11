module "user_pool" {
  source = "./modules/cognito_user_pool"

  pool_name       = "${var.project_name}-${var.environment}-user-pool"
  app_client_name = "${var.project_name}-${var.environment}-app-client"
  tags            = var.tags
}

resource "aws_cognito_user" "test_user" {
  user_pool_id = module.user_pool.pool_id

  username = "igopood33@gmail.com"
  password = "th3Ziemni@czek"

  attributes = {
    email          = "igopood33@gmail.com"
    email_verified = true
  }
  message_action = "SUPPRESS"
  lifecycle {
    ignore_changes = [password]
  }
}

module "report_notifications_topic" {
  source     = "./modules/sns_topic"
  topic_name = "${var.project_name}-${var.environment}-report-notifications"
  tags       = var.tags
}

module "report_jobs_queue" {
  source     = "./modules/sqs_queue"
  queue_name = "${var.project_name}-${var.environment}-report-jobs"
  is_fifo    = true
  tags       = var.tags
}

module "reports_bucket" {
  source        = "./modules/s3_bucket"
  bucket_prefix = "${var.project_name}-${var.environment}-reports"
  tags          = var.tags
}

module "vpc" {
  source       = "./modules/vpc"
  project_name = var.project_name
  aws_region   = var.aws_region
  tags         = var.tags
}

resource "aws_security_group" "lambda_sg" {
  name        = "${var.project_name}-${var.environment}-lambda-sg"
  description = "Allows egress traffic for Lambda functions"
  vpc_id      = module.vpc.vpc_id

  ingress {
    protocol        = "tcp"
    from_port       = 5432
    to_port         = 5432
    self            = true
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = var.tags
}

module "database" {
  source = "./modules/rds_postgres"

  project_name            = var.project_name
  environment             = var.environment
  tags                    = var.tags
  vpc_id                  = module.vpc.vpc_id
  public_subnet_ids      = module.vpc.public_subnet_ids

  db_username             = var.db_username
  db_password             = var.db_password
}

module "db_password_secret" {
  source = "./modules/secrets_manager"

  secret_name  = "${var.project_name}/${var.environment}/db-password-v2"
  secret_value = var.db_password
  tags         = var.tags
}

resource "aws_secretsmanager_secret_version" "db_credentials_version" {
  secret_id = module.db_password_secret.secret_id

  secret_string = jsonencode({
    username = var.db_username
    password = var.db_password
    dbname   = module.database.db_connection_data.dbname
    host     = module.database.db_instance_endpoint
    port     = module.database.db_connection_data.port
  })
}