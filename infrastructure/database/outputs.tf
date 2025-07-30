output "db_instance_endpoint" {
  value = module.database.db_instance_endpoint
}

output "db_connection_data" {
  value = module.database.db_connection_data
}

output "db_secret_id" {
  value = module.db_password_secret.secret_id
}