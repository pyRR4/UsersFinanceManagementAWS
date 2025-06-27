variable "bucket_prefix" {
  description = "Prefiks dla nazwy bucketu. Zostanie do niego dodany losowy sufiks."
  type        = string
}

variable "tags" {
  description = "Mapa tagów do przypisania do zasobu."
  type        = map(string)
  default     = {}
}