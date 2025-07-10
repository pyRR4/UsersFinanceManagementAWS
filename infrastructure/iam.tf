resource "aws_iam_policy" "app_lambda_permissions" {
  name        = "${var.project_name}-${var.environment}-app-permissions"
  description = "Niestandardowe uprawnienia dla Lambd aplikacji finansowej."

  policy = data.aws_iam_policy_document.app_lambda_permissions.json
}

data "aws_iam_policy_document" "app_lambda_permissions" {
  statement {
    sid       = "AllowReadingDBSecret"
    effect    = "Allow"
    actions   = ["secretsmanager:GetSecretValue"]
    resources = [module.db_password_secret.secret_arn]
  }

  statement {
    sid    = "AllowS3PutObject"
    effect = "Allow"
    actions = ["s3:PutObject"]
    resources = ["${module.reports_bucket.bucket_arn}/*"]
  }

  statement {
    sid    = "AllowAsyncMessaging"
    effect = "Allow"
    actions = [
      "sqs:SendMessage",
      "sns:Publish"
    ]
    resources = [
      module.report_jobs_queue.queue_arn,
      module.report_notifications_topic.topic_arn
    ]
  }
}

data "aws_iam_policy" "lambda_basic_execution" {
  arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

data "aws_iam_policy" "lambda_vpc_access" {
  arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}

data "aws_caller_identity" "current" {}


module "app_lambda_role" {
  source = "./modules/iam_role"

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

resource "aws_iam_policy" "sfn_lambda_invoke_policy" {
  name   = "${var.project_name}-${var.environment}-sfn-lambda-invoke-policy"
  policy = data.aws_iam_policy_document.sfn_lambda_invoke.json
}

data "aws_iam_policy_document" "sfn_lambda_invoke" {
  statement {
    effect = "Allow"
    actions   = ["lambda:InvokeFunction"]
    resources = [ for lambda in module.lambda_functions : lambda.function_arn if contains(keys(lambda.tags), "Feature") && lambda.tags.Feature == "Forecasting" ]
  }
}

module "sfn_execution_role" {
  source = "./modules/iam_role"

  role_name = "${var.project_name}-${var.environment}-sfn-execution-role"
  tags      = var.tags

  assume_role_policy_json = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action    = "sts:AssumeRole",
      Effect    = "Allow",
      Principal = { Service = "states.amazonaws.com" }
    }]
  })

  policy_arns_to_attach = {
    SfnInvoke = aws_iam_policy.sfn_lambda_invoke_policy.arn
  }
}

resource "aws_iam_policy" "scheduler_targets_invoke_policy" {
  name   = "${var.project_name}-${var.environment}-scheduler-targets-invoke-policy"
  policy = data.aws_iam_policy_document.scheduler_targets_invoke.json
}

data "aws_iam_policy_document" "scheduler_targets_invoke" {
  statement {
    effect = "Allow"
    actions = [
      "lambda:InvokeFunction",
      "states:StartExecution"
    ]
    resources = [
      module.lambda_functions["start-report-generation"].function_arn,
      module.forecasting_sfn.state_machine_arn
    ]
  }
}

module "scheduler_execution_role" {
  source = "./modules/iam_role"

  role_name = "${var.project_name}-${var.environment}-scheduler-execution-role"
  tags      = var.tags

  assume_role_policy_json = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action    = "sts:AssumeRole",
      Effect    = "Allow",
      Principal = { Service = "scheduler.amazonaws.com" }
    }]
  })

  policy_arns_to_attach = {
    SfnInvoke = aws_iam_policy.sfn_lambda_invoke_policy.arn
  }
}