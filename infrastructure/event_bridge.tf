module "reporting_schedule" {
  source = "./modules/scheduler"

  schedule_name       = "${var.project_name}-${var.environment}-monthly-reporting"
  schedule_expression = "cron(0 2 1 * ? *)"
  target_arn          = module.lambda_functions["start-report-generation"].function_arn
  iam_role_arn        = module.scheduler_execution_role.role_arn
}

module "forecasting_schedule" {
  source = "./modules/scheduler"

  schedule_name       = "${var.project_name}-${var.environment}-weekly-forecasting"
  schedule_expression = "cron(0 4 ? * SUN *)"
  target_arn          = module.forecasting_sfn.state_machine_arn
  iam_role_arn        = module.scheduler_execution_role.role_arn
}