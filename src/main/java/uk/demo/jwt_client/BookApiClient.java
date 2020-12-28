package uk.demo.jwt_client;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.stream.Stream;

@Controller
public class BookApiClient {

    private String jwt;
    private MultiValueMap<String, String> headers;

    public BookApiClient() {
        jwt = getJwtToken(true);
        headers = setStringStringMultiValueMap();
        String bookName = "Spring Batch in action";
        addBook(bookName);
        getBooks();
    }

    private void addBook(String bookToAdd) {

        HttpEntity httpEntity = new HttpEntity(bookToAdd, headers);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.exchange("http://localhost:8080/api/books",
                HttpMethod.POST,
                httpEntity,
                Void.class);
    }

    private MultiValueMap<String, String> setStringStringMultiValueMap() {
        MultiValueMap<String, String> headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + jwt);
        return headers;
    }

    private void getBooks() {

        HttpEntity httpEntity = new HttpEntity(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String[]> exchange = restTemplate.exchange("http://localhost:8080/api/books",
                HttpMethod.GET,
                httpEntity,
                String[].class);

        Stream.of(exchange.getBody()).forEach(System.out::println);
    }

    private String getJwtToken(boolean isAdmin) {

        Algorithm algorithm = Algorithm.RSA256(null, (RSAPrivateKey) getPrivateKey());
        return JWT.create()
                .withClaim("admin", isAdmin)
                .sign(algorithm);
    }

    private PrivateKey getPrivateKey() {

        String privateKey = "MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAw1BQvLKLOzk0II+e" +
                "J9wBLjV2IjehnNSvS2klTpMF3Dy1EC3eY3ZmxiQ8rrSaOMwOsXA5hdf290kecdIj" +
                "5u6TRQIDAQABAkEAkbeXNOFETWAlSvG7fmN+ofoS8/5rXfWz/uAojFHWenOlzUau" +
                "mAhdFjwNcIdc8qkfojrQzHd8wHTkWLqGK0bpwQIhAPENVbdlJd+npin70txczY+P" +
                "/TRUqhqj5VOGUjSKxXPRAiEAz2zkHzA/bBAlWbUntuKgnRJjmIH9pir41Vig2OJX" +
                "STUCIC8HPPvkvfjeimqSeNcJPAmQPAQjqHQ+GZWsFQmvMUqhAiEAwlqWeT68/mU2" +
                "ihK6zpsUwXg8h+atI2i6VTVBKVcTUE0CICDTNIzA8pGo6fkLN4K7zrF5KwptKPYA" +
                "NIQXPVtvmCuU";

        PKCS8EncodedKeySpec encodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
        try {
            return  KeyFactory.getInstance("RSA").generatePrivate(encodedKeySpec);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

}
