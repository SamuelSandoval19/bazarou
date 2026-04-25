package com.bazarou.controller;

import com.bazarou.dto.RegistroDTO;
import com.bazarou.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/registro")
    public String registroPage(Model model) {
        if (!model.containsAttribute("registro")) {
            model.addAttribute("registro", new RegistroDTO());
        }
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrar(@Valid @ModelAttribute("registro") RegistroDTO dto,
                            BindingResult result,
                            Model model,
                            RedirectAttributes ra) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "match", "Las contraseñas no coinciden");
        }
        if (result.hasErrors()) {
            return "auth/registro";
        }
        try {
            userService.registrar(dto);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/registro";
        }
        ra.addFlashAttribute("success", "¡Bienvenido a Bazarou! Ya puedes iniciar sesión.");
        return "redirect:/login";
    }
}
