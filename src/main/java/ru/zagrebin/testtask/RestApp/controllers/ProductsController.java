package ru.zagrebin.testtask.RestApp.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.zagrebin.testtask.RestApp.dto.ProductDTO;
import ru.zagrebin.testtask.RestApp.models.Article;
import ru.zagrebin.testtask.RestApp.models.Product;
import ru.zagrebin.testtask.RestApp.services.ArticlesService;
import ru.zagrebin.testtask.RestApp.services.ProductsService;
import ru.zagrebin.testtask.RestApp.util.ErrorResponse;
import ru.zagrebin.testtask.RestApp.util.NotCreatedException;
import ru.zagrebin.testtask.RestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductsService productsService;
    private final ArticlesService articlesService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductsController(ProductsService productsService, ArticlesService articlesService, ModelMapper modelMapper) {
        this.productsService = productsService;
        this.articlesService = articlesService;
        this.modelMapper = modelMapper;
    }

    //Получить все продукты
    @GetMapping()
    public List<ProductDTO> getProducts() {
        return productsService.findAll().stream().map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    //Получить продукт по id
    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable("id") int id) {
        return convertToProductDTO(productsService.findOne(id));
    }

    //Создать новый продукт
    @PostMapping("/new")
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid ProductDTO productDTO,
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
        productsService.save(convertToProduct(productDTO));
        //отправляем HTTP ответ с пустым телом и со статусом 200
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //Удалить продукт
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") int id) {
        productsService.delete(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    //Обновить продукт
    @PatchMapping("/{id}")
    public ResponseEntity<HttpStatus> update(@RequestBody ProductDTO productDTO,
                                             @PathVariable("id") int id) {

        productsService.update(id, convertToProduct(productDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    /////ПОИСК СТАТЬИ ПО ID ПРОДУКТА/////
    @GetMapping("/search/{id}")
    public List<Article> search(@PathVariable("id") int id) {
        Product product = productsService.findOne(id);
        return articlesService.search(Optional.ofNullable(product));
    }


    /////СОРТИРОВКИ ПО ПОЛЯМ/////
    @GetMapping("/sort/name")
    public List<ProductDTO> getProductsByName() {
        return productsService.findAllByName().stream().map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/sort/description")
    public List<ProductDTO> getProductsByDescription() {
        return productsService.findAllByDescription().stream().map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/sort/cost")
    public List<ProductDTO> getProductsByCost() {
        return productsService.findAllByCost().stream().map(this::convertToProductDTO)
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

    //Конверторы Product <-> ProductDTO
    private Product convertToProduct(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    private ProductDTO convertToProductDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }
}
