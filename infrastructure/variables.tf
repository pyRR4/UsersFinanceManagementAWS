variable "aws_region" {
  description = "Region AWS, w którym wdrażane są wszystkie zasoby."
  type        = string
  default     = "eu-central-1"
}

variable "project_name" {
  description = "Nazwa projektu, używana do tworzenia nazw i tagów dla zasobów."
  type        = string
}

variable "environment" {
  description = "Nazwa środowiska (np. dev, stage, prod)."
  type        = string
  default     = "dev"
}

variable "db_username" {
  description = "Nazwa głównego użytkownika bazy danych."
  type        = string
  default     = "postgres"
}

variable "db_password" {
  description = "Hasło dla głównego użytkownika bazy danych."
  type        = string
  sensitive   = true # Ważne: Terraform nie będzie wyświetlał tej wartości w logach
}

variable "app_jar_path" {
  description = "Ścieżka do pliku JAR z aplikacją Javy."
  type        = string
  default     = "../application/target/AplikacjaFinansowaBackend-1.0.0.jar"
}