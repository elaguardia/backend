package com.orderingapp.backend.model;

import lombok.Data;

@Data
public class ShippingInfo {
	private String firstName;
	private String lastName;
	private String middleName;
	private String postalCode;
	private String addressLine1;
	private String addressLine2;
	private String municipality;
	private String brgy;
	private String region;
	private String province;
	private String country;
	private String deliveryMethod;
	private String shippingPhone;
	private String email;
	private String deliveryStart;
	private String deliveryEnd;
	private Boolean consent;

}