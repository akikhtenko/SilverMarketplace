## Running the board

The best way to try all relevant scenarios is by running the blackbox test called ```OrderBoardSystemTest.java```.

## Design decisions

The implemented solution is largely driven by assumptions that

    * The number of orders is relatively high in general and for every price level in particular
    * The board is heavy on reads meaning readers view orders summary often and potentially concurrently
    * The number of distinct price levels is negligible in comparison to the number of live orders

To avoid re-aggregating all orders on every read causing excessive stress on infrastructure and making readers wait prolonged times I decided to aggregate orders at write time.
Above assumptions 'could' necessitate a NoSql database engine as an underlying order storage mechanism. In presence of which it is highly unlikely we can count on transactions that cross boundaries of a single aggregate. To avoid data losses at crash times I opted for event driven solution (namely event sourcing + SQRS).

## Disaster recovery

A recovery mechanism has not been built into the solution since this is a test exercise.

To have it implemented though the event store would maintain a sequence number that every event is assigned before being published to interested subscribers. Event subscribers would maintain and track the last processed/applied sequence number.
Then on system start or in an event of manual recovery all events from event store which have sequence number higher than the last processed by a given subscriber would be replied.