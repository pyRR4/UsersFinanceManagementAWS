variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "tags" {
  type = map(string)
}

variable "test_user_email" {
  type = string
}

variable "test_user_password" {
  type      = string
  sensitive = true
}