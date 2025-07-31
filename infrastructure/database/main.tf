module "database" {
  source = "./rds_postgres"

  project_name            = var.project_name
  environment             = var.environment
  tags                    = var.tags
  vpc_id                  = var.vpc_id
  public_subnet_ids      = var.public_subnet_ids

  db_username             = var.db_username
  db_password             = var.db_password
}

module "db_password_secret" {
  source = "./secrets_manager"

  secret_name  = "${var.project_name}/${var.environment}/db-password-v6"
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