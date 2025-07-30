output "sns_topic_arn" {
  value = module.report_notifications_topic.topic_arn
}

output "sqs_queue_arn" {
  value = module.report_jobs_queue.queue_arn
}

output "sqs_queue_url" {
  value = module.report_jobs_queue.queue_url
}