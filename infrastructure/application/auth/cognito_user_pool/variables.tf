variable "pool_name" {
  description = "Nazwa puli użytkowników Cognito, która zostanie utworzona."
  type        = string
}

variable "app_client_name" {
  description = "Nazwa klienta aplikacji dla puli użytkowników."
  type        = string
}

variable "tags" {
  description = "Mapa tagów do przypisania do zasobów."
  type        = map(string)
  default     = {}
}