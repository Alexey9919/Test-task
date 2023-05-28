package ru.zagrebin.testtask.RestApp.controllers;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.zagrebin.testtask.RestApp.dto.ArticleDTO;
import ru.zagrebin.testtask.RestApp.models.Article;
import ru.zagrebin.testtask.RestApp.models.Product;
import ru.zagrebin.testtask.RestApp.services.ArticlesService;
import ru.zagrebin.testtask.RestApp.util.ErrorResponse;
import ru.zagrebin.testtask.RestApp.util.NotCreatedException;
import ru.zagrebin.testtask.RestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/articles")
public class ArticlesController {

    private final ArticlesService articlesService;
    private final ModelMapper modelMapper;


    @Autowired
    public ArticlesController(ArticlesService articlesService, ModelMapper modelMapper) {
        this.articlesService = articlesService;
        this.modelMapper = modelMapper;
    }


    //Получить все статьи
    @GetMapping()
    public List<ArticleDTO> getArticles() {
        return articlesService.findAll().stream().map(this::convertToArticleDTO)
                .collect(Collectors.toList());
    }

    //Получить статью по id
    @GetMapping("/{id}")
    public ArticleDTO getArticle(@PathVariable("id") int id) {
        return convertToArticleDTO(articlesService.findOne(id));
    }

    //Создать новую статью
    @PostMapping("/new")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid ArticleDTO articleDTO,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error : errors) {
                errorMsg.append(error.getField())
                        .append(" - ").append(error.getDefaultMessage())
                        .append(";");
            }

            throw new NotCreatedException(errorMsg.toString());
        }
        articlesService.save(convertToArticle(articleDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //Удалить статью
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        articlesService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //Обновить статью
    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@RequestBody ArticleDTO articleDTO,
                                             @PathVariable("id") int id) {

        articlesService.update(id, convertToArticle(articleDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }

    /////ПОИСК ПРОДУКТА ПО ID СТАТЬИ/////
    @GetMapping("/search/{id}")
    public Optional<Product> search(@PathVariable("id") int id) {
        return articlesService.searchProduct(id);
    }



    //////////МЕТОДЫ ДЛЯ СОРТИРОВКИ//////////

    @GetMapping("/sort/product")
    public List<ArticleDTO> getArticlesByProduct() {
        return articlesService.findAllByProduct().stream().map(this::convertToArticleDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/sort/name")
    public List<ArticleDTO> getArticlesByName() {
        return articlesService.findAllByName().stream().map(this::convertToArticleDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/sort/content")
    public List<ArticleDTO> getArticlesByContent() {
        return articlesService.findAllByContent().stream().map(this::convertToArticleDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/sort/date")
    public List<ArticleDTO> getArticlesByDate() {
        return articlesService.findAllByDate().stream().map(this::convertToArticleDTO)
                .collect(Collectors.toList());
    }


    //Обработка Exception
    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(NotFoundException e) {
        ErrorResponse response = new ErrorResponse(
                "Nothing found!",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<ErrorResponse> handleException(NotCreatedException e) {
        ErrorResponse response = new ErrorResponse(
                e.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Конверторы Article <-> ArticleDTO
    private Article convertToArticle(ArticleDTO articleDTO) {
        return modelMapper.map(articleDTO, Article.class);
    }

    private ArticleDTO convertToArticleDTO(Article article) {
        return modelMapper.map(article, ArticleDTO.class);
    }
}














