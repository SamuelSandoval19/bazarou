package com.bazarou.controller;

import com.bazarou.repository.CategoryRepository;
import com.bazarou.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("recientes", productService.recientes(8));
        model.addAttribute("populares", productService.populares(4));
        model.addAttribute("categorias", categoryRepository.findAll());
        return "index";
    }

    @GetMapping("/sobre-nosotros")
    public String sobreNosotros() {
        return "sobre-nosotros";
    }

    @GetMapping("/como-funciona")
    public String comoFunciona() {
        return "como-funciona";
    }
}
