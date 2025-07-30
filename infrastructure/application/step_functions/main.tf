module "reporting_schedule" {
  source = "./scheduler"

  schedule_name       = "${var.project_name}-${var.environment}-monthly-reporting"
  schedule_expression = var.schedule_expression_reporting
  target_arn          = var.lambda_arns["start-report-generation"]
  iam_role_arn        = var.scheduler_execution_role_arn
}

module "forecasting_schedule" {
  source = "./scheduler"

  schedule_name       = "${var.project_name}-${var.environment}-weekly-forecasting"
  schedule_expression = var.schedule_expression_forecasting
  target_arn          = module.forecasting_sfn.state_machine_arn
  iam_role_arn        = var.scheduler_execution_role_arn
}

module "forecasting_sfn" {
  source = "./step_function"

  state_machine_name = "${var.project_name}-${var.environment}-forecasting-sm"
  iam_role_arn       = var.sfn_execution_role_arn

  definition_template_path = "${path.module}/state_machine_definition.asl.json"
  definition_template_vars = {
    GetAllUsersLambdaArn           = var.lambda_arns["get-all-users"]
    FetchUserTransactionsLambdaArn = var.lambda_arns["fetch-user-transactions"]
    CalculateForecastLambdaArn     = var.lambda_arns["calculate-forecast"]
    SaveForecastLambdaArn          = var.lambda_arns["save-forecast"]
  }

  tags = var.tags
}
