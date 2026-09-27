package martinromero.actividad3.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import martinromero.actividad3.Repository.CategoriaRepository;
import martinromero.actividad3.Repository.RecetaRepository;
import martinromero.actividad3.model.Categoria;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;
    private final RecetaRepository recetaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository, RecetaRepository recetaRepository) {
        this.categoriaRepository = categoriaRepository;
        this.recetaRepository = recetaRepository;
    }

    @GetMapping
    public List<Categoria> obtenerCategorias() {
        return categoriaRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Categoria> crearCategoria(@RequestBody Categoria categoria) {
        categoria.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoriaRepository.save(categoria));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(
            @PathVariable Long id,
            @RequestBody Categoria categoria) {
        return categoriaRepository.findById(id)
                .map(existente -> {
                    existente.setNombre(categoria.getNombre());
                    return ResponseEntity.ok(categoriaRepository.save(existente));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        if (!categoriaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        if (recetaRepository.existsByCategoriaId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}