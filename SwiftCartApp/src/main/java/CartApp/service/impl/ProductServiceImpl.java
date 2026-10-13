package CartApp.service.impl;

import CartApp.dto.request.ProductRequestDTO;
import CartApp.dto.response.PageResponseDTO;
import CartApp.dto.response.ProductResponseDTO;
import CartApp.entity.Product;
import CartApp.exception.DuplicateResourceException;
import CartApp.exception.ResourceNotFoundException;
import CartApp.repository.ProductRepository;
import CartApp.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponseDTO createProduct(ProductRequestDTO productRequestDTO) {
        if (productRequestDTO.getSku() != null && productRepository.existsBySku(productRequestDTO.getSku())) {
            throw new DuplicateResourceException("Product", "SKU", productRequestDTO.getSku());
        }

        Product product = Product.builder()
                .name(productRequestDTO.getName())
                .description(productRequestDTO.getDescription())
                .price(productRequestDTO.getPrice() != null ? productRequestDTO.getPrice() : 0.0)
                .stockQuantity(productRequestDTO.getStockQuantity())
                .category(productRequestDTO.getCategory())
                .brand(productRequestDTO.getBrand())
                .imageUrl(productRequestDTO.getImageUrl())
                .sku(productRequestDTO.getSku())
                .isAvailable(productRequestDTO.getIsAvailable() != null ? productRequestDTO.getIsAvailable() : true)
                .build();

        Product savedProduct = productRepository.save(product);
        return mapToResponseDTO(savedProduct);
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = findProductById(id);
        return mapToResponseDTO(product);
    }

    @Override
    public ProductResponseDTO getProductBySku(String sku) {
        Optional<Product> productOptional = productRepository.findBySku(sku);
        if (productOptional.isPresent()) {
            return mapToResponseDTO(productOptional.get());
        } else {
            throw new ResourceNotFoundException("Product", "SKU", sku);
        }
    }

    @Override
    public List<ProductResponseDTO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductResponseDTO> responseList = new ArrayList<>();
        for (Product product : products) {
            responseList.add(mapToResponseDTO(product));
        }
        return responseList;
    }

    @Override
    public PageResponseDTO<ProductResponseDTO> getAllProductsPaginated(int page, int size, String sortBy,
            String sortDir) {
        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        Page<Product> productPage = productRepository.findAll(pageable);
        return mapToPageResponse(productPage);
    }

    @Override
    public List<ProductResponseDTO> getAvailableProducts() {
        List<Product> products = productRepository.findByIsAvailableTrue();
        List<ProductResponseDTO> responseList = new ArrayList<>();
        for (Product product : products) {
            responseList.add(mapToResponseDTO(product));
        }
        return responseList;
    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategoryIgnoreCase(category);
        List<ProductResponseDTO> responseList = new ArrayList<>();
        for (Product product : products) {
            responseList.add(mapToResponseDTO(product));
        }
        return responseList;
    }

    @Override
    public List<ProductResponseDTO> getProductsByPriceRange(double minPrice, double maxPrice) {
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice);
        List<ProductResponseDTO> responseList = new ArrayList<>();
        for (Product product : products) {
            responseList.add(mapToResponseDTO(product));
        }
        return responseList;
    }

    @Override
    public PageResponseDTO<ProductResponseDTO> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.searchProducts(keyword, pageable);
        return mapToPageResponse(productPage);
    }

    @Override
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productRequestDTO) {
        Product product = findProductById(id);

        if (productRequestDTO.getSku() != null
                && !productRequestDTO.getSku().equals(product.getSku())
                && productRepository.existsBySku(productRequestDTO.getSku())) {
            throw new DuplicateResourceException("Product", "SKU", productRequestDTO.getSku());
        }

        product.setName(productRequestDTO.getName());
        product.setDescription(productRequestDTO.getDescription());
        if (productRequestDTO.getPrice() != null) {
            product.setPrice(productRequestDTO.getPrice());
        }
        product.setStockQuantity(productRequestDTO.getStockQuantity());
        product.setCategory(productRequestDTO.getCategory());
        product.setBrand(productRequestDTO.getBrand());
        product.setImageUrl(productRequestDTO.getImageUrl());
        product.setSku(productRequestDTO.getSku());
        if (productRequestDTO.getIsAvailable() != null) {
            product.setIsAvailable(productRequestDTO.getIsAvailable());
        }

        Product updatedProduct = productRepository.save(product);
        return mapToResponseDTO(updatedProduct);
    }

    @Override
    public void updateStock(Long productId, Integer quantity) {
        Product product = findProductById(productId);
        int newStock = product.getStockQuantity() + quantity;
        if (newStock < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot be negative. Current stock: " + product.getStockQuantity());
        }
        product.setStockQuantity(newStock);
        productRepository.save(product);
    }

    @Override
    public List<ProductResponseDTO> getLowStockProducts(Integer threshold) {
        List<Product> products = productRepository.findLowStockProducts(threshold);
        List<ProductResponseDTO> responseList = new ArrayList<>();
        for (Product product : products) {
            responseList.add(mapToResponseDTO(product));
        }
        return responseList;
    }

    @Override
    public boolean existsBySku(String sku) {
        return productRepository.existsBySku(sku);
    }

    // Helper: find product by ID
    private Product findProductById(Long id) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            return productOptional.get();
        } else {
            throw new ResourceNotFoundException("Product", "id", id);
        }
    }

    // Helper: create Pageable
    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        Sort sort;
        if (sortDir.equalsIgnoreCase("desc")) {
            sort = Sort.by(sortBy).descending();
        } else {
            sort = Sort.by(sortBy).ascending();
        }
        return PageRequest.of(page, size, sort);
    }

    // Helper: map Product to ProductResponseDTO
    private ProductResponseDTO mapToResponseDTO(Product product) {
        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .category(product.getCategory())
                .brand(product.getBrand())
                .imageUrl(product.getImageUrl())
                .sku(product.getSku())
                .isAvailable(product.getIsAvailable())
                .inStock(product.getStockQuantity() > 0)
                .build();
    }

    // Helper: map Page to PageResponseDTO
    private PageResponseDTO<ProductResponseDTO> mapToPageResponse(Page<Product> productPage) {
        List<ProductResponseDTO> products = new ArrayList<>();
        for (Product product : productPage.getContent()) {
            products.add(mapToResponseDTO(product));
        }

        return PageResponseDTO.<ProductResponseDTO>builder()
                .content(products)
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .first(productPage.isFirst())
                .last(productPage.isLast())
                .hasNext(productPage.hasNext())
                .hasPrevious(productPage.hasPrevious())
                .build();
    }
}
