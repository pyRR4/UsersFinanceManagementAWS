output "state_machine_arn" {
  description = "ARN stworzonej maszyny stanów."
  value       = aws_sfn_state_machine.this.id
}