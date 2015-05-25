About Apache Storm
Apache Storm is a free and open source distributed real-time fault-tolerant computation system. Storm makes it easy to reliably process unbounded streams of data, doing for real-time processing what Hadoop did for batch processing. Storm is simple, can be used with any programming language. Storm works as a topology where stream of tuples starts from Spouts and goes through bolts for real time computation. 
Some of the key storm abstractions are explained below:
•	Topology is a network of Spouts and Bolts.
•	Tuple is a named list of values, where each value can be any type
•	Stream	is an unbounded sequence of tuples that is processed and created in parallel in a distributed fashion
•	Spout is a source of streams in a topology.
•	Bolts contains logic of computation such as functions, filters, streaming joins, talking to database etc.
Storm topology may be executed in two modes – Local and Distributed modes. Local mode is generally used for development purposes wherein whole topology is run on single machine for a limited time. In production, distributed mode is used with an ever running topology on multiple nodes.
Some of the key benefits of Storm Event Streaming are that it is quite scalable, serves extremely broad set of use cases, easy to setup, programming language agnostic, fault tolerant and guarantees no data loss.
Some drawbacks of Storm are poor Storm Javadoc API documentation of it is hard to provide an intermix of high throughput stream processing, manipulation and low latency querying.
Live Twitter Emotion trends using Storm Streaming
I used Storm Event Streaming to develop a small application which gets the live sample tweet stream to generate a stream of English tweets from a spout. I used Twitter4J library for reading sample tweet stream. This input stream goes through a topology containing multiple computation bolts which reads the tweets and finally spits the counts of some emotions related words which gives an idea of how emotions are trending on twitter. As a sample I have just selected a few words for the trending analysis – ‘love’, ‘hate’, ‘lol’ and ‘happy’. The output is observed on the console output which is updated every time count of any of the emotion words updates. This project also demonstrates the use of couple of ShuffleGrouping where we don’t care about which task of the bolt receives the tuple and FieldGrouping used in the end where I needed to make sure that same bolt task gets same words every time.



