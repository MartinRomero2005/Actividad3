package martinromero.actividad3;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import martinromero.actividad3.Repository.CategoriaRepository;
import martinromero.actividad3.Repository.RecetaRepository;
import martinromero.actividad3.model.Categoria;
import martinromero.actividad3.model.Receta;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class Actividad3ApplicationTests {

	@Autowired
	private CategoriaRepository categoriaRepository;

	@Autowired
	private RecetaRepository recetaRepository;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void recetaSeGuardaRelacionadaConUnaCategoria() {
		Categoria categoria = categoriaRepository.save(new Categoria("Postres"));
		Receta receta = recetaRepository.save(new Receta("Flan", "Flan casero", categoria, 60));

		Receta recetaGuardada = recetaRepository.findById(receta.getId()).orElseThrow();
		assertEquals("Postres", recetaGuardada.getCategoria().getNombre());
	}

}
