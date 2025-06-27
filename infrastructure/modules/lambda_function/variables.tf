variable "function_name" {
  description = "Nazwa funkcji Lambda."
  type        = string
}

variable "description" {
  description = "Opis funkcji Lambda."
  type        = string
  default     = null
}

variable "handler" {
  description = "Pełna ścieżka do handlera w kodzie Javy (pakiet.Klasa::metoda)."
  type        = string
}

variable "runtime" {
  description = "Środowisko uruchomieniowe Javy."
  type        = string
  default     = "java17"
}

variable "memory_size" {
  description = "Ilość pamięci RAM dla funkcji w MB."
  type        = number
  default     = 512
}

variable "timeout" {
  description = "Maksymalny czas wykonania funkcji w sekundach."
  type        = number
  default     = 30
}

variable "iam_role_arn" {
  description = "ARN roli IAM, którą funkcja ma przyjąć."
  type        = string
}

variable "jar_path" {
  description = "Ścieżka do pliku JAR z kodem aplikacji."
  type        = string
}

variable "vpc_config" {
  description = "Opcjonalna konfiguracja VPC dla funkcji."
  type = object({
    subnet_ids         = list(string)
    security_group_ids = list(string)
  })
  default = null
}

variable "environment_variables" {
  description = "Mapa zmiennych środowiskowych dla funkcji."
  type        = map(string)
  default     = {}
}

variable "tags" {
  description = "Mapa tagów."
  type        = map(string)
  default     = {}
}