package martinromero.actividad3.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import martinromero.actividad3.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
}