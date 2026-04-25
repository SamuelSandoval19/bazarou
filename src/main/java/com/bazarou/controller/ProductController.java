package com.bazarou.controller;

import com.bazarou.dto.ProductDTO;
import com.bazarou.model.Product;
import com.bazarou.model.User;
import com.bazarou.repository.CategoryRepository;
import com.bazarou.service.FavoriteService;
import com.bazarou.service.OfferService;
import com.bazarou.service.ProductService;
import com.bazarou.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;
    private final UserService userService;
    private final OfferService offerService;
    private final FavoriteService favoriteService;

    @GetMapping("/buscar")
    public String buscar(@RequestParam(required = false, defaultValue = "") String q,
                         @RequestParam(required = false, defaultValue = "0") int page,
                         @RequestParam(required = false, defaultValue = "recientes") String sort,
                         Model model) {
        var resultado = q.isBlank()
                ? productService.listarDisponibles(page, 24, sort)
                : productService.buscar(q, page, 24);
        model.addAttribute("productos", resultado);
        model.addAttribute("q", q);
        model.addAttribute("sort", sort);
        model.addAttribute("categorias", categoryRepository.findAll());
        return "products/list";
    }

    @GetMapping("/categoria/{slug}")
    public String porCategoria(@PathVariable String slug,
                               @RequestParam(required = false, defaultValue = "0") int page,
                               Model model) {
        var cat = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        model.addAttribute("productos", productService.porCategoria(cat, page, 24));
        model.addAttribute("categoria", cat);
        model.addAttribute("categorias", categoryRepository.findAll());
        return "products/list";
    }

    @GetMapping("/producto/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Product p = productService.porId(id);
        productService.incrementarVistas(p);
        User actual = userService.getUsuarioActual();
        model.addAttribute("producto", p);
        model.addAttribute("ofertas", offerService.ofertasDeProducto(p));
        model.addAttribute("esFavorito", favoriteService.esFavorito(actual, p));
        model.addAttribute("esPropio",
                actual != null && actual.getId().equals(p.getVendedor().getId()));
        return "products/detail";
    }

    @GetMapping("/vender")
    public String formNuevo(Model model) {
        if (!model.containsAttribute("producto")) {
            model.addAttribute("producto", new ProductDTO());
        }
        model.addAttribute("categorias", categoryRepository.findAll());
        model.addAttribute("estados", Arrays.asList(Product.Estado.values()));
        return "products/new";
    }

    @PostMapping("/vender")
    public String publicar(@Valid @ModelAttribute("producto") ProductDTO dto,
                           BindingResult result,
                           @RequestParam(value = "imagenes", required = false) List<MultipartFile> imagenes,
                           Model model,
                           RedirectAttributes ra) throws Exception {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoryRepository.findAll());
            model.addAttribute("estados", Arrays.asList(Product.Estado.values()));
            return "products/new";
        }
        if (imagenes == null || imagenes.isEmpty() || imagenes.stream().allMatch(MultipartFile::isEmpty)) {
            result.reject("imagenes", "Sube al menos 1 foto de tu producto");
            model.addAttribute("categorias", categoryRepository.findAll());
            model.addAttribute("estados", Arrays.asList(Product.Estado.values()));
            return "products/new";
        }
        User vendedor = userService.getUsuarioActual();
        Product p = productService.crear(dto, imagenes, vendedor);
        ra.addFlashAttribute("success", "¡Tu producto ya está en el tianguis! 🌶️");
        return "redirect:/producto/" + p.getId();
    }
}
