# ActiveReplication
3-layered system that simulates a distributed systems where some clients make requests to be executed and some replicas keep track of them. The system is distributed, asynchronous and fault-tolerant.
The main components of the software are:
* **FO** - The replicas, these are the endpoint of the requests. All the replicas will have the same executions and in the same order. Replicas can fail but at least one is needed.
* **ARH** - The middle layer. These are the responsibles of accepting client requests, order them (using DSS, sequencers, and total order broadcasts) and retransmit them to the replicas.

## Install
Download the source code and compile it using:
```
    ant
```

## Getting Started
To run the programs use:

Replica (FO)
```
	java -cp ./bin es.unavarra.distributedsystems.FOMain cfg/FO1.txt
```

Middleware (ARH+DSS)
```
	java -cp ./bin es.unavarra.distributedsystems.ARHMain cfg/ARH1.txt
```

Client (RR)
```
	java -cp ./bin es.unavarra.distributedsystems.ClientMain cfg/Client1.txt
```

Note that the programs take one parameter that specifies the config file (if not given it is assumed it exists on 'cfg/config.txt')

## Config file
The configuration file is a standard java properties file. It should contain the values in the format explained below.

### Client
* CLIENT_ADDRESS the host the listening server will bind to
* CLIENT_NUMBER the number of clients that want to be spawned
* CLIENT_PORT_START the port number were the client will listen (if more than one client they use the next port numbers)
* CLIENT_ARH_LIST a comma separated list of host:port values that tell the client how to find the middleware processess
* CLIENT_ID identification number that make their requests unique
* CLIENT_TIMEOUT_MILLIS the number of milliseconds a client waits until another middleware process is tried if the previous one is not responding
Example
```
	CLIENT_ADDRESS=localhost
	CLIENT_NUMBER=1
	CLIENT_MESSAGES=5
	CLIENT_PORT_START=3030
	CLIENT_ARH_LIST=localhost:4040,localhost:4141
	CLIENT_ID=10
	CLIENT_TIMEOUT_MILLIS=10000
```


### Middleware
* The parameters are similar to the previous ones
* ARH_FO_LIST tells the middleware where to find the replcias
* ARH_SEQUENCER_PORT_START specifies which port the DSS will use
* ARH_SEQUENCER_LIST specifies the list of the other DSSs (only include external DSS addresses)
```
	ARH_ADDRESS=localhost
	ARH_NUMBER=1
	ARH_PORT_START=4040
	ARH_FO_LIST=localhost:5050,localhost:5151,localhost:5252
	ARH_SEQUENCER_PORT_START=4045
	ARH_SEQUENCER_LIST=localhost:4145

```

### Replica
* All the parameters are similar to the previous ones
Example
```
FO_ADDRESS=localhost
FO_NUMBER=1
FO_PORT_START=5050
```
###

