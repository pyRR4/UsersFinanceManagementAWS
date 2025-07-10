variable "project_name" {
  description = "Nazwa projektu, uzywana do tagowania zasobow."
  type        = string
}

variable "environment" {
  description = "Nazwa srodowiska (np. dev)."
  type        = string
}

variable "tags" {
  description = "Mapa wspolnych tagow."
  type        = map(string)
  default     = {}
}

variable "vpc_id" {
  description = "ID sieci VPC, w której ma zostać umieszczona baza danych."
  type        = string
}

variable "public_subnet_ids" {
  description = "Lista ID prywatnych podsieci dla bazy danych."
  type        = list(string)
}

variable "db_instance_class" {
  description = "Klasa instancji RDS (rozmiar i moc)."
  type        = string
  default     = "db.t3.micro"
}

variable "db_allocated_storage" {
  description = "Rozmiar dysku dla bazy danych w GB."
  type        = number
  default     = 20
}

variable "db_name" {
  description = "Nazwa glownej bazy danych do utworzenia."
  type        = string
  default     = "aplikacja_finansowa"
}

variable "db_username" {
  description = "Nazwa glownego uzytkownika bazy danych."
  type        = string
}

variable "db_password" {
  description = "Haslo dla głownego uzytkownika bazy danych."
  type        = string
  sensitive   = true
}