package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="t_receipt_items")
public class ReceiptItems{
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="receipt_id")
	private Receipt receipt;
	
	@ManyToOne
	@JoinColumn(name="item_id")
	private Item item;

	private double amount = 1.00;
	
	@Column(name="item_price")
	private double itemPrice;
	
	@Column(name="price_sum")
	private double priceSum;

	public ReceiptItems() {}
	
	public ReceiptItems(Receipt receipt, Item item, double amount, double itemPrice, double priceSum) {
		super();
		this.receipt = receipt;
		this.item = item;
		this.amount = amount;
		this.itemPrice = itemPrice;
		this.priceSum = priceSum;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Receipt getReceipt() {
		return receipt;
	}

	public void setReceipt(Receipt receipt) {
		this.receipt = receipt;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(double itemPrice) {
		this.itemPrice = itemPrice;
	}

	public double getPriceSum() {
		return priceSum;
	}

	public void setPriceSum(double priceSum) {
		this.priceSum = priceSum;
	}
	
	
}
