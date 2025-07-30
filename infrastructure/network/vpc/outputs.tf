output "vpc_id" {
  description = "ID stworzonego VPC."
  value       = aws_vpc.this.id
}

output "public_subnet_ids" {
  description = "Lista ID stworzonych podsieci publicznych."
  value       = [for s in aws_subnet.public : s.id]
}

output "private_subnet_ids" {
  description = "Lista ID stworzonych podsieci prywatnych."
  value       = [for s in aws_subnet.private : s.id]
}