variable "project_name" {
  description = "Nazwa projektu, używana do tagowania zasobów sieciowych."
  type        = string
}

variable "aws_region" {
  description = "Region AWS, w którym tworzona jest sieć."
  type        = string
}

variable "vpc_cidr_block" {
  description = "Główny blok adresów IP dla całego VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "public_subnet_cidrs" {
  description = "Lista bloków CIDR dla podsieci publicznych."
  type        = list(string)
  default     = ["10.0.1.0/24", "10.0.2.0/24"]
}

variable "private_subnet_cidrs" {
  description = "Lista bloków CIDR dla podsieci prywatnych."
  type        = list(string)
  default     = ["10.0.101.0/24", "10.0.102.0/24"]
}

variable "tags" {
  description = "Mapa wspólnych tagów."
  type        = map(string)
  default     = {}
}