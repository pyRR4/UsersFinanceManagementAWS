module "forecasting_sfn" {
  source = "./modules/step_function"

  state_machine_name = "${var.project_name}-${var.environment}-forecasting-sm"
  iam_role_arn       = module.sfn_execution_role.role_arn

  definition_template_path = "${path.module}/state_machine_definition.asl.json"
  definition_template_vars = {
    GetAllUsersLambdaArn           = module.lambda_functions["get-all-users"].function_arn
    FetchUserTransactionsLambdaArn = module.lambda_functions["fetch-user-transactions"].function_arn
    CalculateForecastLambdaArn     = module.lambda_functions["calculate-forecast"].function_arn
    SaveForecastLambdaArn          = module.lambda_functions["save-forecast"].function_arn
  }

  tags = var.tags
}