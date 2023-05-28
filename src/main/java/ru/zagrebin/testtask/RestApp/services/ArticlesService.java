package ru.zagrebin.testtask.RestApp.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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

    private final ProductsRepository productsRepository;
    private final ArticleDTO articleDTO;
    private final ProductsService productsService;

    @Autowired
    public ArticlesService(ArticlesRepository articlesRepository, ArticleDTO articleDTO
            , ProductsService productsService, ProductsRepository productsRepository) {
        this.articlesRepository = articlesRepository;
        this.articleDTO = articleDTO;
        this.productsService = productsService;
        this.productsRepository = productsRepository;
    }

    //Получить все статьи
    public List<Article> findAll() {
        return articlesRepository.findAll();
    }

    //Получить статьи по id
    public Article findOne(int id) {
        Optional<Article> foundArticle = articlesRepository.findById(id);
        return foundArticle.orElseThrow(NotFoundException::new);
    }

    //Сохранить  статью
    @Transactional
    public void save(Article article) {
        enrichArticle(article);
        articlesRepository.save(article);
    }

    //Удалить статью
    @Transactional
    public void delete(int id) {
        articlesRepository.delete(findOne(id));
    }

    //Обновить статью
    @Transactional
    public void update(int id, Article updatedArticle) {
        updatedArticle.setId(id);
        articlesRepository.save(updatedArticle);
    }

    //Поиск статей по продукту
    @Transactional
    public List<Article> search(Optional<Product> product) {
        List<Article> list = articlesRepository.findByProduct(product);
        return list;
    }

    //Поиск продукта по id статьи
    @Transactional
    public Optional<Product> searchProduct(int id) {
        Article art = findOne(id);
        int idProduct = art.getProduct().getId();
        return productsRepository.findById(idProduct);
    }


    //МЕТОДЫ ДЛЯ СОРТИРОВКИ ПО ПОЛЯМ/////

    @Transactional
    public List<Article> findAllByProduct() {
        List<Article> articles = articlesRepository.findAll(Sort.by(Sort.Direction.ASC, "product"));
        return articles;
    }

    @Transactional
    public List<Article> findAllByName() {
        List<Article> articles = articlesRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        return articles;
    }

    @Transactional
    public List<Article> findAllByContent() {
        List<Article> articles = articlesRepository.findAll(Sort.by(Sort.Direction.ASC, "content"));
        return articles;
    }

    @Transactional
    public List<Article> findAllByDate() {
        List<Article> articles = articlesRepository.findAll(Sort.by(Sort.Direction.ASC, "date"));
        return articles;
    }


    //Дополнительное заполнение полей в Article
    private void enrichArticle(Article article) {
        article.setId(article.getId());
    }
}


