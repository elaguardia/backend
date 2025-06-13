package com.orderingapp.backend.product;

import com.fasterxml.jackson.databind.JsonNode;
import com.orderingapp.backend.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "15") int limit,
            @RequestParam(required = false) String search) {
        try {
            String url = "https://fakestoreapi.com/products";
            if (search != null && !search.isEmpty()) {
                url = "https://fakestoreapi.com/products";
            }

            JsonNode response = restTemplate.getForObject(url, JsonNode.class);
            List<Product> products = new ArrayList<>();

            if (response == null || !response.isArray()) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body("Failed to fetch products: Invalid response from API");
            }

            int start = Math.min(skip, response.size());
            int end = Math.min(start + limit, response.size());

            for (int i = start; i < end; i++) {
                JsonNode node = response.get(i);
                Product product = new Product();
                product.setId(node.get("id").asLong());
                product.setTitle(node.get("title").asText());
                product.setCategory(node.get("category").asText());
                product.setPrice(node.get("price").asDouble());
                product.setRating(node.get("rating").get("rate").asDouble());
                product.setStock(node.get("rating").get("count").asInt());
                product.setThumbnail(node.get("image").asText());
                product.setBrand("Generic");

                product.setShippingInformation("Standard shipping: 3-5 days");
                products.add(product);
            }

            return ResponseEntity.ok(products);
        } catch (ResourceAccessException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Failed to fetch products: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing products: " + e.getMessage());
        }
    }

   
}