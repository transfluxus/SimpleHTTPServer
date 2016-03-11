package http;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

class SimpleFormatter extends Formatter {

	String nl = System.getProperty("line.separator");
	
		@Override
		public String format(LogRecord record) {
			return record.getLevel() + " : " + record.getMessage() + nl;
		}
	
}
