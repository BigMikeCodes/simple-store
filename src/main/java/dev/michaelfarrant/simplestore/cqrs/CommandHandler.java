package dev.michaelfarrant.simplestore.cqrs;

public interface CommandHandler<TCommand extends Command, TResult> {

    TResult handleCommand(TCommand command);

}
