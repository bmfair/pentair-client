package pentair.prometheus;

import java.io.Closeable;
import java.io.IOException;
import java.util.function.Consumer;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.prometheus.client.Counter;
import pentair.client.PentairClient;
import pentair.model.messages.NotifyList;
import pentair.model.messages.RequestParamList;

public class ResilientClient implements Closeable {

	private final String ip;
	private final int port;

	private PentairClient client;
	private boolean closed = false;
	private final Consumer<ResilientClient> connectCallback;

	public ResilientClient(String ip, int port, Consumer<ResilientClient> connectCallback) {
		this.ip = ip;
		this.port = port;
		this.connectCallback = connectCallback;
		tryConnect();
	}

	private void tryConnect() {
		tryCleanup();
		while (!closed) {
			try {
				this.client = new PentairClient(ip, port);
				LogUtil.log().info("Connected to " + ip + ":" + port);
				connectCallback.accept(this);
				return;
			} catch (IOException e) {
				LogUtil.log().warn("Failed to connect to " + ip + ":" + port, e, null);
			}
			try {
				Thread.sleep(30 * 1000L);
			} catch (InterruptedException e) {
				LogUtil.log().warn("Sleep Interrupted", e, null);
			}
		}
	}

	private static final int INACTIVITY_MAX = 60;

	public NotifyList tryRead(Counter msgCount) {
		int i = 0;
		while (!closed) {
			try {
				while (!client.ready()) {
					try {
						Thread.sleep(1000);
						i++;
						if (i > INACTIVITY_MAX) {
							client.ping(); // it is unlikely but possible this consumes a real message instead of pong,
											// we can afford to lose 1
							LogUtil.log().info("Ping-pong keep alive success");
							i = 0; // reset inactivity
							msgCount.inc();
						}
					} catch (InterruptedException e) {
						LogUtil.log().warn("Interrupted while waiting for data", e, null);
						return null;
					}
				}
				NotifyList msg = client.readNotifyUpdate();
				msgCount.inc();
				return msg;
			} catch (IOException e) {
				LogUtil.log().warn("Read failed", e, null);
			}
			tryConnect();
		}
		return null;
	}

	public boolean trySend(RequestParamList req) throws JsonProcessingException {
		return trySend(client.toString(req));
	}

	public boolean trySend(String s) {
		while (!closed) {
			try {
				client.send(s);
				LogUtil.log().info("Sent message: " + s);
				return true;
			} catch (IOException e) {
				LogUtil.log().warn("Send failed", e, null);
			}
			tryConnect();
		}
		return false;
	}

	private void tryCleanup() {
		if (client != null) {
			try {
				client.close();
				client = null;
			} catch (IOException e) {
				LogUtil.log().warn("Failed to close client", e, null);
			}
		}
	}

	@Override
	public void close() throws IOException {
		this.closed = true;
		tryCleanup();
	}

}
