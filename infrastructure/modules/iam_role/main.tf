resource "aws_iam_role" "this" {
  name               = var.role_name
  assume_role_policy = var.assume_role_policy_json
  tags               = var.tags
}

resource "aws_iam_role_policy_attachment" "this" {
  for_each = var.policy_arns_to_attach

  role       = aws_iam_role.this.name
  policy_arn = each.value
}