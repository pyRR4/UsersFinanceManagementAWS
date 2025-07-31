module "vpc" {
  source       = "./vpc"
  project_name = var.project_name
  aws_region   = var.aws_region
  tags         = var.tags
}

module "lambda_sg" {
  source       = "./security_groups/lambda_sg"
  project_name = var.project_name
  environment  = var.environment
  tags         = var.tags
  vpc_id       = module.vpc.vpc_id
}