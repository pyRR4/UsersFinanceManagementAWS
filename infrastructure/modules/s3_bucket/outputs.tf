output "bucket_name" {
  description = "Unikalna, wygenerowana nazwa bucketu S3."
  value       = aws_s3_bucket.this.bucket
}

output "bucket_arn" {
  description = "ARN nowo utworzonego bucketu S3."
  value       = aws_s3_bucket.this.arn
}

output "bucket_domain_name" {
  description = "Nazwa domenowa bucketu S3 (potrzebna dla CloudFront)."
  value       = aws_s3_bucket.this.bucket_domain_name
}