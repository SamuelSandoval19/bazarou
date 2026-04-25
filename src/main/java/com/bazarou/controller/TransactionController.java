package com.bazarou.controller;

import com.bazarou.model.Product;
import com.bazarou.service.ProductService;
import com.bazarou.service.TransactionService;
import com.bazarou.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/compra")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final ProductService productService;
    private final UserService userService;

    @GetMapping("/checkout/{productoId}")
    public String checkout(@PathVariable Long productoId, Model model) {
        Product p = productService.porId(productoId);
        model.addAttribute("producto", p);
        // Costo de envío estimado simulado (en producción se calcularía con la API de paquetería)
        model.addAttribute("costoEnvio", new BigDecimal("99.00"));
        model.addAttribute("comision", p.getPrecio().multiply(new BigDecimal("0.05")));
        return "transactions/checkout";
    }

    @PostMapping("/confirmar")
    public String confirmar(@RequestParam Long productoId,
                            @RequestParam BigDecimal monto,
                            @RequestParam String direccion,
                            @RequestParam(defaultValue = "99.00") BigDecimal costoEnvio,
                            RedirectAttributes ra) {
        try {
            Product p = productService.porId(productoId);
            var t = transactionService.comprar(p, userService.getUsuarioActual(), monto, direccion, costoEnvio);
            ra.addFlashAttribute("success", "¡Compra realizada! Código: " + t.getCodigoSeguimiento() +
                    ". Tu pago está protegido con Bazarou.");
            return "redirect:/compra/mis-compras";
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
            return "redirect:/producto/" + productoId;
        }
    }

    @GetMapping("/mis-compras")
    public String misCompras(Model model) {
        model.addAttribute("transacciones",
                transactionService.compras(userService.getUsuarioActual()));
        return "transactions/mis-compras";
    }

    @GetMapping("/mis-ventas")
    public String misVentas(Model model) {
        model.addAttribute("transacciones",
                transactionService.ventas(userService.getUsuarioActual()));
        return "transactions/mis-ventas";
    }

    @PostMapping("/{id}/enviar")
    public String marcarEnviado(@PathVariable Long id,
                                @RequestParam String paqueteria,
                                @RequestParam String guia,
                                RedirectAttributes ra) {
        try {
            transactionService.marcarEnviado(id, userService.getUsuarioActual(), paqueteria, guia);
            ra.addFlashAttribute("success", "¡Marcado como enviado! El comprador recibirá el aviso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/compra/mis-ventas";
    }

    @PostMapping("/{id}/confirmar-recepcion")
    public String confirmarRecepcion(@PathVariable Long id, RedirectAttributes ra) {
        try {
            transactionService.confirmarRecepcion(id, userService.getUsuarioActual());
            ra.addFlashAttribute("success", "¡Recepción confirmada! El pago se libera al vendedor.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/compra/mis-compras";
    }

    @PostMapping("/{id}/reportar")
    public String reportar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            transactionService.reportarProblema(id, userService.getUsuarioActual());
            ra.addFlashAttribute("success", "Reporte enviado. El equipo de Bazarou revisará tu caso.");
        } catch (RuntimeException ex) {
            ra.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/compra/mis-compras";
    }
}
