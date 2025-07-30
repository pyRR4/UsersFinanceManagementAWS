module "report_notifications_topic" {
  source     = "./sns_topic"
  topic_name = "${var.project_name}-${var.environment}-report-notifications"
  tags       = var.tags
}

module "report_jobs_queue" {
  source     = "./sqs_queue"
  queue_name = "${var.project_name}-${var.environment}-report-jobs"
  is_fifo    = true
  tags       = var.tags
}