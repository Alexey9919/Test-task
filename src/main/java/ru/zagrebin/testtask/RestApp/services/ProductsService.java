package ru.zagrebin.testtask.RestApp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.zagrebin.testtask.RestApp.models.Product;
import ru.zagrebin.testtask.RestApp.repositories.ProductsRepository;
import ru.zagrebin.testtask.RestApp.util.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductsService {

    private final ProductsRepository productsRepository;

    @Autowired
    public ProductsService(ProductsRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    public List<Product> findAll() { return productsRepository.findAll(); }

    public Product findOne(int id) {
        Optional<Product> foundProduct = productsRepository.findById(id);
        return foundProduct.orElseThrow(NotFoundException::new);
    }

    @Transactional
    public void save(Product product) {
        enrichProduct(product);
        productsRepository.save(product);
    }

    @Transactional
    public void delete(int id) {
        productsRepository.delete(findOne(id));
    }

    @Transactional
    public void update(int id, Product updatedProduct) {
        updatedProduct.setId(id);
        productsRepository.save(updatedProduct);
    }

    /////Методы для сортировки/////
    @Transactional
    public List<Product> findAllByName() {
        List<Product> products = productsRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
        return products;
    }

    @Transactional
    public List<Product> findAllByDescription() {
        List<Product> products = productsRepository.findAll(Sort.by(Sort.Direction.ASC, "description"));
        return products;
    }

    @Transactional
    public List<Product> findAllByCost() {
        List<Product> products = productsRepository.findAll(Sort.by(Sort.Direction.ASC, "cost"));
        return products;
    }


    private void enrichProduct(Product product) {
        product.setId(product.getId());
    }
}
