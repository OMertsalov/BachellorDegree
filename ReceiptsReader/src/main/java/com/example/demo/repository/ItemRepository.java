package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.example.demo.model.Item;

public interface ItemRepository extends CrudRepository<Item, Long>{
	Optional<Item> findByName(String name);

}
