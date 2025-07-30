variable "rest_api_id" {
  description = "ID głównego zasobu API Gateway, do którego należy ten endpoint."
  type        = string
}

variable "parent_id" {
  description = "ID zasobu nadrzędnego (np. roota API lub innej ścieżki)."
  type        = string
}

variable "path_part" {
  description = "Fragment ścieżki URL dla tego zasobu (np. 'transactions' lub '{id}')."
  type        = string
}

variable "authorizer_id" {
  description = "ID autoryzatora Cognito do zabezpieczenia metod."
  type        = string
}

variable "authorization_type" {
  description = "Typ autoryzacji dla metody (np. 'NONE', 'COGNITO_USER_POOLS')."
  type        = string
  default     = "COGNITO_USER_POOLS"
}

variable "lambda_integrations" {
  description = "Mapa metod HTTP do integracji z funkcjami Lambda. Klucz to metoda (np. 'POST'), wartość to obiekt z ARN i nazwą Lambdy."
  type        = map(object({
    invoke_arn = string
    name       = string
  }))
  default     = {}
}

variable "cors_allowed_origin" {
  description = "Domena, której zezwalamy na dostęp w ramach CORS."
  type        = string
  default     = "*"
}

variable "rest_api_execution_arn" {
  description = "Execution ARN głównego zasobu API Gateway."
  type        = string
}