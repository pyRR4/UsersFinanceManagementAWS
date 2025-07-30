variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "tags" {
  type = map(string)
}

variable "forecasting_lambda_arns" {
  type = list(string)
}