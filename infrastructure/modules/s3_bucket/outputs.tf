output "bucket_name" {
  description = "Unikalna, wygenerowana nazwa bucketu S3."
  value       = aws_s3_bucket.this.bucket
}

output "bucket_arn" {
  description = "ARN nowo utworzonego bucketu S3."
  value       = aws_s3_bucket.this.arn
}