package dev.michaelfarrant.simplestore.command.create;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CreateProductCommandHandler {

    public UUID handleCommand(CreateProductCommand command) {
        return UUID.randomUUID();
    }

}
