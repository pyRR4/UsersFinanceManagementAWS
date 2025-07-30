output "topic_arn" {
  description = "ARN nowo utworzonego tematu SNS."
  value       = aws_sns_topic.this.arn
}

output "topic_name" {
  description = "Nazwa nowo utworzonego tematu SNS."
  value       = aws_sns_topic.this.name
}