package ru.zagrebin.testtask.RestApp.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.zagrebin.testtask.RestApp.models.Article;
import ru.zagrebin.testtask.RestApp.models.Product;

import java.util.List;
import java.util.Optional;

public interface ArticlesRepository extends JpaRepository<Article, Integer> {

    List<Article> findByProduct(Optional<Product> product);
}
