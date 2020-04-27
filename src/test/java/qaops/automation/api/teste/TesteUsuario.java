package qaops.automation.api.teste;

import io.restassured.response.ResponseBodyExtractionOptions;
import org.apache.http.HttpStatus;
import org.junit.Test;
import qaops.automation.api.dominio.Usuario;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class TesteUsuario extends TesteBase {

    private static final String LISTA_USUARIOS_ENDPOINT = "/users";
    private static final String MOSTRA_USUARIO_ENDPOINT = "/users/{userId}";
    private static final String CRIAR_USUARIO_ENDPOINT = "/user";

    @Test
    public void testListUserMetadata() {
        given().
            params("page", "2").
        when().
            get(LISTA_USUARIOS_ENDPOINT).
        then().
            statusCode(HttpStatus.SC_OK).
            body("page", is(2)).
            body("data", is(notNullValue()));
    }

    @Test
    public void testSuccessfullyCreateaUser() {
        Map<String, String> usuario = new HashMap<>();
        usuario.put("name", "rafael");
        usuario.put("job", "eng test");

        given().
            body(usuario).
        when().
            post(CRIAR_USUARIO_ENDPOINT).
        then().
            statusCode(HttpStatus.SC_CREATED).
            body("name", is("rafael"));
    }

    @Test
    public void testePesquisaPorUsuario() {
        String user = "2";
        when().
            get("/users/" + user).
        then().
            statusCode(HttpStatus.SC_OK).
            body("data.email", containsString("@reqres.in"));
    }

    @Test
    public void testePesquisaPorUsuario2() {
        given().
            pathParam("userId", "2").
        when().
            get(MOSTRA_USUARIO_ENDPOINT).
        then().
            statusCode(HttpStatus.SC_OK).
            body("data.email", containsString("@reqres.in"));
    }

    @Test
    public void testePesquisaPorUsuarioEDeserializa() {
        Usuario usuario = given().
            pathParam("userId", "2").
        when().
            get(MOSTRA_USUARIO_ENDPOINT).
        then().
            statusCode(HttpStatus.SC_OK).
        extract().
            body().jsonPath().getObject("data", Usuario.class);

        assertThat(usuario.getEmail(), containsString("@reqres.in"));
        assertThat(usuario.getName(), is(("Janet")));
        assertThat(usuario.getLastName(), is(("Weaver")));
    }

    @Test
    public void testeListaMetadadosDoUsuario2() {
        int perPage = getUserPerPage();

        given().
            param("page", "2").
        when().
            get(LISTA_USUARIOS_ENDPOINT).
        then().
            statusCode(HttpStatus.SC_OK).
            body("page", is(2)).
            body("data.findAll { it.avatar.startsWith('https://s3.amazonaws.com')}.size()", is(perPage)).
            body("data.size()", is(perPage));
    }

    int getUserPerPage() {
        return given().
            param("page", "2").
        when().
            get(LISTA_USUARIOS_ENDPOINT).
        then().
            statusCode(HttpStatus.SC_OK).
            extract().
            path("per_page");
    }


}
