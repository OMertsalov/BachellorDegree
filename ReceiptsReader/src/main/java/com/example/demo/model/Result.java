package com.example.demo.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class Result {

	private Receipt receipt;
	private Map<String, ReceiptItems> receiptItems = new LinkedHashMap<>();
	
	public Result() {
	}
	

	public Result(Receipt receipt, Map<String, ReceiptItems> receiptItems) {
		super();
		this.receipt = receipt;
		this.receiptItems = receiptItems;
	}



	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public Map<String, ReceiptItems> getReceiptItems() {
		return receiptItems;
	}

	public void setReceiptItems(Map<String, ReceiptItems> receiptItems) {
		this.receiptItems = receiptItems;
	}
	
	
}
