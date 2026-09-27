package martinromero.actividad3.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Service
public class RecetaExternaService {

    private static final Logger logger = LoggerFactory.getLogger(RecetaExternaService.class);

    private final RestClient restClient;

    public RecetaExternaService(RestClient mealDbRestClient) {
        this.restClient = mealDbRestClient;
    }

    public List<RecetaExterna> buscar(String nombre) {
        try {
            RespuestaMealDb respuesta = restClient.get()
                    .uri("/search.php?s={nombre}", nombre)
                    .retrieve()
                    .body(RespuestaMealDb.class);

            if (respuesta == null || respuesta.meals() == null) {
                return List.of();
            }

            return respuesta.meals().stream()
                    .map(comida -> new RecetaExterna(
                            comida.idMeal(),
                            comida.strMeal(),
                            comida.strCategory(),
                            comida.strArea(),
                            comida.strInstructions(),
                            comida.strMealThumb()))
                    .toList();
        } catch (RestClientException exception) {
            logger.warn("No se pudo consultar TheMealDB para el término solicitado.", exception);
            throw exception;
        }
    }

    public record RecetaExterna(
            String id,
            String nombre,
            String categoria,
            String area,
            String instrucciones,
            String imagen) {
    }

    public record RespuestaMealDb(List<ComidaMealDb> meals) {
    }

    public record ComidaMealDb(
            String idMeal,
            String strMeal,
            String strCategory,
            String strArea,
            String strInstructions,
            String strMealThumb) {
    }
}