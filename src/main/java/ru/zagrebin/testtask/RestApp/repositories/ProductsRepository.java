package ru.zagrebin.testtask.RestApp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.zagrebin.testtask.RestApp.models.Product;

@Repository
public interface ProductsRepository extends JpaRepository<Product, Integer> {
}
