package ch.moneyflow.backend.service;

import ch.moneyflow.backend.entity.Client;
import ch.moneyflow.backend.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public ClientService(ClientRepository clientRepository, BCryptPasswordEncoder passwordEncoder) {
        this.clientRepository = clientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Authenticate client
    public Optional<Client> authenticate(String email, String password) {
        Optional<Client> optionalClient = clientRepository.findByMail(email);
        if (optionalClient.isPresent()) {
            Client client = optionalClient.get();
            if (passwordEncoder.matches(password, client.getPasswordHash())) {
                return Optional.of(client);
            }
        }
        return Optional.empty();
    }

    // Register client
    public void register(Client client) {
        if (clientRepository.existsByMail(client.getMail(), client.getPseudo())) {
            throw new IllegalArgumentException("Email or pseudo already exists");
        }
        String hashedPassword = passwordEncoder.encode(client.getPasswordHash());
        client.setPasswordHash(hashedPassword);
        clientRepository.register(client.getMail(), client.getPseudo(), client.getPasswordHash());
    }
}