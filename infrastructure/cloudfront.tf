module "cloudfront_log_bucket" {
  source = "./modules/s3_bucket"

  bucket_prefix = "${var.project_name}-cf-logs"
  tags          = var.tags
}

resource "aws_s3_bucket_policy" "cloudfront_logs_policy" {
  bucket = module.cloudfront_log_bucket.bucket_name

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect    = "Allow"
        Principal = {
          Service = "cloudfront.amazonaws.com"
        }
        Action    = "s3:PutObject"
        Resource  = "${module.cloudfront_log_bucket.bucket_arn}/*"
        Condition = {
          StringEquals = {
            "AWS:SourceArn" = aws_cloudfront_distribution.main_distribution.arn
          }
        }
      }
    ]
  })
}

resource "aws_cloudfront_distribution" "main_distribution" {
  enabled         = true
  is_ipv6_enabled = true
  comment         = "Main distribution for the finance application API"

  origin {
    domain_name = split("/", replace(aws_api_gateway_stage.main.invoke_url, "https://", ""))[0]
    origin_path = "/${var.environment}"
    origin_id   = "APIGW-${aws_api_gateway_rest_api.main.name}"

    custom_origin_config {
      http_port              = 80
      https_port             = 443
      origin_protocol_policy = "https-only"
      origin_ssl_protocols   = ["TLSv1.2"]
    }
  }

  default_cache_behavior {
    allowed_methods = ["DELETE", "GET", "HEAD", "OPTIONS", "PATCH", "POST", "PUT"]
    cached_methods  = ["GET", "HEAD"]

    target_origin_id = "APIGW-${aws_api_gateway_rest_api.main.name}"

    viewer_protocol_policy = "redirect-to-https"

    forwarded_values {
      query_string = true
      headers      = ["*"]
      cookies {
        forward = "none"
      }
    }
  }

  logging_config {
    include_cookies = false
    bucket          = module.cloudfront_log_bucket.bucket_domain_name
    prefix          = "cdn-access-logs/"
  }

  viewer_certificate {
    cloudfront_default_certificate = true
  }

  restrictions {
    geo_restriction {
      restriction_type = "none"
    }
  }

  web_acl_id = null

  tags = var.tags
}

resource "aws_api_gateway_rest_api" "main" {
  name        = "${var.project_name}-${var.environment}-api"
  description = "API dla aplikacji do zarządzania finansami."
  tags        = var.tags

  endpoint_configuration {
    types = ["REGIONAL"]
  }
}
