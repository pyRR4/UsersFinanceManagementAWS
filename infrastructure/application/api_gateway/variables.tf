variable "project_name" {
  type        = string
  description = "Prefix for resource names"
}

variable "environment" {
  type        = string
  description = "Deployment environment name (e.g. dev, prod)"
}

variable "aws_region" {
  type = string
}

variable "tags" {
  type    = map(string)
  default = {}
}

variable "cloudwatch_role_arn" {
  type        = string
  description = "IAM Role ARN for API Gateway logging"
}

variable "user_pool_arn" {
  type        = string
  description = "Cognito User Pool ARN used for authorization"
}

variable "lambda_integrations" {
  description = "Mapa metod HTTP do integracji z funkcjami Lambda"
  type = map(object({
    invoke_arn = string
    name       = string
  }))
}
