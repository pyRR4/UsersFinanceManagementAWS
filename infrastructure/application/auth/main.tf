module "user_pool" {
  source = "./cognito_user_pool"

  pool_name       = "${var.project_name}-${var.environment}-user-pool"
  app_client_name = "${var.project_name}-${var.environment}-app-client"
  tags            = var.tags
}

resource "aws_cognito_user" "test_user" {
  user_pool_id = module.user_pool.pool_id

  username = var.test_user_email
  password = var.test_user_password

  attributes = {
    email          = "igopood33@gmail.com"
    email_verified = true
  }
  message_action = "SUPPRESS"
  lifecycle {
    ignore_changes = [password]
  }
}