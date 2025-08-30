package com.example.java_aes_gcm_api;

import com.example.java_aes_gcm_api.dto.DecryptRequest;
import com.example.java_aes_gcm_api.dto.EncryptRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
public class CryptoController {

    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @PostMapping("/encrypt")
    @Operation(summary = "Encrypt plain text using AES GCM",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EncryptRequest.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"plainText\":\"Hello World\",\"key\":\"MTIzNDU2Nzg5MDEyMzQ1Ng==\",\"charset\":\"UTF-8\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Encryption successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    public ResponseEntity<String> encrypt(@RequestBody Map<String, String> payload) {
        try {
            String plainText = payload.get("plainText");
            String key = payload.get("key");
            String charsetName = payload.getOrDefault("charset", StandardCharsets.UTF_8.name());

            if (plainText == null || key == null) {
                throw new IllegalArgumentException("'plainText' and 'key' are required.");
            }

            Charset charset = Charset.forName(charsetName);
            String encryptedText = cryptoService.encrypt(plainText, key, charset);
            return ResponseEntity.ok(encryptedText);
        } catch (IllegalArgumentException e) {
            // 使用 400 Bad Request
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // 使用 500 Internal Server Error
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Encryption failed.", e);
        }
    }

    @PostMapping("/decrypt")
    @Operation(summary = "Decrypt hex encoded cipher text using AES GCM",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DecryptRequest.class),
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    value = "{\"cipherText\":\"...hex_ciphertext...\",\"key\":\"MTIzNDU2Nzg5MDEyMzQ1Ng==\",\"charset\":\"UTF-8\"}"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Decryption successful"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized or decryption failure")
            }
    )
    public ResponseEntity<String> decrypt(@RequestBody Map<String, String> payload) {
        try {
            String cipherText = payload.get("cipherText");
            String key = payload.get("key");
            String charsetName = payload.getOrDefault("charset", StandardCharsets.UTF_8.name());

            if (cipherText == null || key == null) {
                throw new IllegalArgumentException("'cipherText' and 'key' are required.");
            }

            Charset charset = Charset.forName(charsetName);
            String decryptedText = cryptoService.decrypt(cipherText, key, charset);
            return ResponseEntity.ok(decryptedText);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            // 使用 401 Unauthorized (或 400 Bad Request)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Decryption failed. Check key or ciphertext.", e);
        }
    }
}