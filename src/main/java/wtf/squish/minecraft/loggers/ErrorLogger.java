package wtf.squish.minecraft.loggers;

import wtf.squish.minecraft.ErrorLog;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ErrorLogger extends Handler {
    @Override
    public void publish(LogRecord logRecord) {
        if(logRecord.getThrown() == null)
            return;

        ErrorLog log = new ErrorLog(logRecord.getThrown());
        log.send();
    }

    @Override
    public void flush() {}
    @Override
    public void close() {}
}
