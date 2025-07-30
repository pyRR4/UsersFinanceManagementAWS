variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "tags" {
  type = map(string)
}

variable "app_jar_path" {
  type        = string
  description = "Ścieżka do pliku JAR z aplikacją"
}

variable "db_cluster_arn" {
  type = string
}

variable "db_secret_arn" {
  type = string
}

variable "db_host" {
  type = string
}

variable "db_port" {
  type = number
}

variable "db_name" {
  type = string
}

variable "db_username" {
  type = string
}

variable "aws_region" {
  type = string
}

variable "cognito_userpool_id" {
  type = string
}

variable "app_lambda_role_arn" {
  description = "ARN roli IAM używanej przez funkcje Lambda"
  type        = string
}

variable "vpc_private_subnet_ids" {
  description = "Lista prywatnych subnetów dla funkcji wymagających VPC"
  type        = list(string)
}

variable "lambda_security_group_id" {
  description = "ID security group dla funkcji Lambda"
  type        = string
}

variable "report_jobs_queue_url" {
  description = "URL kolejki SQS dla generowania raportów"
  type        = string
}

variable "reports_bucket_name" {
  description = "Nazwa bucketa S3 na wygenerowane raporty"
  type        = string
}

variable "report_notifications_topic_arn" {
  description = "ARN tematu SNS do powiadamiania o raportach"
  type        = string
}