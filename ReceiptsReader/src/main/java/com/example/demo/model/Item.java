package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="t_item")
public class Item {

	@Id
	@GeneratedValue
	private Long id;
	
	private String name;
	
	@ManyToOne
	@JoinColumn(name="tax_id")
	private Tax tax;
	
	@Column(name="is_temporary")
	private boolean temporary;

	public Item() {}
	
	public Item(String name, Tax tax, boolean temporary) {
		super();
		this.name = name;
		this.tax = tax;
		this.temporary = temporary;
	}

	public Item(String name,Tax tax) {
		this.name = name;
		this.tax = tax;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Tax getTax() {
		return tax;
	}

	public void setTax(Tax tax) {
		this.tax = tax;
	}

	public boolean isTemporary() {
		return temporary;
	}

	public void setTemporary(boolean temporary) {
		this.temporary = temporary;
	}

	@Override
	public String toString() {
		return "Item [name=" + name + ", tax=" + tax + ", temporary=" + temporary + "]";
	}
	
	
	
}
