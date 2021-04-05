package com.example.demo.services.receipt;

import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import com.example.demo.model.ReceiptForm;
import com.example.demo.model.ReceiptItem;

@Service
public class ReceiptServiceImpl implements ReceiptService {

	@Override
	public void validateData(ReceiptForm receiptForm, BindingResult result) {
		validateItemData(receiptForm.getReceiptItems(), result);
	}

	private void validateItemData(Map<String, ReceiptItem> receiptItems, BindingResult result) {
		Set<String> keys = receiptItems.keySet();
		for (String key : keys) {
			ReceiptItem receiptItem = receiptItems.get(key);
			double calculatedItemPrice = Math.round(receiptItem.getAmount() * receiptItem.getItemPrice() * 100) / 100.0d;
			boolean isPriceSumCorrect = calculatedItemPrice == receiptItem.getPriceSum();
			if (!isPriceSumCorrect || !isDataByUserCorrect(receiptItem.toLineText(), receiptItem.getLineTextByOCR())) {
				result.rejectValue("receiptItems[" + key + "]", "");
			}
		}
	}

	private boolean isDataByUserCorrect(String userSeq, String ocrSeq) {
		StringBuilder longestSeq = new StringBuilder(userSeq);
		String shortestSeq = ocrSeq;
		if (userSeq.length() < ocrSeq.length()) {
			longestSeq = new StringBuilder(ocrSeq);
			shortestSeq = userSeq;
		}
		int lengthDiff = longestSeq.length() - shortestSeq.length();
		int differences = lengthDiff;
		for (int i = 0; i < shortestSeq.length(); i++) {
			int iter = i - lengthDiff;
			if (iter < 0)
				iter = 0;
			boolean equal = false;
			for (int j = iter; j <= i + lengthDiff; j++) {
				char a = shortestSeq.charAt(i);
				char b = longestSeq.charAt(j);
				if (a == b) {
					equal = true;
					longestSeq.setCharAt(j, '@');
					break;
				}
			}
			if (!equal)
				differences++;
		}
		return (double)differences/ocrSeq.length() <=  0.25;
	}
}
