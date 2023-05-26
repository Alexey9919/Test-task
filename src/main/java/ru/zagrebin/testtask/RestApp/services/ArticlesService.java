package ru.zagrebin.testtask.RestApp.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zagrebin.testtask.RestApp.dto.ArticleDTO;
import ru.zagrebin.testtask.RestApp.models.Article;
import ru.zagrebin.testtask.RestApp.models.Product;
import ru.zagrebin.testtask.RestApp.repositories.ArticlesRepository;
import ru.zagrebin.testtask.RestApp.repositories.ProductsRepository;
import ru.zagrebin.testtask.RestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ArticlesService {

    private final ArticlesRepository articlesRepository;
    private final ArticleDTO articleDTO;
    private final ProductsService productsService;

    @Autowired
    public ArticlesService(ArticlesRepository articlesRepository, ArticleDTO articleDTO, ProductsService productsService) {
        this.articlesRepository = articlesRepository;
        this.articleDTO = articleDTO;
        this.productsService = productsService;
    }

    public List<Article> findAll() { return articlesRepository.findAll(); }

    public Article findOne(int id) {
        Optional<Article> foundArticle = articlesRepository.findById(id);
        return foundArticle.orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void save(Article article) {
        enrichArticle(article);
        articlesRepository.save(article);
    }

    @Transactional
    public void delete(int id) {
        articlesRepository.delete(findOne(id));
    }

    @Transactional
    public void update(int id, Article updatedArticle) {
        updatedArticle.setId(id);
        articlesRepository.save(updatedArticle);
    }

    private void enrichArticle(Article article) {
        article.setId(article.getId());
    }
}


