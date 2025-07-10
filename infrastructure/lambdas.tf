module "lambda_functions" {
  source = "./modules/lambda_function"

  for_each = local.lambda_definitions

  function_name = "${var.project_name}-${var.environment}-${each.key}"
  handler       = each.value.handler
  iam_role_arn  = module.app_lambda_role.role_arn
  jar_path      = var.app_jar_path
  tags          = merge(var.tags, try(each.value.tags, {}))

  vpc_config = each.value.needs_vpc ? {
    subnet_ids         = module.vpc.private_subnet_ids
    security_group_ids = [aws_security_group.lambda_sg.id]
  } : null

  environment_variables = merge(
    local.common_environment_variables,
    try(each.value.env_vars, {})
  )

  depends_on = [
    module.app_lambda_role,
    module.vpc
  ]
}


resource "aws_lambda_permission" "allow_api_gateway_all" {
  for_each = module.lambda_functions

  statement_id  = "AllowApiGatewayInvoke-${each.key}"
  action        = "lambda:InvokeFunction"
  function_name = each.value.function_name
  principal     = "apigateway.amazonaws.com"

  source_arn    = "${aws_api_gateway_rest_api.main.execution_arn}/*/*"

  depends_on = [
    module.lambda_functions
  ]
}
