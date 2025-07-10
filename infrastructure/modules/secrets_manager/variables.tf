variable "secret_name" {
  description = "Nazwa sekretu."
  type        = string
}

variable "secret_value" {
  description = "Wrażliwa wartość do przechowania w sekrecie."
  type        = string
  sensitive   = true
}

variable "tags" {
  type    = map(string)
  default = {}
}