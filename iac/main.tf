terraform {
  required_providers {
    opensearch = {
      source  = "opensearch-project/opensearch"
      version = "2.3.2"
    }
  }
}

provider "opensearch" {
  url      = "http://127.0.0.1:9200"
  insecure = true
  username = "admin"
  password = "SuperSecretPassword123!"
}


#  Product Document
# {
#   "id": "xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"
#   "name": "Product Name",
#   "description": "Product Description",
#   "tags": ["tag1", "tag2", "tag3"]
# }

resource "opensearch_index" "product_idx" {
  name = "product-v1"
  aliases = jsonencode({
    "products" : {
      "is_write_index" : true
    }
  })
  mappings = jsonencode({
    "properties" : {
      "id" : {
        "type" : "keyword"
      },
      "name" : {
        "type" : "search_as_you_type",
        "fields" : {
          "raw" : {
            "type" : "keyword"
          }
        }
      },
      "tags" : {
        "type" : "keyword"
      },
      "description" : {
        "type" : "text"
      }
    }
  })
}
