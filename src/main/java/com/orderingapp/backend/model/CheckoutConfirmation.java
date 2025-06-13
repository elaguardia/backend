package com.orderingapp.backend.model;

import lombok.Data;

import java.util.List;

@Data
public class CheckoutConfirmation {
	private String orderId;
	private Double total;
	private String message;

	// No-arg constructor for Jackson
	public CheckoutConfirmation() {
	}

	// Constructor for error messages
	public CheckoutConfirmation(String message) {
		this.message = message;
	}
}