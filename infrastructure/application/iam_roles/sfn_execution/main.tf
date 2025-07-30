data "aws_iam_policy_document" "sfn_lambda_invoke" {
  statement {
    effect = "Allow"
    actions = ["lambda:InvokeFunction"]
    resources = var.forecasting_lambda_arns
  }
}

resource "aws_iam_policy" "sfn_lambda_invoke" {
  name   = "${var.project_name}-${var.environment}-sfn-lambda-invoke-policy"
  policy = data.aws_iam_policy_document.sfn_lambda_invoke.json
}

module "role" {
  source = "../iam_role"

  role_name = "${var.project_name}-${var.environment}-sfn-execution-role"

  assume_role_policy_json = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Action    = "sts:AssumeRole",
      Effect    = "Allow",
      Principal = { Service = "states.amazonaws.com" }
    }]
  })

  policy_arns_to_attach = {
    SfnInvoke = aws_iam_policy.sfn_lambda_invoke.arn
  }

  tags = var.tags
}
