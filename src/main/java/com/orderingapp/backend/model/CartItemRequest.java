package com.orderingapp.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CartItemRequest {

	@JsonProperty("id")
	private Long productId;
    private Integer quantity;
    private Double price;
	
}