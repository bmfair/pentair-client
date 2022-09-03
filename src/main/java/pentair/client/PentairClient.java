package pentair.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import pentair.model.PentairJson;
import pentair.model.messages.NotifyList;
import pentair.model.messages.PentairMessage;
import pentair.model.messages.PentairRequest;
import pentair.model.messages.PentairResponse;

public class PentairClient {

	public static int INTELLICENTER_PORT = 6681;
	public static String INTELLICENTER_IP = "192.168.1.249";

	private Socket clientSocket;
	private final BufferedReader bufferInput;
	private PentairJson j;

	public PentairJson getMapper() {
		return this.j;
	}

	public PentairClient() throws UnknownHostException, IOException {
		this(INTELLICENTER_IP, INTELLICENTER_PORT);
	}

	public PentairClient(String ip, int port) throws UnknownHostException, IOException {
		System.out.format("Client attempting to connect to %s:%d%n", ip, port);
		clientSocket = new Socket(ip, port);
		bufferInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		System.out.println("Client connected to server");
		j = new PentairJson();
	}

	public String toString(PentairMessage<?> msg) throws JsonProcessingException {
		return j.toString(msg);
	}

	public void send(Object msg) throws JsonGenerationException, JsonMappingException, IOException {
		System.out.println("Client sending message...");
		j.write(clientSocket.getOutputStream(), msg);
	}

	public void send(String msg) throws IOException {
		System.out.println("Client sending message...");
		clientSocket.getOutputStream().write(msg.getBytes());
	}

	public void ping() throws IOException {
		send("ping");
	}

	public PentairResponse sendReq(PentairRequest<?> msg)
			throws JsonGenerationException, JsonMappingException, IOException {
		return sendReq(msg, PentairResponse.class);
	}

	public <T> T sendReq(Object msg, Class<T> responseClass)
			throws JsonGenerationException, JsonMappingException, IOException {
		send(msg);
		System.out.println("Client sent message, awaiting response...stream closed is: " + clientSocket.isClosed());
		return j.read(responseClass, clientSocket.getInputStream());
	}

	public NotifyList readNotifyUpdate() throws JsonParseException, JsonMappingException, IOException {
		if (!clientSocket.isClosed()) {
			// First see if its a ping-pong
			String line = bufferInput.readLine();
			if ("pong".equals(line)) {
				System.out.println("pong");
				return readNotifyUpdate();
			}
			return j.readNotify(line);
		} else
			return null;
	}

	public void close() throws IOException {
		bufferInput.close();
		clientSocket.close();
	}

}
