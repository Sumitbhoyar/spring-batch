# spring-batch

##### Spring Batch Business Use Case
- At the end of a month when a company has to send salary to its employee’s respective accounts.
- Processing of salary slips at month end is when spring batch can be used.
- Sending out mass communication emails.
- For generating automated reports on daily, weekly or monthly basis.
- Executing business workflow automatically without human intervention.

##### Framework Architecture

Spring Batch exhibit a layered architecture and it comprises of three major high level components: 
Application, Core and Infrastructure.

- The **application** layer contains all the batch job configurations, custom codes for business logic and job meta information developed by Application developers.

- The Batch **Core** has the core runtime classes necessary to launch and control any batch job. Some of the core runtime classes include JobLauncher, Job, and Step implementations.

- The **infrastructure** contains API for common readers and writers, and services for retrying on failure, repeat jobs etc. The infrastructure layer are used both by application developers(ItemReader and ItemWriter) and the core framework itself for controlling the batch job such as Retry, repeat. Thus Batch Core and Application layers are built on top of Infrastructure layer.

##### How Spring Batch works
```
                               / ItemReader
JobLauncher -- Job -- Step --/---ItemProcessor
                             \__ItemWriter
                            
    Job      Repository
```
- **JobLauncher** - represents a simple interface for launching a Job with a given set of JobParameters.
- **StepExecution** - a StepExecution represents a single attempt to execute a step.
- **JobInstance** - A JobInstance represents the concept of a logical job run.
- **JobRepository** - The JobRepository is used for basic CRUD operations of the various persisted domain objects within Spring Batch, such as JobExecution and StepExecution. It is required by many of the major framework features, such as the JobLauncher, Job, and Step.
- **step** - A Step that delegates to a Job to do its work. This is a great tool for managing dependencies between jobs, and also to modularise complex step logic into something that is testable in isolation. The job is executed with parameters that can be extracted from the step execution, hence this step can also be usefully used as the worker in a parallel or partitioned execution.
- **ItemReader** - Strategy interface for providing the data. Implementations are expected to be stateful and will be called multiple times for each batch, with each call to read() returning a different value and finally returning null when all input data is exhausted. Implementations need not be thread-safe and clients of a ItemReader need to be aware that this is the case. A richer interface (e.g. with a look ahead or peek) is not feasible because we need to support transactions in an asynchronous batch.
- **ItemProcessor** - Interface for item transformation. Given an item as input, this interface provides an extension point which allows for the application of business logic in an item oriented processing scenario. It should be noted that while it's possible to return a different type than the one provided, it's not strictly necessary. Furthermore, returning null indicates that the item should not be continued to be processed.
- **ItemStreamWriter** - Basic interface for generic output operations. Class implementing this interface will be responsible for serializing objects as necessary. Generally, it is responsibility of implementing class to decide which technology to use for mapping and how it should be configured. The write method is responsible for making sure that any internal buffers are flushed. If a transaction is active it will also usually be necessary to discard the output on a subsequent rollback. The resource to which the writer is sending data should normally be able to handle this itself.
- **Tasklet** - The Tasklet is an interface which performs any single task such as setup resource, running a sql update, cleaning up resources etc.

##### Chunk Processing
- Suppose the job to be run is complex and involves executing of tasks involving reads, processing and writes the we use chunk oriented processing
- It involves reading an input, processing it based on the business logic and then aggregating it till the commit-interval is reached and finally writing out the chunk of data output to a file or database table.
- Usually used in scenarios where multiple aggregated steps need to be run like copying, processing and transferring of data .

Example
```
@Bean
	public Step orderStep1() {
		return stepBuilderFactory.get("orderStep1").<String, String> chunk(10)
				.reader(new Reader()).processor(new Processor())
				.writer(new Writer()).build();
	}
```

##### Concurrent Batch
Concurrent/on-line batch processing refers to the batch process that handles data being concurrently used/updated by online users so the data cannot be locked in database or file as the online users will need it. Also the data updates should be commited frequently at the end of few transactions to minimize the portion of data that is unavailable to other processes and the elapsed time the data is unavailable.

##### Parallel Processing
Parallel processing enables multiple batch runs jobs to run in parallel to reduce the total elapsed batch processing time. Parallel processing is simpler as long as the same file or database table is not shared among the processes otherwise the processes should process partitioned data.

Another approach would be using a control table for maintaining interdependencies and to track each shared resource in use by any process or not.

Other key issues in parallel processing include load balancing and the availability of general system resources such as files, database buffer pools etc. Also note that the control table itself can easily become a critical resource.

##### Metadata Schema
The Spring Batch Meta-Data tables are used to persist batch domain objects such as JobInstance, JobExecution, JobParameters, and StepExecution for internally managing the Batch Jobs.

The JobRepository is responsible for saving and storing each Java object into its correct table

