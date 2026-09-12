package com.copal.webdev2;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
public class ReportRunner implements CommandLineRunner {
 private final ProductService productService;

  public ReportRunner(ProductService productService) {
        this.productService = productService;
    }
    
     @Override
    public void run(String... args) throws Exception {
        double threshold = 5000.0;
        List<Product> expensiveProducts = productService.getProductsAbovePrice(threshold);

        System.out.println();
        System.out.println("================================");
        System.out.println("       PRODUCT REPORT");
        System.out.println("================================");
        System.out.println("Shop: " + productService.getShopName());
        System.out.println("Currency: " + productService.getShopCurrency());
        System.out.println();
        System.out.println("Products above " + productService.getShopCurrency() + " " + (int) threshold + ":");
        System.out.println();

        for (Product product : expensiveProducts) {
            System.out.println(product.getName() + " - " + productService.getShopCurrency() + " " + (int) product.getPrice());
        }

        System.out.println("================================");
        System.out.println();
    }
    
}