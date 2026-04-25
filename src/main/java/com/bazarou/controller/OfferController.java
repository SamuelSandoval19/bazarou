package com.bazarou.controller;

import com.bazarou.model.Product;
import com.bazarou.service.OfferService;
import com.bazarou.service.ProductService;
import com.bazarou.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/ofertas")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;
    private final ProductService productService;
    private final UserService userService;

    @PostMapping("/nueva")
    public String hacer(@RequestParam Long productoId,
                        @RequestParam BigDecimal monto,
                        @RequestParam(required = false) String mensaje,
                        RedirectAttributes ra) {
        try {
            Product p = productService.porId(productoId);
            offerService.hacerOferta(p, userService.getUsuarioActual(), monto, mensaje);
            ra.addFlashAttribute("success", "¡Tu oferta fue enviada! El vendedor te responderá pronto.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/producto/" + productoId;
    }

    @GetMapping("/recibidas")
    public String recibidas(Model model) {
        model.addAttribute("ofertas", offerService.ofertasRecibidas(userService.getUsuarioActual()));
        return "offers/recibidas";
    }

    @GetMapping("/enviadas")
    public String enviadas(Model model) {
        model.addAttribute("ofertas", offerService.ofertasEnviadas(userService.getUsuarioActual()));
        return "offers/enviadas";
    }

    @PostMapping("/{id}/aceptar")
    public String aceptar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            offerService.aceptar(id, userService.getUsuarioActual());
            ra.addFlashAttribute("success", "¡Oferta aceptada! El comprador puede continuar con la compra.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/ofertas/recibidas";
    }

    @PostMapping("/{id}/rechazar")
    public String rechazar(@PathVariable Long id,
                           @RequestParam(required = false) String mensaje,
                           RedirectAttributes ra) {
        try {
            offerService.rechazar(id, userService.getUsuarioActual(), mensaje);
            ra.addFlashAttribute("success", "Oferta rechazada.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/ofertas/recibidas";
    }

    @PostMapping("/{id}/contraoferta")
    public String contraoferta(@PathVariable Long id,
                               @RequestParam BigDecimal monto,
                               @RequestParam(required = false) String mensaje,
                               RedirectAttributes ra) {
        try {
            offerService.contraofertar(id, userService.getUsuarioActual(), monto, mensaje);
            ra.addFlashAttribute("success", "¡Contraoferta enviada al comprador!");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/ofertas/recibidas";
    }
}
