variable "role_name" {
  type        = string
}

variable "assume_role_policy_json" {
  type        = string
}

variable "policy_arns_to_attach" {
  type        = map(string)
  default     = {}
}

variable "tags" {
  type        = map(string)
  default     = {}
}