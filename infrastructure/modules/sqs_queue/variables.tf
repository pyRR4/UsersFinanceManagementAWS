variable "queue_name" {
  description = "Podstawowa nazwa dla kolejki SQS."
  type        = string
}

variable "is_fifo" {
  description = "Jeśli ustawione na true, stworzy kolejkę FIFO z sufiksem .fifo."
  type        = bool
  default     = false
}

variable "tags" {
  description = "Mapa tagów do przypisania do zasobu."
  type        = map(string)
  default     = {}
}