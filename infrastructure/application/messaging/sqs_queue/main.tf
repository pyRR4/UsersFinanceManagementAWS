resource "aws_sqs_queue" "this" {
  name = var.is_fifo ? "${var.queue_name}.fifo" : var.queue_name

  fifo_queue = var.is_fifo

  content_based_deduplication = var.is_fifo

  tags = var.tags
}