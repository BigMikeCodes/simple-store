package dev.michaelfarrant.simplestore.cqrs;

public interface QueryHandler <TQuery extends Query, TResult> {

    public TResult handleQuery(TQuery query);

}
