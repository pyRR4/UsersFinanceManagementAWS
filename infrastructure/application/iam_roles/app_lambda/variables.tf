variable "project_name" {
  type = string
}

variable "environment" {
  type = string
}

variable "tags" {
  type = map(string)
}

variable "db_password_secret_arn" {
  type = string
}

variable "reports_bucket_arn" {
  type = string
}

variable "report_jobs_queue_arn" {
  type = string
}

variable "report_notifications_topic_arn" {
  type = string 
}

