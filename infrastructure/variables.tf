variable "aws_region" {
  description = "Region AWS, w którym wdrażane są wszystkie zasoby."
  type        = string
  default     = "us-east-1"
}

variable "project_name" {
  description = "Krótka, unikalna nazwa projektu (bez spacji, małe litery), używana do tworzenia nazw zasobów."
  type        = string
  default     = "finanse-app"
}

variable "environment" {
  description = "Nazwa środowiska (np. dev, stage, prod), używana do tworzenia nazw i tagów."
  type        = string
  default     = "dev"
}

variable "db_name" {
  description = "Nazwa głównej bazy danych do utworzenia wewnątrz instancji RDS."
  type        = string
  default     = "aplikacja_finansowa"
}

variable "db_username" {
  description = "Nazwa głównego użytkownika bazy danych."
  type        = string
  default     = "postgres"
}

variable "db_password" {
  description = "Hasło dla głównego użytkownika bazy danych. Powinno być podane w pliku .tfvars."
  type        = string
  sensitive   = true
}

variable "test_user_password" {
  description = "Hasło dla testowego użytkownika aplikacji. Powinno być podane w pliku .tfvars."
  type        = string
  sensitive   = true
}

variable "test_user_email" {
  description = "Email dla testowego użytkownika aplikacji. Powinno być podane w pliku .tfvars."
  type        = string
}

variable "app_jar_path" {
  description = "Względna ścieżka do pliku JAR z aplikacją Javy."
  type        = string
  default     = "../application/target/UsersFinanceManagementCloud-1.0.0.jar"
}

variable "tags" {
  description = "Mapa wspólnych tagów do przypisania do wszystkich zasobów."
  type        = map(string)
  default = {
    ManagedBy = "Terraform"
    Project   = "AplikacjaFinansowa"
  }
}