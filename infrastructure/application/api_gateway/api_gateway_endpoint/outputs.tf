output "resource_id" {
  description = "ID nowo utworzonego zasobu API Gateway."
  value       = aws_api_gateway_resource.this.id
}

output "resource_path" {
  description = "Ścieżka nowo utworzonego zasobu."
  value       = aws_api_gateway_resource.this.path
}