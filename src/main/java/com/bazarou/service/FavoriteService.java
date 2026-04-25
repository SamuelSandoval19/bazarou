package com.bazarou.service;

import com.bazarou.model.Favorite;
import com.bazarou.model.Product;
import com.bazarou.model.User;
import com.bazarou.repository.FavoriteRepository;
import com.bazarou.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final ProductRepository productRepository;

    @Transactional
    public boolean toggle(User u, Product p) {
        var existente = favoriteRepository.findByUsuarioAndProducto(u, p);
        if (existente.isPresent()) {
            favoriteRepository.delete(existente.get());
            p.setFavoritos(Math.max(0, p.getFavoritos() - 1));
            productRepository.save(p);
            return false;
        }
        favoriteRepository.save(Favorite.builder().usuario(u).producto(p).build());
        p.setFavoritos(p.getFavoritos() + 1);
        productRepository.save(p);
        return true;
    }

    public boolean esFavorito(User u, Product p) {
        if (u == null) return false;
        return favoriteRepository.existsByUsuarioAndProducto(u, p);
    }

    public List<Favorite> deUsuario(User u) {
        return favoriteRepository.findByUsuarioOrderByFechaAgregadoDesc(u);
    }
}
