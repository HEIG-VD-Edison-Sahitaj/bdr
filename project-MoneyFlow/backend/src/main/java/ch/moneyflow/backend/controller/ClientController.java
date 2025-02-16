package ch.moneyflow.backend.controller;

import ch.moneyflow.backend.entity.Client;
import ch.moneyflow.backend.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String mail, @RequestParam String password) {
        Optional<Client> client = clientService.authenticate(mail, password);
        return client.map(value -> ResponseEntity.ok("User authenticated successfully")).orElseGet(() -> ResponseEntity.status(401).body("Invalid credentials"));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Client client) {
        try {
            clientService.register(client);
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}
