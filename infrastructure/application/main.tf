module "auth" {
  source              = "./auth"
  project_name        = var.project_name
  environment         = var.environment
  tags                = var.tags
  test_user_email     = var.test_user_email
  test_user_password  = var.test_user_password
}

module "storage" {
  source       = "./storage"
  project_name = var.project_name
  environment  = var.environment
  tags         = var.tags
}

module "messaging" {
  source       = "./messaging"
  project_name = var.project_name
  environment  = var.environment
  tags         = var.tags
}

module "app_lambda_role" {
  source = "./iam_roles/app_lambda"

  project_name                    = var.project_name
  environment                     = var.environment
  tags                            = var.tags
  db_password_secret_arn          = var.db_password_secret_arn
  reports_bucket_arn              = module.storage.reports_bucket_arn
  report_jobs_queue_arn           = module.messaging.sqs_queue_arn
  report_notifications_topic_arn  = module.messaging.sns_topic_arn
}

module "lambdas" {
  source                          = "./lambdas"
  project_name                    = var.project_name
  environment                     = var.environment
  tags                            = var.tags

  app_jar_path                    = var.app_jar_path

  db_cluster_arn                  = var.db_cluster_arn
  db_secret_arn                   = var.db_secret_arn
  db_host                         = var.db_host
  db_port                         = var.db_port
  db_name                         = var.db_name
  db_username                     = var.db_username

  aws_region                      = var.aws_region
  cognito_userpool_id             = module.auth.user_pool_id

  app_lambda_role_arn             = module.app_lambda_role.role_arn

  vpc_private_subnet_ids          = var.lambda_subnet_ids
  lambda_security_group_id        = var.lambda_sg_id

  report_jobs_queue_url           = module.messaging.sqs_queue_url
  reports_bucket_name             = module.storage.reports_bucket_name
  report_notifications_topic_arn  = module.messaging.sns_topic_arn
}

module "sfn_execution_role" {
  source = "./iam_roles/sfn_execution"

  project_name            = var.project_name
  environment             = var.environment
  tags                    = var.tags
  forecasting_lambda_arns = [
    for key, details in module.lambdas.lambda_details :
    details.arn
    if contains(keys(details.tags), "Feature") && details.tags["Feature"] == "Forecasting"
  ]
}

module "scheduler_execution_role" {
  source = "./iam_roles/scheduler_execution"

  project_name             = var.project_name
  environment              = var.environment
  tags                     = var.tags
  lambda_arn               = module.lambdas.lambda_invoke_arns["start-report-generation"]
  state_machine_arn        = module.step_functions.forecasting_state_machine_arn
}

module "step_functions" {
  source                        = "./step_functions"
  project_name                  = var.project_name
  environment                   = var.environment
  tags                          = var.tags
  sfn_execution_role_arn        = module.sfn_execution_role.role_arn
  scheduler_execution_role_arn  = module.scheduler_execution_role.role_arn
  lambda_arns                   = module.lambdas.lambda_invoke_arns
}

module "api_gateway_logging_role" {
  source       = "./iam_roles/api_gateway_logging"
}

module "api_gateway" {
  source              = "./api_gateway"
  project_name        = var.project_name
  environment         = var.environment
  tags                = var.tags
  aws_region          = var.aws_region
  cloudwatch_role_arn = module.api_gateway_logging_role.role_arn
  user_pool_arn       = module.auth.user_pool_arn
  lambda_integrations = {
    for key in keys(module.lambdas.lambda_invoke_arns) : key => {
      invoke_arn = module.lambdas.lambda_invoke_arns[key]
      name       = module.lambdas.lambda_function_names[key]
    }
  }
}

module "cloudfront" {
  source                  = "./cloudfront"
  project_name            = var.project_name
  environment             = var.environment
  tags                    = var.tags
  api_gateway_invoke_url  = module.api_gateway.api_url
  api_gateway_name        = module.api_gateway.api_name
}

module "bastion" {
  source            = "./bastion"
  project_name      = var.project_name
  environment       = var.environment
  tags              = var.tags
  vpc_id            = module.vpc.vpc_id
  public_subnet_id  = module.vpc.public_subnet_ids[0]
  key_name          = var.bastion_key_name
}
