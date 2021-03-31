package com.example.demo.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_receipt")
public class Receipt {

	@Id
	@GeneratedValue
	private Long id;
	
	@OneToMany
	@JoinColumn(name="market_id")
	private Market market;
	
	@Column(name="sell_date")
	private Date sellDate;
	
	@Column(name = "tax_sum")
	private double taxSum;
	
	@Column(name="price_sum")
	private double priceSum;
	
	@OneToMany(mappedBy = "receipt")
	private List<ReceiptItems> items = new ArrayList();
	
	@Transient
	private boolean containNewData;
	
	@Transient
	private String text;
	
	public Receipt() {
	}
	
	public Receipt(Market market, Date sellDate, double taxSum, double priceSum) {
		this.market = market;
		this.sellDate = sellDate;
		this.taxSum = taxSum;
		this.priceSum = priceSum;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<ReceiptItems> getItems() {
		return items;
	}

	public void setItems(List<ReceiptItems> items) {
		this.items = items;
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

	public boolean isContainNewData() {
		return containNewData;
	}

	public void setContainNewData(boolean containNewData) {
		this.containNewData = containNewData;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
