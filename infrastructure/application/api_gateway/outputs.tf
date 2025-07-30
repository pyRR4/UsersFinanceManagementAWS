output "api_gateway_id" {
  description = "ID głównego REST API"
  value       = aws_api_gateway_rest_api.main.id
}

output "api_gateway_root_resource_id" {
  description = "Root resource ID API Gateway"
  value       = aws_api_gateway_rest_api.main.root_resource_id
}

output "api_url" {
  description = "Pełny URL API"
  value       = "https://${aws_api_gateway_rest_api.main.id}.execute-api.${var.aws_region}.amazonaws.com/${var.environment}"
}

output "api_name" {
  value = aws_api_gateway_rest_api.main.name
}