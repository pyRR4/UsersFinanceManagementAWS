resource "aws_security_group" "bastion_sg" {
  name   = "${var.project_name}-${var.environment}-bastion-sg"
  vpc_id = module.vpc.vpc_id

  ingress {
    protocol    = "tcp"
    from_port   = 22
    to_port     = 22
    cidr_blocks = ["${chomp(data.http.my_ip.response_body)}/32"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = merge(var.tags, { Name = "${var.project_name}-bastion-sg" })
}

data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }
}

resource "aws_instance" "bastion" {
  ami           = data.aws_ami.amazon_linux.id
  instance_type = "t2.micro"

  subnet_id = module.vpc.public_subnet_ids[0]

  vpc_security_group_ids = [aws_security_group.bastion_sg.id]
  key_name               = "bastion-key"

  tags = merge(var.tags, { Name = "${var.project_name}-bastion-host" })
}

data "http" "my_ip" {
  url = "http://ipv4.icanhazip.com"
}