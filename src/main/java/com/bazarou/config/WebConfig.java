package com.bazarou.config;

import com.bazarou.model.User;
import com.bazarou.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.nio.file.Paths;

/**
 * - Sirve la carpeta /uploads como recurso estático
 * - Inyecta el usuario logueado en todos los modelos de Thymeleaf
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${bazarou.uploads.dir}")
    private String uploadsDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String absoluteUploadPath = Paths.get(uploadsDir).toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(absoluteUploadPath);
    }

    @ControllerAdvice
    public static class GlobalModelAttributes {

        private final UserRepository userRepository;

        public GlobalModelAttributes(UserRepository userRepository) {
            this.userRepository = userRepository;
        }

        @ModelAttribute("currentUser")
        public User currentUser() {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
                return null;
            }
            return userRepository.findByUsername(auth.getName()).orElse(null);
        }
    }
}
