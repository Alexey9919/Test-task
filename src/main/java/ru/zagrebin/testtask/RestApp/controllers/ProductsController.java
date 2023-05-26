package ru.zagrebin.testtask.RestApp.controllers;

import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import ru.zagrebin.testtask.RestApp.dto.ProductDTO;
import ru.zagrebin.testtask.RestApp.models.Product;
import ru.zagrebin.testtask.RestApp.services.ProductsService;
import ru.zagrebin.testtask.RestApp.util.ErrorResponse;
import ru.zagrebin.testtask.RestApp.util.NotCreatedException;
import ru.zagrebin.testtask.RestApp.util.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductsService productsService;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductsController(ProductsService productsService, ModelMapper modelMapper) {
        this.productsService = productsService;
        this.modelMapper = modelMapper;
    }


    @GetMapping()
    public List<ProductDTO> getProducts() {
        return productsService.findAll().stream().map(this::convertToProductDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProductDTO getProduct(@PathVariable("id") int id) {
        return convertToProductDTO(productsService.findOne(id));
    }

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

    private Product convertToProduct(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    private ProductDTO convertToProductDTO(Product product) {
        return modelMapper.map(product, ProductDTO.class);
    }
}
