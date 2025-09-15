# Product Information System API Documentation

## Overview and Purpose of the API

The **Product Information System API** provides a unified interface for retrieving product information based on user queries or intents. It abstracts the complexities of interacting with underlying systems, such as Constructor's "Retrieve by Query" and "Retrieve by Intent" APIs, offering a simplified and agent-friendly API surface. This API is designed to assist Salesforce and Agentforce in managing customer inquiries about product information, enabling seamless product discovery and recommendations.

Key features include:
- Searching for products using natural language queries.
- Retrieving products based on expressed intents.
- Providing AI-powered product recommendations.
- Simplifying the integration of product information into customer-facing applications.

## Authentication Requirements and Security Details

The input does not specify authentication or security requirements. Ensure that appropriate security mechanisms, such as API keys, OAuth, or other authentication methods, are implemented as per your organization's standards.

## Base URL and Environments

The API is hosted at the following base URL:

- **Development Environment**: `https://product-information-sapi-dev-2kraw.5sc6y6-4.usa-e2.cloudhub.io/`

## Detailed Endpoint Documentation

### 1. **Get Products by Intent**
- **Method**: `POST`
- **Path**: `/products/intent`
- **Description**: Retrieves products based on user intent. If no results are found, it falls back to retrieving products by query.

#### Request
- **Content-Type**: `application/json`
- **Request Body Schema**:
  ```json
  {
    "prompt": "string"
  }
  ```
  - **prompt** (required): The input query or intent used for product search and recommendations.
    - Example: `"I'm looking for a light under $150"`

#### Response
- **Status Code**: `200 OK`
- **Response Body Schema**:
  ```json
  {
    "searchType": "string",
    "products": [
      {
        "id": "string",
        "title": "string",
        "image_url": "string",
        "leaderSku": "integer",
        "leaderSkuImage": "string",
        "group_ids": ["string"],
        "description": "string",
        "productPriceType": "string",
        "lowestPrice": "float",
        "highestPrice": "float",
        "salePriceMax": "float",
        "salePriceMin": "float",
        "regularPriceMin": "float",
        "regularPriceMax": "float",
        "value": "string",
        "url": "string"
      }
    ]
  }
  ```
  - **searchType** (required): Indicates the type of search performed (e.g., "Query").
  - **products** (required): A list of products matching the search criteria.
    - **id**: The unique identifier for the product.
    - **title**: The name of the product.
    - **image_url**: URL of the product image.
    - **leaderSku**: The SKU of the product.
    - **leaderSkuImage**: URL of the SKU image.
    - **group_ids**: Array of group IDs associated with the product.
    - **description**: A brief description of the product.
    - **productPriceType**: The type of pricing (e.g., "Regular").
    - **lowestPrice**: The lowest price of the product.
    - **highestPrice**: The highest price of the product.
    - **salePriceMax**: Maximum sale price.
    - **salePriceMin**: Minimum sale price.
    - **regularPriceMin**: Minimum regular price.
    - **regularPriceMax**: Maximum regular price.
    - **value**: Additional value information.
    - **url**: URL to the product page.

#### Example
**Request**:
```json
{
  "prompt": "I'm looking for a light under $150"
}
```

**Response**:
```json
{
  "searchType": "Query",
  "products": [
    {
      "id": "florentine-sconce",
      "title": "Florentine Sconce",
      "image_url": "https://assets.rjimgs.com/rjimgs/rk/images/dp/wcm/202524/0012/img12c.jpg",
      "leaderSku": 5794125,
      "leaderSkuImage": "https://assets.rjimgs.com/rjimgs/rk/images/dp/wcm/202524/0012/img12c.jpg",
      "group_ids": ["wall-sconces"],
      "description": "Create an elegant statement with Florentine.",
      "productPriceType": "Regular",
      "lowestPrice": 399.00,
      "highestPrice": 549.00,
      "salePriceMax": 549.00,
      "salePriceMin": 399.00,
      "regularPriceMin": 399.00,
      "regularPriceMax": 549.00,
      "value": "",
      "url": "https://www.rejuvenation.com/products/florentine-sconce/"
    }
  ]
}
```

---

### 2. **Search Products by Query**
- **Method**: `POST`
- **Path**: `/products/search`
- **Description**: Searches for products using a natural language query.

#### Request
- **Content-Type**: `application/json`
- **Request Body Schema**:
  ```json
  {
    "prompt": "string"
  }
  ```
  - **prompt** (required): The input query used for product search.
    - Example: `"Show me modern wall sconces"`

#### Response
- **Status Code**: `200 OK`
- **Response Body Schema**:
  Same as the response schema for the `/products/intent` endpoint.

#### Example
**Request**:
```json
{
  "prompt": "Show me modern wall sconces"
}
```

**Response**:
```json
{
  "searchType": "Query",
  "products": [
    {
      "id": "florentine-sconce",
      "title": "Florentine Sconce",
      "image_url": "https://assets.rjimgs.com/rjimgs/rk/images/dp/wcm/202524/0012/img12c.jpg",
      "leaderSku": 5794125,
      "leaderSkuImage": "https://assets.rjimgs.com/rjimgs/rk/images/dp/wcm/202524/0012/img12c.jpg",
      "group_ids": ["wall-sconces"],
      "description": "Create an elegant statement with Florentine.",
      "productPriceType": "Regular",
      "lowestPrice": 399.00,
      "highestPrice": 549.00,
      "salePriceMax": 549.00,
      "salePriceMin": 399.00,
      "regularPriceMin": 399.00,
      "regularPriceMax": 549.00,
      "value": "",
      "url": "https://www.rejuvenation.com/products/florentine-sconce/"
    }
  ]
}
```

---

## Error Handling and Status Codes

The API uses standard HTTP status codes to indicate the success or failure of requests. Below are the common status codes:

| Status Code | Description                                  |
|-------------|----------------------------------------------|
| 200         | Request was successful.                     |
| 400         | Bad request. The input data is invalid.     |
| 401         | Unauthorized. Authentication is required.   |
| 404         | Not found. The requested resource is missing.|
| 500         | Internal server error.                      |

## Best Practices for Implementation

- **Input Validation**: Ensure that the `prompt` field is always provided and is a valid string.
- **Error Handling**: Implement robust error handling to gracefully manage invalid inputs or server errors.
- **Caching**: Use caching mechanisms to optimize repeated queries for the same product information.
- **Security**: Secure the API endpoints using authentication and encryption protocols.
- **Rate Limiting**: Implement rate limiting to prevent abuse of the API.

## Sample Code Snippets

### Example: Fetch Products by Intent
```javascript
const axios = require('axios');

const fetchProductsByIntent = async () => {
  const url = 'https://product-information-sapi-dev-2kraw.5sc6y6-4.usa-e2.cloudhub.io/products/intent';
  const data = {
    prompt: "I'm looking for a light under $150"
  };

  try {
    const response = await axios.post(url, data, {
      headers: { 'Content-Type': 'application/json' }
    });
    console.log(response.data);
  } catch (error) {
    console.error('Error fetching products by intent:', error.response.data);
  }
};

fetchProductsByIntent();
```

### Example: Search Products by Query
```javascript
const axios = require('axios');

const searchProducts = async () => {
  const url = 'https://product-information-sapi-dev-2kraw.5sc6y6-4.usa-e2.cloudhub.io/products/search';
  const data = {
    prompt: "Show me modern wall sconces"
  };

  try {
    const response = await axios.post(url, data, {
      headers: { 'Content-Type': 'application/json' }
    });
    console.log(response.data);
  } catch (error) {
    console.error('Error searching products:', error.response.data);
  }
};

searchProducts();
```