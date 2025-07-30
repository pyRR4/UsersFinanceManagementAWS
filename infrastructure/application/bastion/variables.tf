variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "public_subnet_id" {
  type = string
}

variable "tags" {
  type = map(string)
}

variable "key_name" {
  type        = string
  description = "SSH key pair name to connect to the bastion host"
}
