package com.bazarou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 🌶️ BAZAROU - El tianguis digital de México
 *
 * Compra, vende y regatea como en el mercado de toda la vida,
 * pero con la seguridad de una plataforma moderna.
 */
@SpringBootApplication
public class BazarouApplication {

    public static void main(String[] args) {
        SpringApplication.run(BazarouApplication.class, args);
        System.out.println("""

                 ╔══════════════════════════════════════════╗
                 ║   🌶️  BAZAROU está corriendo en :8080  🌶️   ║
                 ║   El tianguis digital de México              ║
                 ╚══════════════════════════════════════════╝
                """);
    }
}
