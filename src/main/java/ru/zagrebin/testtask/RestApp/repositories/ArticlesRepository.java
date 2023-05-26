package ru.zagrebin.testtask.RestApp.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.zagrebin.testtask.RestApp.models.Article;
import ru.zagrebin.testtask.RestApp.models.Product;

public interface ArticlesRepository extends JpaRepository<Article, Integer> {
}
