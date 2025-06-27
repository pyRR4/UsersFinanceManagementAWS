output "api_gateway_invoke_url" {
  description = "Główny adres URL do wywoływania naszego API (etap 'dev')."
  value       = aws_api_gateway_stage.main.invoke_url
}

output "cognito_user_pool_id" {
  description = "ID puli użytkowników Cognito."
  value       = module.user_pool.pool_id
}

output "cognito_user_pool_client_id" {
  description = "ID klienta aplikacji Cognito"
  value       = module.user_pool.client_id
}

output "rds_database_endpoint" {
  description = "Adres (host) bazy danych RDS."
  value       = module.database.db_instance_endpoint
  sensitive   = true # Ukrywamy go, bo to wrażliwa informacja
}

output "s3_reports_bucket_name" {
  description = "Nazwa bucketu S3, w którym będą przechowywane raporty."
  value       = module.reports_bucket.bucket_name
}