# Concurrency Topics
## Promise
- Write-Side -> someone will produce a value or error in the future

## Future
- Read-Side -> someone waits or listens for that value
- Reference to the result of an asynchronous operation in Java
- Limitations:
    - Futures cannot be completed manually (i.e. we have a task that returns a future that fetches data from a remote server if the service is down we can't use the latest cached data available to complete the future manually)
    - Actions cannot be performed until the result is available & we are not notified of completion (only a blocking get())
    - We can't attach a callback function to the future and have it called automatically when the future's result is available
    - Multiple futures can't be chained together (i.e. sometimes we need to execute a long-running task and when it's done, send its result to another long-running task, etc.) 
    - Multiple futures can't be combined together (if we have 10 different futures we want to run in parallel and then run some function once all of them complete, we can't do that with the Future class)
    - No exception handling in the Future API

## CompletableFuture
- Overcomes all the previous limitations of the Future class
- Provides a huge set of methods for creating, chaining, and combining multiple Futures w/ very comprehensive exception handling
```
runAsync = creates a completablefuture (starts a brand new async task) which has some Void result. Used for async action, like writing an audit log to a table, publishing message to kafka, sending an email, etc.

supplyAsync = creates a completablefuture which has some return value that can be passed down to chaining methods. Used for async computation, like reading from a db, calling REST service, reading file, resizing an image, running expensive algo, etc.

thenApply = analogous to Stream.map() applies some method to the result of a completablefuture, outputting new completablefuture

thenAccept = analogous to Stream.foreach() consumes the result of a completablefuture, not passing down any new value

thenRun = does not have access to the previous result and returns Void completablefuture (not passing down any new value)

thenCompose = analogous to Stream.flatmap() – flattens nested CompletableFuture results

allOf = all futures must be completed

anyOf = executes when any of the futures have been completed

get = blocking call to retrieve result of future once completed, throws interrupted exception & execution exception

join = blocking call to retrieve result of future once completed, does not throw checked exceptions
```

## Executor
- Interface which accepts runnable (void method) or callable (returns a value)

## Thread Pool
- Implements executor interface
> ExecutorService executor = Executors.newFixedThreadPool(# threads)

## Callback Functions
- Function passed as an argument to another function and executed when that function completes or some event happens