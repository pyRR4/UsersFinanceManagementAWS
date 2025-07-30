output "queue_arn" {
  description = "ARN nowo utworzonej kolejki SQS."
  value       = aws_sqs_queue.this.arn
}

output "queue_url" {
  description = "URL nowo utworzonej kolejki SQS (używany jako jej ID)."
  value       = aws_sqs_queue.this.id
}