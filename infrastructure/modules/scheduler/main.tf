resource "aws_scheduler_schedule" "this" {
  name       = var.schedule_name
  group_name = "default"

  flexible_time_window { mode = "OFF" }

  schedule_expression          = var.schedule_expression
  schedule_expression_timezone = "Europe/Warsaw"

  target {
    arn      = var.target_arn
    role_arn = var.iam_role_arn
  }
}