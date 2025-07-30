variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "tags" {
  type = map(string)
}

variable "schedule_expression_reporting" {
  type    = string
  default = "cron(0 2 1 * ? *)" # 1. dnia miesiąca o 02:00
}

variable "schedule_expression_forecasting" {
  type    = string
  default = "cron(0 4 ? * SUN *)" # w każdą niedzielę o 04:00
}

variable "scheduler_execution_role_arn" {
  type = string
}

variable "sfn_execution_role_arn" {
  type = string
}

variable "lambda_arns" {
  type = map(string)
  description = "Mapa z nazwami funkcji Lambda i ich ARN-ami. Wymagane: get-all-users, fetch-user-transactions, calculate-forecast, save-forecast, start-report-generation"
}
