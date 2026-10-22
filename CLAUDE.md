# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

Spring Boot 4 (Web MVC, HATEOAS, Data Redis, JDBC, Validation) on Java 25 (`.sdkmanrc`: `java=25-tem`), built with the Maven wrapper. Virtual threads are enabled. Backing services: OpenSearch (product search), Valkey/Redis (carts), and PostgreSQL (catalogue management, accessed via `JdbcClient` — no JPA).

## Commands

```bash
docker compose up -d                 # OpenSearch :9200 (security disabled), Valkey :6379, Postgres :5432, then runs Flyway migrations
docker compose run --rm flyway migrate   # re-apply migrations after adding one
cd iac && terraform init && terraform apply   # create the product-v1 index + aliases in local OpenSearch
./mvnw spring-boot:run               # run the app on :8080
./mvnw test                          # run tests
./mvnw test -Dtest=SimplestoreApplicationTests#contextLoads   # single test
./mvnw package                       # build jar
```

`SimplestoreApplicationTests` is a `@SpringBootTest` context-load test, so it needs the backing services reachable. `ListingTest` is a plain unit test of the listing lifecycle rules.

## Architecture

Single Spring Boot app under `dev.michaelfarrant.simplestore`, organised by bounded context, then by feature (vertical slices), with a lightweight hand-rolled CQRS style:

- `cqrs/` — marker interfaces `Command`, `Query`, and `CommandHandler<C, R>.handleCommand` / `QueryHandler<Q, R>.handleQuery`. There is no bus/dispatcher; controllers inject concrete handler beans directly. Older handlers (cart, storefront catalogue) don't yet implement these interfaces; catalogue management does. Command handlers with no result use `Void` and return `null`.
- `storefront/` — customer-facing read side.
  - `catalogue/` — `/api/products` (read-only): get-by-id, search, search-suggestions. Backed by OpenSearch via `OpenSearchClient`; documents are `ProductDocument`. Each operation lives in its own sub-package (`byid`, `searchproducts`, `searchsuggestion`) with a query + handler.
  - `cart/` — `/api/cart`. Carts are Redis hashes: key `cart:<userId>`, hash field = product id, value = quantity (see `RedisBeanConfig`). User id is currently hard-coded to the nil UUID in `CartRestController`.
- `cataloguemgmt/` — back-office catalogue management under `/api/catalogue-management`. Domain (package root): an `Item` is a sellable thing; a `Listing` aggregate is composed of `ListingItem`s (item + quantity) with a `ListingStatus` lifecycle DRAFT → ACTIVE (published) → WITHDRAWN (terminal; drafts can also be withdrawn). Items can only change while DRAFT; once ACTIVE only title/description corrections are allowed. Rule violations throw `ListingStateException`.
  - `features/<name>/` holds the command/query + handler, with a `rest/` sub-package for the controller and request DTO. Controllers extend `CatalogueManagementBaseController`; `rest/CatalogueManagementExceptionHandler` maps domain exceptions to `ProblemDetail` responses (404/409/422).
  - Write side: command handlers load/save aggregates through `db/ItemRepository` and `db/ListingRepository` (JDBC, optimistic locking via `listing.version`). Read side: query handlers query projections directly with `JdbcClient`.
  - `events/` — integration events (`ListingPublished`, `ListingUpdated`, `ListingWithdrawn`) sent through the `ListingEventPublisher` abstraction. No broker or outbox yet: the impl publishes Spring application events, delivered `@Async` after commit to `LoggingListingEventListener` (a placeholder). The storefront is meant to consume these eventually; there is no consumer yet.
- `config/` — `OpenSearchClientBeanConfig` (Apache HttpClient 5 transport, properties bound from `opensearch.*` via `OpenSearchConfiguration`), `Indexes` constants, and `RedisBeanConfig`.

### OpenSearch index

The index is managed by Terraform in `iac/main.tf`, not by the app. The physical index is `product-v1` with aliases `products` (write index, used by the app via `Indexes.PRODUCTS`) and `storefront-ro` (read-only). `name` is a `search_as_you_type` field (with `name.raw` keyword), which is why search queries target `name._2gram` / `name._3gram`. Mapping changes should go through a new versioned index in Terraform.

### Database migrations

Postgres schema lives in `migrations/` as Flyway SQL files, run by the `flyway` docker-compose service (Flyway is deliberately not an app dependency). Name files with a timestamp version, e.g. `V202610221514__create-item.sql`, and keep them idempotent (`CREATE ... IF NOT EXISTS`, no `CREATE TYPE`).
