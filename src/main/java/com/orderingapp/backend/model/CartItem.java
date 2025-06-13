package com.orderingapp.backend.model;

import lombok.Data;

@Data
public class CartItem {
	private Long productId;
	private int quantity;
	private Product product;

	public CartItem(Long productId, int quantity, Product product) {
		this.productId = productId;
		this.quantity = quantity;
		this.product = product;
	}
}