data "aws_iam_policy" "lambda_basic_execution" {
  arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

data "aws_iam_policy" "lambda_vpc_access" {
  arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

data "aws_iam_policy_document" "app_lambda_permissions" {
  statement {
    sid       = "AllowReadingDBSecret"
    effect    = "Allow"
    actions   = ["secretsmanager:GetSecretValue"]
    resources = [var.db_password_secret_arn]
  }

  statement {
    sid    = "AllowS3PutObject"
    effect = "Allow"
    actions = ["s3:PutObject"]
    resources = ["${var.reports_bucket_arn}/*"]
  }

  statement {
    sid    = "AllowAsyncMessaging"
    effect = "Allow"
    actions = [
      "sqs:SendMessage",
      "sns:Publish"
    ]
    resources = [
      var.report_jobs_queue_arn,
      var.report_notifications_topic_arn
    ]
  }
}

resource "aws_iam_policy" "app_lambda_permissions" {
  name        = "${var.project_name}-${var.environment}-app-permissions"
  description = "Permissions for Lambda to access secrets, S3 and queues."
  policy      = data.aws_iam_policy_document.app_lambda_permissions.json
}

module "role" {
  source = "../iam_role"

  role_name = "${var.project_name}-${var.environment}-main-lambda-role"

  assume_role_policy_json = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action    = "sts:AssumeRole",
      Effect    = "Allow",
      Principal = { Service = "lambda.amazonaws.com" }
    }]
  })

  policy_arns_to_attach = {
    BasicExecution = data.aws_iam_policy.lambda_basic_execution.arn
    VPC_Access     = data.aws_iam_policy.lambda_vpc_access.arn
    AppPermissions = aws_iam_policy.app_lambda_permissions.arn
  }

  tags = var.tags
}
