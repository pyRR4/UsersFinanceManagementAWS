resource "aws_security_group" "this" {
  name        = "${var.project_name}-${var.environment}-rds-sg"
  description = "Allows for incoming"
  vpc_id      = var.vpc_id

  ingress {
    from_port   = 5432
    to_port     = 5432
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    protocol    = "-1"
    from_port   = 0
    to_port     = 0
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = var.tags
}

resource "aws_db_subnet_group" "this" {
  name       = "${var.project_name}-${var.environment}-db-subnet-group"
  subnet_ids = var.public_subnet_ids

  tags = var.tags
}

resource "aws_db_instance" "this" {
  identifier           = "${var.project_name}-${var.environment}-db"
  engine               = "postgres"
  engine_version       = "17.4"
  instance_class       = var.db_instance_class
  allocated_storage    = var.db_allocated_storage

  db_name              = var.db_name
  username             = var.db_username
  password             = var.db_password

  db_subnet_group_name = aws_db_subnet_group.this.name
  vpc_security_group_ids = [aws_security_group.this.id]

  publicly_accessible = true
  skip_final_snapshot = true
  deletion_protection = false

  tags = var.tags
}