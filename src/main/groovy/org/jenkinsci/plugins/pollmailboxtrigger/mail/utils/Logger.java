package org.jenkinsci.plugins.pollmailboxtrigger.mail.utils;

import org.jenkinsci.lib.xtrigger.XTriggerLog;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * <p>
 *     Logger interface, so that we're not tied to XTriggerLog!
 * </p>
 * <p>
 *     Question: is there a better/generic way of doing this?
 * </p>
 *
 * @author Nick Grealy
 */
@SuppressWarnings("unused")
public abstract class Logger {

    public static final Logger DEFAULT = new Logger() {

        @Override
        public void info(String message) {
            write(System.out, message);
        }

        @Override
        public void error(String message) {
            write(System.err, message);
        }

        private void write(PrintStream out, String message) {
            if (!"".equals(message.trim())) {
                out.print(message);
                if (!message.contains("\n")) {
                    out.print("\n");
                }
            }
        }
    };

    public abstract void info(String message);

    public abstract void error(String message);

    public OutputStream getOutputStream() {
        return new LoggerStream(this);
    }

    public PrintStream getPrintStream() {
        return new PrintStream(new LoggerStream(this));
    }

    /* utilty classes */

    public static abstract class HasLogger {

        public Logger logger;

        public HasLogger(Logger logger) {
            this.logger = logger;
        }
    }

    public static class LoggerStream extends OutputStream {

        private final Logger logger;

        public LoggerStream(Logger logger) {
            this.logger = logger;
        }

        @Override
        public void write(byte[] b) throws IOException {
            logger.info(new String(b));
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            logger.info(new String(b, off, len));
        }

        @Override
        public void write(int b) throws IOException {
            write(new byte[]{(byte) b});
        }
    }

    /* implementations */

    public static class XTriggerLoggerWrapper extends Logger {

        private XTriggerLog delegate;

        public XTriggerLoggerWrapper(XTriggerLog delegate) {
            this.delegate = delegate;
        }

        public void error(String message) {
            delegate.error(message);
        }

        public void info(String message) {
            delegate.info(message);
        }
    }

}
