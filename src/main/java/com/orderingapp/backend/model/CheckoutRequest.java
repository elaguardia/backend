package com.orderingapp.backend.model;

import java.util.List;

import lombok.Data;

@Data
public class CheckoutRequest {
//    private String name;
//    private String address;
//    private String contactNumber;
	
	private List<CartItemRequest> cartItems;
    private ShippingInfo shippingInfo;
}