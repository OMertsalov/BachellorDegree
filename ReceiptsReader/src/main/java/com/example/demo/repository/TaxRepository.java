package com.example.demo.repository;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.Tax;


public interface TaxRepository extends CrudRepository<Tax, Long>{
	
	Tax findBySign(char sign);
}

