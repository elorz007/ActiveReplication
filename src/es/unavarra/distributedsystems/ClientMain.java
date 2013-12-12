package es.unavarra.distributedsystems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import es.unavarra.distributedsystems.client.Client;
import es.unavarra.distributedsystems.client.RR;
import es.unavarra.distributedsystems.communication.Connector;
import es.unavarra.distributedsystems.communication.MessageDeliverer;
import es.unavarra.distributedsystems.communication.NetworkNode;
import es.unavarra.distributedsystems.configuration.ConfigurationLoader;
import es.unavarra.distributedsystems.configuration.NodeListMapper;

public class ClientMain {

	public static void main(String[] args) {
		try {
			String settingsFile = null;
			if (args.length > 0) {
				settingsFile = args[0];
	        }
			Properties properties = new ConfigurationLoader().loadOrUseDefault(settingsFile);
			String clientAddress = properties.getProperty("CLIENT_ADDRESS");
			int numberOfClients = Integer.valueOf(properties.getProperty("CLIENT_NUMBER"));
			int numberOfMessagesPerClient = Integer.valueOf(properties.getProperty("CLIENT_MESSAGES"));
			int baseClientPort = Integer.valueOf(properties.getProperty("CLIENT_PORT_START"));
			int clientId = Integer.valueOf(properties.getProperty("CLIENT_ID"));
			int timeoutMillis = Integer.valueOf(properties.getProperty("CLIENT_TIMEOUT_MILLIS"));
			String arhNodeList = properties.getProperty("CLIENT_ARH_LIST");
			ArrayList<NetworkNode> middleware = new NodeListMapper().map(arhNodeList);
			spawnClients(clientAddress, clientId, numberOfClients, numberOfMessagesPerClient, baseClientPort, middleware, timeoutMillis);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void spawnClients(String address, int clientId, int numberOfClients, int numberOfMessagesPerClient, int baseClientPort, ArrayList<NetworkNode> middleware, int timeOutMillis) {
		for (int i = 0; i < numberOfClients; i++) {
			NetworkNode myself = new NetworkNode(address, baseClientPort + i);
			Connector connector = new Connector(myself );
			RR rr = new RR(connector, middleware, timeOutMillis);
			Client client = new Client(rr, numberOfMessagesPerClient, clientId + i);
			MessageDeliverer rrMessageDeliverer = new MessageDeliverer(myself, rr);
			new Thread(rrMessageDeliverer).start();
			new Thread(client).start();
		}
	}

}
