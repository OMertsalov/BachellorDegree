package com.example.demo.services.receipt;

import org.springframework.validation.BindingResult;

import com.example.demo.model.ReceiptForm;

public interface ReceiptService {
	
	void validateData(ReceiptForm receiptForm, BindingResult result);
}

