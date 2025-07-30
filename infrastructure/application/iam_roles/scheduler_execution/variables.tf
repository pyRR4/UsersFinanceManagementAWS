variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "tags" {
  type = map(string)
}

variable "lambda_arn" {
  type = list(string)
}

variable "state_machine_arn" {
  type = string
}