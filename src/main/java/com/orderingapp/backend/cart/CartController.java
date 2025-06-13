package com.orderingapp.backend.cart;

import com.fasterxml.jackson.databind.JsonNode;
import com.orderingapp.backend.model.CartItem;
import com.orderingapp.backend.model.CartItemRequest;
import com.orderingapp.backend.model.CheckoutConfirmation;
import com.orderingapp.backend.model.CheckoutRequest;
import com.orderingapp.backend.model.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

	@Autowired
	private RestTemplate restTemplate;

	private final Map<Long, Integer> stockCache = new HashMap<>();
	private final List<CartItem> cart = new ArrayList<>();

	@GetMapping
	public ResponseEntity<List<CartItem>> getCart() {
		return ResponseEntity.ok(cart);
	}

	@DeleteMapping("/item/{id}")
	public ResponseEntity<String> deleteFromCart(@PathVariable Long id) {
		cart.removeIf(item -> item.getProductId().equals(id));
		return ResponseEntity.ok("Item removed from cart");
	}

	@PostMapping("/checkout")
	public ResponseEntity<CheckoutConfirmation> checkout(@RequestBody CheckoutRequest request) {
		// Validate cartItems
		if (request.getCartItems() == null || request.getCartItems().isEmpty()) {
			return ResponseEntity.badRequest().body(new CheckoutConfirmation("Cart is empty"));
		}


		// Validate stock and calculate total 
		double totalPrice = 0.0;
		for (CartItemRequest item : request.getCartItems()) {
			JsonNode productNode = restTemplate.getForObject("https://fakestoreapi.com/products/" + item.getProductId(),
					JsonNode.class);
			int availableStock = productNode.get("rating").get("count").asInt();
			
			stockCache.putIfAbsent(item.getProductId(), availableStock);
			if (stockCache.get(item.getProductId()) < item.getQuantity()) {
				return ResponseEntity.badRequest()
						.body(new CheckoutConfirmation("Insufficient stock for product ID: " + item.getProductId()));
			}
			// Verify price matches frontend to prevent tampering
			double serverPrice = productNode.get("price").asDouble();
			if (Math.abs(serverPrice - item.getPrice()) > 0.01) {
				return ResponseEntity.badRequest()
						.body(new CheckoutConfirmation("Price mismatch for product ID: " + item.getProductId()));
			}
			totalPrice += serverPrice * item.getQuantity();
		}

		// Update stock
		for (CartItemRequest item : request.getCartItems()) {
			stockCache.compute(item.getProductId(), (k, v) -> v - item.getQuantity());
		}

		// Generate order confirmation
		CheckoutConfirmation confirmation = new CheckoutConfirmation();
		confirmation.setOrderId(UUID.randomUUID().toString());
		confirmation.setTotal(totalPrice);
		// Clear server-side cart (if still used)
		cart.clear();

		return ResponseEntity.status(201).body(confirmation);
	}
}