variable "secret_name" {
  description = "Nazwa sekretu, która zostanie utworzona w AWS Secrets Manager."
  type        = string
}

variable "secret_description" {
  description = "Opis dla tworzonego sekretu."
  type        = string
  default     = "Managed by Terraform"
}

variable "secret_payload" {
  description = "Mapa (klucz-wartość) zawierająca dane do umieszczenia w sekrecie. Zostanie przekonwertowana na JSON."
  type        = map(string)
  sensitive   = true # Ważne: zawartość nie będzie pokazywana w logach
}

variable "tags" {
  description = "Mapa tagów do przypisania do zasobu."
  type        = map(string)
  default     = {}
}