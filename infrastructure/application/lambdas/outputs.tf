output "lambda_function_arns" {
  description = "ARNs wszystkich funkcji Lambda"
  value = {
    for key, mod in module.lambda_functions :
    key => mod.function_arn
  }
}

output "lambda_function_names" {
  description = "Nazwy funkcji Lambda (resource names, nie ARNs)"
  value = {
    for key, mod in module.lambda_functions :
    key => mod.function_name
  }
}

output "lambda_invoke_arns" {
  description = "Invoke ARNs (np. dla API Gateway integracji)"
  value = {
    for key, mod in module.lambda_functions :
    key => mod.invoke_arn
  }
}

output "lambda_details" {
  description = "Szczegóły wszystkich funkcji Lambda, w tym tagi i ARN"
  value = {
    for key, mod in module.lambda_functions : key => {
      arn  = mod.function_arn
      tags = mod.tags
      name = mod.function_name
    }
  }
}
