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
import ru.zagrebin.testtask.RestApp.dto.ProductDTO;
import ru.zagrebin.testtask.RestApp.models.Article;
import ru.zagrebin.testtask.RestApp.models.Product;
import ru.zagrebin.testtask.RestApp.services.ArticlesService;
import ru.zagrebin.testtask.RestApp.services.ProductsService;
import ru.zagrebin.testtask.RestApp.util.ErrorResponse;
import ru.zagrebin.testtask.RestApp.util.NotCreatedException;
import ru.zagrebin.testtask.RestApp.util.NotFoundException;

import java.util.List;
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


    @GetMapping()
    public List<ArticleDTO> getArticles() {
        return articlesService.findAll().stream().map(this::convertToArticleDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ArticleDTO getArticle(@PathVariable("id") int id) {
        return convertToArticleDTO(articlesService.findOne(id));
    }

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
        //отправляем HTTP ответ с пустым телом и со статусом 200
        return ResponseEntity.ok(HttpStatus.OK);
    }


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

    private Article convertToArticle(ArticleDTO articleDTO) {
        return modelMapper.map(articleDTO, Article.class);
    }

    private ArticleDTO convertToArticleDTO(Article article) {
        return modelMapper.map(article, ArticleDTO.class);
    }
}














