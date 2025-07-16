resource "aws_sfn_state_machine" "this" {
  name     = var.state_machine_name
  role_arn = var.iam_role_arn

  definition = templatefile(var.definition_template_path, var.definition_template_vars)

  tags = var.tags
}