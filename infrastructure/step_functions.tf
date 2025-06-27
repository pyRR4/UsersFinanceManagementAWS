resource "aws_sfn_state_machine" "forecasting_state_machine" {
  name     = "${var.project_name}-${var.environment}-forecasting-sm"
  role_arn = data.aws_iam_role.lab_execution_role.arn

  definition = templatefile("${path.module}/state_machine_definition.asl.json", {
    GetAllUsersLambdaArn           = module.lambda_functions["get-all-users"].function_arn
    FetchUserTransactionsLambdaArn = module.lambda_functions["fetch-user-transactions"].function_arn
    CalculateForecastLambdaArn     = module.lambda_functions["calculate-forecast"].function_arn
    SaveForecastLambdaArn          = module.lambda_functions["save-forecast"].function_arn
  })

  tags = var.tags
}