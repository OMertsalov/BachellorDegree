package com.example.demo.model;

import java.util.LinkedHashMap;
import java.util.Map;

public class ReceiptForm {

	private Receipt receipt;
	private Map<String, ReceiptItem> receiptItems = new LinkedHashMap<>();
	
	public ReceiptForm() {
	}
	

	public ReceiptForm(Receipt receipt, Map<String, ReceiptItem> receiptItems) {
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

	public Map<String, ReceiptItem> getReceiptItems() {
		return receiptItems;
	}

	public void setReceiptItems(Map<String, ReceiptItem> receiptItems) {
		this.receiptItems = receiptItems;
	}
	
	
}
