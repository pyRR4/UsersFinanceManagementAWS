variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "tags" {
  type = map(string)
}

variable "api_gateway_invoke_url" {
  type = string
  description = "Invoke URL of the API Gateway stage"
}

variable "api_gateway_name" {
  type = string
  description = "Logical name of the API Gateway (used as Origin ID)"
}
