package com.example.demo.repository;


import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.Market;

public interface MarketRepository extends CrudRepository<Market, Long>{
	
	boolean existsByAddress(String address);
}
