variable "topic_name" {
  description = "Nazwa, która zostanie przypisana do tematu SNS."
  type        = string
}

variable "tags" {
  description = "Mapa tagów do przypisania do zasobu."
  type        = map(string)
  default     = {}
}