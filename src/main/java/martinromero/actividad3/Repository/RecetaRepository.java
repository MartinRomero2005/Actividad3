package martinromero.actividad3.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import martinromero.actividad3.model.Receta;

public interface RecetaRepository extends JpaRepository<Receta, Long> {

    List<Receta> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByCategoriaId(Long categoriaId);

}