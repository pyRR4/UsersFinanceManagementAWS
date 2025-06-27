output "pool_arn" {
  description = "ARN nowo utworzonej puli użytkowników Cognito."
  value       = aws_cognito_user_pool.this.arn
}

output "pool_id" {
  description = "ID nowo utworzonej puli użytkowników Cognito."
  value       = aws_cognito_user_pool.this.id
}

output "client_id" {
  description = "ID klienta aplikacji."
  value       = aws_cognito_user_pool_client.this.id
}