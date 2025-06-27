output "secret_arn" {
  description = "ARN nowo utworzonego sekretu."
  value       = aws_secretsmanager_secret.this.arn
}

output "secret_id" {
  description = "ID nowo utworzonego sekretu."
  value       = aws_secretsmanager_secret.this.id
}