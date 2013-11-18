package es.unavarra.distributedsystems;

import java.net.InetSocketAddress;
import java.util.ArrayList;

import es.unavarra.distributedsystems.client.Client;
import es.unavarra.distributedsystems.common.Logger;
import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.NetworkNode;
import es.unavarra.distributedsystems.communication.Receiver;
import es.unavarra.distributedsystems.end.FO;
import es.unavarra.distributedsystems.middleware.ARH;
import es.unavarra.distributedsystems.middleware.DSS;

public class Main {

	public static void main(String[] args) {
		int numberOfMessagesPerClient = 2;
		int numberOfReplicas = 3;
		int numberOfArh = 2;
		int numberOfClients = 3;
		
		int port = 4242;
		
		ArrayList<NetworkNode> replicas = new ArrayList<NetworkNode>(numberOfReplicas);
		for (int i = 0; i<numberOfReplicas; i++) {
			Connector connector = buildNewConnector(port++);
			FO fo = new FO(connector);
			replicas.add(connector.getFrom());
		}
		
		ArrayList<NetworkNode> middleware = new ArrayList<Receiver>(numberOfArh);
		ArrayList<NetworkNode> sequencers = new ArrayList<Receiver>(numberOfArh);
		ArrayList<Connector> connectors = new ArrayList<Connector>(numberOfArh);
		for (int i = 0; i<numberOfArh; i++) {
			DSS sequencer = new DSS(buildNewConnector(port++));
			sequencers.add(sequencer);
			Connector newConnector = buildNewConnector(port++);
			connectors.add(newConnector);
			middleware.add(new ARH(newConnector, sequencer, replicas));
		}
		for (Connector c : connectors) {
			c.setBroadCastGroup(sequencers);
		}
		
		ArrayList<Thread> spawnedClients = new ArrayList<Thread>();
		for (int i = 0; i<numberOfClients; i++) {
			Client client = new Client(numberOfMessagesPerClient, i, middleware, buildNewConnector(port++));
			Thread thread = new Thread(client);
			thread.start();
			spawnedClients.add(thread);
		}
		
		for (Thread waitForIt: spawnedClients) {
			try {
				waitForIt.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Logger.eLog("******All clients finished********");
	}

	private static Connector buildNewConnector(int port) {
		return new Connector(new NetworkNode("localhost", port));
	}
}
