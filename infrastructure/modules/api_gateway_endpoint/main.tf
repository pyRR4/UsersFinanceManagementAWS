# 1. Tworzymy zasób (ścieżkę) na podstawie przekazanych parametrów
resource "aws_api_gateway_resource" "this" {
  rest_api_id = var.rest_api_id
  parent_id   = var.parent_id
  path_part   = var.path_part
}

# 2. Dynamicznie tworzymy metody, integracje i uprawnienia dla każdej pozycji w mapie 'lambda_integrations'
resource "aws_api_gateway_method" "lambda_methods" {
  for_each = var.lambda_integrations

  rest_api_id   = var.rest_api_id
  resource_id   = aws_api_gateway_resource.this.id
  http_method   = each.key # np. "POST", "GET"
  authorization = "COGNITO_USER_POOLS"
  authorizer_id = var.authorizer_id
}

resource "aws_api_gateway_integration" "lambda_integrations" {
  for_each = var.lambda_integrations

  rest_api_id             = var.rest_api_id
  resource_id             = aws_api_gateway_resource.this.id
  http_method             = aws_api_gateway_method.lambda_methods[each.key].http_method
  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = each.value.invoke_arn
}

resource "aws_lambda_permission" "allow_api_gateway" {
  for_each = var.lambda_integrations

  statement_id  = "AllowAPIGatewayInvoke_${replace(var.path_part, "/[{}]/", "")}_${each.key}"
  action        = "lambda:InvokeFunction"
  function_name = each.value.name
  principal     = "apigateway.amazonaws.com"
  source_arn = "${var.rest_api_execution_arn}/*/${aws_api_gateway_method.lambda_methods[each.key].http_method}${aws_api_gateway_resource.this.path}"
}

# 3. Automatyczna obsługa CORS (Metoda OPTIONS)
resource "aws_api_gateway_method" "options_method" {
  rest_api_id   = var.rest_api_id
  resource_id   = aws_api_gateway_resource.this.id
  http_method   = "OPTIONS"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "options_integration" {
  rest_api_id = var.rest_api_id
  resource_id = aws_api_gateway_resource.this.id
  http_method = aws_api_gateway_method.options_method.http_method
  type        = "MOCK"

  request_templates = {
    "application/json" = "{\"statusCode\": 200}"
  }
}

resource "aws_api_gateway_method_response" "options_200" {
  rest_api_id = var.rest_api_id
  resource_id = aws_api_gateway_resource.this.id
  http_method = aws_api_gateway_method.options_method.http_method
  status_code = "200"

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = true,
    "method.response.header.Access-Control-Allow-Methods" = true,
    "method.response.header.Access-Control-Allow-Origin"  = true
  }
}

resource "aws_api_gateway_integration_response" "options_integration_response" {
  rest_api_id = var.rest_api_id
  resource_id = aws_api_gateway_resource.this.id
  http_method = aws_api_gateway_method.options_method.http_method
  status_code = aws_api_gateway_method_response.options_200.status_code

  response_parameters = {
    "method.response.header.Access-Control-Allow-Headers" = "'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token'",
    "method.response.header.Access-Control-Allow-Methods" = "'${join(",", keys(var.lambda_integrations))},OPTIONS'",
    "method.response.header.Access-Control-Allow-Origin"  = "'${var.cors_allowed_origin}'"
  }
  depends_on = [
    aws_api_gateway_integration.options_integration
  ]
}