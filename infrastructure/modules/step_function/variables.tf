variable "state_machine_name" {
  type    = string
}

variable "iam_role_arn" {
  type    = string
}

variable "definition_template_path" {
  type    = string
}

variable "definition_template_vars" {
  type    = map(string)
  default = {}
}

variable "tags" {
  type    = map(string)
  default = {}
}