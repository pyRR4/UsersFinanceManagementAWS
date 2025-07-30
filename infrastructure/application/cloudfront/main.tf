module "log_bucket" {
  source        = "../storage/s3_bucket"
  bucket_prefix = "${var.project_name}-cf-logs"
  tags          = var.tags
}

resource "aws_s3_bucket_policy" "cloudfront_logs_policy" {
  bucket = module.log_bucket.bucket_name

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Service = "cloudfront.amazonaws.com"
        },
        Action = "s3:PutObject",
        Resource = "${module.log_bucket.bucket_arn}/*",
        Condition = {
          StringEquals = {
            "AWS:SourceArn" = aws_cloudfront_distribution.main.arn
          }
        }
      }
    ]
  })
}

resource "aws_cloudfront_distribution" "main" {
  enabled         = true
  is_ipv6_enabled = true
  comment         = "Main distribution for the finance application API"

  origin {
    domain_name = split("/", replace(var.api_gateway_invoke_url, "https://", ""))[0]
    origin_path = "/${var.environment}"
    origin_id   = "APIGW-${var.api_gateway_name}"

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
    target_origin_id = "APIGW-${var.api_gateway_name}"

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
    bucket          = module.log_bucket.bucket_domain_name
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
