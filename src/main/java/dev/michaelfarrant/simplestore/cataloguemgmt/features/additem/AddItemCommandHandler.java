package dev.michaelfarrant.simplestore.cataloguemgmt.features.additem;

import dev.michaelfarrant.simplestore.cataloguemgmt.Item;
import dev.michaelfarrant.simplestore.cqrs.CommandHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AddItemCommandHandler implements CommandHandler<AddItemCommand, UUID> {

    public UUID handleCommand(AddItemCommand command){

        Item item = Item.newItem(command.title(), command.description());

        return item.id();
    }
}
