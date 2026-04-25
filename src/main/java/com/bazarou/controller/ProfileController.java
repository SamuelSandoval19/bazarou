package com.bazarou.controller;

import com.bazarou.model.User;
import com.bazarou.repository.UserRepository;
import com.bazarou.service.FavoriteService;
import com.bazarou.service.ProductService;
import com.bazarou.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final FavoriteService favoriteService;

    @GetMapping("/perfil")
    public String miPerfil(Model model) {
        User u = userService.getUsuarioActual();
        return "redirect:/perfil/" + u.getUsername();
    }

    @GetMapping("/perfil/{username}")
    public String perfilPublico(@PathVariable String username, Model model) {
        User u = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        model.addAttribute("perfil", u);
        model.addAttribute("productos", productService.delVendedor(u));
        return "user/perfil";
    }

    @GetMapping("/perfil/editar")
    public String editar(Model model) {
        model.addAttribute("usuario", userService.getUsuarioActual());
        return "user/editar";
    }

    @PostMapping("/perfil/editar")
    public String guardar(@ModelAttribute User datos, RedirectAttributes ra) {
        User actual = userService.getUsuarioActual();
        actual.setNombreCompleto(datos.getNombreCompleto());
        actual.setBio(datos.getBio());
        actual.setCiudad(datos.getCiudad());
        actual.setEstado(datos.getEstado());
        actual.setTelefono(datos.getTelefono());
        userService.actualizarPerfil(actual);
        ra.addFlashAttribute("success", "Perfil actualizado.");
        return "redirect:/perfil/" + actual.getUsername();
    }

    @GetMapping("/favoritos")
    public String misFavoritos(Model model) {
        model.addAttribute("favoritos", favoriteService.deUsuario(userService.getUsuarioActual()));
        return "user/favoritos";
    }

    @PostMapping("/favoritos/toggle/{productoId}")
    public String toggleFavorito(@PathVariable Long productoId,
                                 @RequestHeader(value = "Referer", required = false) String referer) {
        favoriteService.toggle(userService.getUsuarioActual(), productService.porId(productoId));
        return "redirect:" + (referer != null ? referer : "/");
    }
}
