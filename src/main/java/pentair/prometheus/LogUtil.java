package pentair.prometheus;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.prometheus.client.Counter;

public class LogUtil {

	private static final LogUtil LOGGER = new LogUtil();

	public static LogUtil log() {
		return LOGGER;
	}

	private static final String NAMESPACE = "pentair";

	private final SimpleDateFormat LOG_FMT = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
	private final ObjectMapper mapper = new ObjectMapper();
	private final Counter errorCount, warnCount;

	private LogUtil() {
		errorCount = Counter.build().namespace(NAMESPACE).name("errors_total").help("All errors").register();
		warnCount = Counter.build().namespace(NAMESPACE).name("warns_total")
				.help("Warnings are errors that were caught and handled, app should still function").register();
	}

	public Object lazy(Supplier<String> s) {
		return new Object() {
			@Override
			public String toString() {
				return s.get();
			}
		};
	}

	public Object json(Object s) {
		return new Object() {
			@Override
			public String toString() {
				return jsonToString(s);
			}
		};
	}

	public String jsonToString(Object msg) {
		try {
			return mapper.writeValueAsString(msg);
		} catch (Throwable t) {
			warn("Failed to serialize message to JSON: {}", t, msg);
			return "Failed to write message to JSON: " + msg;
		}
	}

	public void info(String log) {
		log(System.out, "INFO", log, null, null);
	}

	public void info(String log, Object msg) {
		log(System.out, log, "INFO", null, msg);

	}

	public void warn(String log, Throwable t, Object msg) {
		warnCount.inc();
		log(System.out, "WARN", log, t, msg);
	}

	public void error(String log, Throwable t, Object msg) {
		errorCount.inc();
		log(System.err, "ERROR", log, t, msg);
	}

	public void log(PrintStream out, String level, String log, Throwable t, Object msg) {
		String timeStamp = LOG_FMT.format(new Date());
		out.println(timeStamp + ": " + level + ": " + log + (t == null ? "" : (": " + t)));
		if (t != null)
			t.printStackTrace(out);
		if (msg != null) {
			out.println(timeStamp + ": MSG: " + json(msg));
		}
		out.flush();
	}

}
