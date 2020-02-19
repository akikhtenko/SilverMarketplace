## Running the board

The best way to try all relevant scenarios is by running the blackbox test class called ```OrderBoardSystemTest```.

## Design decisions

The implemented solution is largely driven by assumptions that

    * The number of orders is huge in general and for every price level in particular
    * The board is heavy on reads meaning readers view orders summary often and potentially concurrently
    * The number of distinct price levels is negligible in comparison to the number of live orders

To avoid re-aggregating all orders on every read causing excessive stress on infrastructure and making readers wait prolonged times I decided to aggregate orders at write time.

Above assumptions 'could' necessitate a NoSql database engine as an underlying order storage mechanism. In presence of which it is highly unlikely we can count on transactions that cross boundaries of a single aggregate (i.e. transactionality of updates to an order and its corresponding price level summary). To avoid data losses at crash times I opted for event driven solution (namely event sourcing + SQRS) where each external user write is registered as an event in ```EventStore```.

```OrderEventListener``` that deals with price level aggregations is one of possibly many subscribers interested in order events and it's registered as such with ```EventBus``` in ```OrderBoardConfiguration```. Events are getting published in ```ObservableEventStore``` on every successful event save.

The best way to start exploring the solution code in general is ```OrderBoardResource``` class which is the entry point for all user interactions from the UI.

## Disaster recovery

A recovery mechanism has not been built into the solution since this is a test exercise.

To have it implemented though the event store would maintain a sequence number that every event is assigned before being published to interested subscribers. Event subscribers would maintain and track the last processed/applied sequence number.
Then on system start or in an event of manual recovery all events from event store which have sequence number higher than the last processed by a given subscriber would be replied.