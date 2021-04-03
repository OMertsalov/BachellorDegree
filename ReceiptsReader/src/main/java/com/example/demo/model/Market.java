package com.example.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="t_market")
public class Market {

	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String address;
	private String partnership;
	
	@Column(name="partnership_address")
	private String partnershipAddress;
	
	public Market() {
	}

	public Market(String name, String address, String partnership, String partnershipAddress) {
		super();
		this.name = name;
		this.address = address;
		this.partnership = partnership;
		this.partnershipAddress = partnershipAddress;
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPartnership() {
		return partnership;
	}

	public void setPartnership(String partnership) {
		this.partnership = partnership;
	}

	public String getPartnershipAddress() {
		return partnershipAddress;
	}

	public void setPartnershipAddress(String partnershipAddress) {
		this.partnershipAddress = partnershipAddress;
	}

	@Override
	public String toString() {
		return "Market [id=" + id + ", name=" + name + ", address=" + address + ", partnership=" + partnership
				+ ", partnershipAddress=" + partnershipAddress + "]";
	}
	
	
	
}
