data "aws_iam_policy_document" "api_gateway_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["apigateway.amazonaws.com"]
    }
  }
}


module "api_gateway_logging_role" {
  source                  = "../iam_role"

  role_name               = "api-gateway-cloudwatch-logging-role"
  assume_role_policy_json = data.aws_iam_policy_document.api_gateway_assume_role.json

  policy_arns_to_attach = {
    apigw_logging = "arn:aws:iam::aws:policy/service-role/AmazonAPIGatewayPushToCloudWatchLogs"
  }

  tags = {
    ManagedBy = "Terraform"
    Purpose   = "APIGateway Logging"
  }
}