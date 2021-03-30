package com.example.demo.model;

import java.util.Date;

public class Receipt {

	private int id;
	private Market market;
	private Date sellDate;
	private double taxSum;
	private double priceSum;
	
	public Receipt() {
	}
	
	public Receipt(Market market, Date sellDate, double taxSum, double priceSum) {
		this.market = market;
		this.sellDate = sellDate;
		this.taxSum = taxSum;
		this.priceSum = priceSum;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
	}

	public Date getSellDate() {
		return sellDate;
	}

	public void setSellDate(Date sellDate) {
		this.sellDate = sellDate;
	}

	public double getTaxSum() {
		return taxSum;
	}

	public void setTaxSum(double taxSum) {
		this.taxSum = taxSum;
	}

	public double getPriceSum() {
		return priceSum;
	}

	public void setPriceSum(double priceSum) {
		this.priceSum = priceSum;
	}

}
