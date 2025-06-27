output "db_instance_arn" {
  description = "ARN instancji bazy danych."
  value       = aws_db_instance.this.arn
}

output "db_instance_endpoint" {
  description = "Adres (host) bazy danych do połączeń."
  value       = aws_db_instance.this.endpoint
}

output "db_connection_data" {
  description = "Dane potrzebne do połączenia z bazą i do stworzenia sekretu."
  value = {
    username = aws_db_instance.this.username
    password = aws_db_instance.this.password
    dbname   = aws_db_instance.this.db_name
    host     = aws_db_instance.this.address
    port     = aws_db_instance.this.port
  }
  sensitive = true
}