output "security_group_id" {
  description = "ID of the Lambda security group"
  value       = aws_security_group.lambda_sg.id
}

output "security_group_arn" {
  description = "ARN of the Lambda security group"
  value       = aws_security_group.lambda_sg.arn
}