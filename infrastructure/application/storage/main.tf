module "reports_bucket" {
  source        = "./s3_bucket"
  bucket_prefix = "${var.project_name}-${var.environment}-reports"
  tags          = var.tags
}