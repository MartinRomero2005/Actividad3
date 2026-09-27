package martinromero.actividad3.controller;

import java.util.List;
import java.util.Map;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import martinromero.actividad3.Repository.CategoriaRepository;
import martinromero.actividad3.model.Categoria;
import martinromero.actividad3.service.RecetaExternaService;
import martinromero.actividad3.service.RecetaExternaService.RecetaExterna;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import martinromero.actividad3.Repository.RecetaRepository;
import martinromero.actividad3.model.Receta;

@RestController
@RequestMapping("/recetas")
public class RecetaController {

    private final RecetaRepository recetaRepository;
    private final CategoriaRepository categoriaRepository;
    private final RecetaExternaService recetaExternaService;
    private final Counter busquedasCounter;

    public RecetaController(
            RecetaRepository recetaRepository,
            CategoriaRepository categoriaRepository,
            RecetaExternaService recetaExternaService,
            MeterRegistry meterRegistry) {
        this.recetaRepository = recetaRepository;
        this.categoriaRepository = categoriaRepository;
        this.recetaExternaService = recetaExternaService;
        this.busquedasCounter = meterRegistry.counter("recetas.busquedas");
    }

    @GetMapping
    public ResponseEntity<List<Receta>> obtenerRecetas() {
        return ResponseEntity.ok(recetaRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Receta> obtenerReceta(@PathVariable Long id) {
        return recetaRepository.findById(id)
                .map(receta -> ResponseEntity.ok(receta))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Receta>> buscarPorNombre(
            @RequestParam String nombre) {

        busquedasCounter.increment();
        List<Receta> recetas =
                recetaRepository.findByNombreContainingIgnoreCase(nombre);

        return ResponseEntity.ok(recetas);
    }

    @PostMapping
    public ResponseEntity<Receta> crearReceta(@RequestBody Receta receta) {

        if (receta.getCategoria() == null || receta.getCategoria().getId() == null) {
            return ResponseEntity.badRequest().build();
        }
        Categoria categoria = categoriaRepository.findById(receta.getCategoria().getId()).orElse(null);
        if (categoria == null) {
            return ResponseEntity.badRequest().build();
        }
        receta.setCategoria(categoria);
        Receta nuevaReceta = recetaRepository.save(receta);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(nuevaReceta);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Receta> actualizarReceta(
            @PathVariable Long id,
            @RequestBody Receta receta) {

        Receta recetaExistente =
                recetaRepository.findById(id).orElse(null);

        if (recetaExistente == null) {
            return ResponseEntity.notFound().build();
        }

        recetaExistente.setNombre(receta.getNombre());
        recetaExistente.setDescripcion(receta.getDescripcion());
        if (receta.getCategoria() != null && receta.getCategoria().getId() != null) {
            Categoria categoria = categoriaRepository.findById(receta.getCategoria().getId()).orElse(null);
            if (categoria == null) {
                return ResponseEntity.badRequest().build();
            }
            recetaExistente.setCategoria(categoria);
        }
        recetaExistente.setTiempoPreparacion(
                receta.getTiempoPreparacion()
        );

        Receta recetaActualizada =
                recetaRepository.save(recetaExistente);

        return ResponseEntity.ok(recetaActualizada);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReceta(@PathVariable Long id) {

        if (!recetaRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        recetaRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/externas")
    public ResponseEntity<?> buscarRecetasExternas(@RequestParam String nombre) {
        try {
            List<RecetaExterna> recetas = recetaExternaService.buscar(nombre);
            return ResponseEntity.ok(recetas);
        } catch (RestClientException exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("mensaje", "No fue posible consultar el servicio externo."));
        }
    }
}