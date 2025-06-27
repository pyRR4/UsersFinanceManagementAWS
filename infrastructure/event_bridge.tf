resource "aws_scheduler_schedule" "run_reporting_job" {
  name       = "${var.project_name}-${var.environment}-monthly-reporting-job"
  group_name = "default"

  flexible_time_window {
    mode = "OFF"
  }

  # Uruchom o 2:00 w nocy, pierwszego dnia każdego miesiąca.
  # Składnia to cron(minuty godziny dzień-miesiąca miesiąc dzień-tygodnia rok)
  schedule_expression          = "cron(0 2 1 * ? *)"
  schedule_expression_timezone = "Europe/Warsaw"

  target {
    arn      = module.lambda_functions["start-report-generation"].function_arn
    role_arn = data.aws_iam_role.lab_execution_role.arn
  }
}

resource "aws_scheduler_schedule" "run_forecasting_job" {
  name       = "${var.project_name}-${var.environment}-weekly-forecasting-job"
  group_name = "default"

  flexible_time_window {
    mode = "OFF"
  }

  # Uruchom o 4:00 rano w każdą niedzielę.
  schedule_expression          = "cron(0 4 ? * SUN *)"
  schedule_expression_timezone = "Europe/Warsaw"

  target {
    arn      = aws_sfn_state_machine.forecasting_state_machine.id
    role_arn = data.aws_iam_role.lab_execution_role.arn
  }
}