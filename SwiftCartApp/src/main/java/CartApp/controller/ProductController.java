package CartApp.controller;

import CartApp.dto.request.ProductRequestDTO;
import CartApp.dto.response.ApiResponseDTO;
import CartApp.dto.response.PageResponseDTO;
import CartApp.dto.response.ProductResponseDTO;
import CartApp.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    /**
     * Create a new product
     * POST /api/products
     */
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> createProduct(
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO createdProduct = productService.createProduct(productRequestDTO);
        return new ResponseEntity<>(
                ApiResponseDTO.success("Product created successfully", createdProduct),
                HttpStatus.CREATED);
    }

    /**
     * Get product by ID
     * GET /api/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponseDTO.success(product));
    }

    /**
     * Get product by SKU
     * GET /api/products/sku/{sku}
     */
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> getProductBySku(@PathVariable String sku) {
        ProductResponseDTO product = productService.getProductBySku(sku);
        return ResponseEntity.ok(ApiResponseDTO.success(product));
    }

    /**
     * Get all products (non-paginated)
     * GET /api/products/all
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponseDTO.success("Fetched " + products.size() + " products", products));
    }

    /**
     * Get all products (paginated)
     * GET /api/products?page=0&size=10&sortBy=name&sortDir=asc
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<ProductResponseDTO>>> getAllProductsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        PageResponseDTO<ProductResponseDTO> products = productService.getAllProductsPaginated(page, size, sortBy,
                sortDir);
        return ResponseEntity.ok(ApiResponseDTO.success(products));
    }

    /**
     * Get available products (non-paginated)
     * GET /api/products/available
     */
    @GetMapping("/available")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getAvailableProducts() {
        List<ProductResponseDTO> products = productService.getAvailableProducts();
        return ResponseEntity.ok(ApiResponseDTO.success(products));
    }

    /**
     * Get products by category
     * GET /api/products/category/{category}
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getProductsByCategory(
            @PathVariable String category) {
        List<ProductResponseDTO> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(ApiResponseDTO.success(products));
    }

    /**
     * Get products by price range
     * GET /api/products/price-range?min=100&max=500
     */
    @GetMapping("/price-range")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getProductsByPriceRange(
            @RequestParam double min,
            @RequestParam double max) {
        List<ProductResponseDTO> products = productService.getProductsByPriceRange(min, max);
        return ResponseEntity.ok(ApiResponseDTO.success(products));
    }

    /**
     * Search products
     * GET /api/products/search?keyword=xyz&page=0&size=10
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<PageResponseDTO<ProductResponseDTO>>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponseDTO<ProductResponseDTO> products = productService.searchProducts(keyword, page, size);
        return ResponseEntity.ok(ApiResponseDTO.success(products));
    }

    /**
     * Update product
     * PUT /api/products/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductResponseDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO updatedProduct = productService.updateProduct(id, productRequestDTO);
        return ResponseEntity.ok(ApiResponseDTO.success("Product updated successfully", updatedProduct));
    }

    /**
     * Update product stock
     * PATCH /api/products/{id}/stock?quantity=10
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ApiResponseDTO<Void>> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        productService.updateStock(id, quantity);
        return ResponseEntity.ok(ApiResponseDTO.success("Stock updated successfully"));
    }

    /**
     * Get low stock products
     * GET /api/products/low-stock?threshold=10
     */
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponseDTO<List<ProductResponseDTO>>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold) {
        List<ProductResponseDTO> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(ApiResponseDTO.success(products));
    }

    /**
     * Check if SKU exists
     * GET /api/products/check-sku?sku=xyz
     */
    @GetMapping("/check-sku")
    public ResponseEntity<ApiResponseDTO<Boolean>> checkSkuExists(@RequestParam String sku) {
        boolean exists = productService.existsBySku(sku);
        return ResponseEntity.ok(ApiResponseDTO.success(
                exists ? "SKU already exists" : "SKU is available", exists));
    }
}
