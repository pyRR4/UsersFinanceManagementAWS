data "aws_iam_policy_document" "scheduler_targets_invoke" {
  statement {
    effect = "Allow"
    actions = [
      "lambda:InvokeFunction",
      "states:StartExecution"
    ]
    resources = [
      var.lambda_arn,
      var.state_machine_arn
    ]
  }
}

resource "aws_iam_policy" "scheduler_targets_invoke" {
  name   = "${var.project_name}-${var.environment}-scheduler-targets-invoke-policy"
  policy = data.aws_iam_policy_document.scheduler_targets_invoke.json
}

module "scheduler_execution_role" {
  source = "../iam_role"

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
    SfnInvoke = aws_iam_policy.scheduler_targets_invoke.arn
  }
}