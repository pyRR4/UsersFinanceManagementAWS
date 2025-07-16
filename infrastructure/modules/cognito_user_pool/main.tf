resource "aws_cognito_user_pool" "this" {
  name = var.pool_name
  tags = var.tags

  username_attributes = ["email"]

  auto_verified_attributes = ["email"]

  password_policy {
    minimum_length    = 8
    require_lowercase = true
    require_numbers   = true
    require_symbols   = true
    require_uppercase = true
  }
}

resource "aws_cognito_user_pool_client" "this" {
  name         = var.app_client_name

  generate_secret = false

  explicit_auth_flows = [
    "ADMIN_NO_SRP_AUTH",
    "USER_PASSWORD_AUTH"
  ]
  supported_identity_providers = ["COGNITO"]
  user_pool_id = aws_cognito_user_pool.this.id

  access_token_validity  = 60    # Ważność Access Tokenu: 60 minut
  id_token_validity      = 60    # Ważność ID Tokenu: 60 minut
  refresh_token_validity = 30    # Ważność Refresh Tokenu: 30 dni

  token_validity_units {
    access_token  = "minutes"
    id_token      = "minutes"
    refresh_token = "days"
  }
}

