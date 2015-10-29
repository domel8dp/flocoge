package pl.dpawlak.flocoge.log.util;

import pl.dpawlak.flocoge.log.ErrorLogger;

public class ErrorCollectingLogger extends ErrorLogger {

    private String error;

    public ErrorCollectingLogger() {
        super(false, null);
    }

    @Override
    public void error(String msg) {
        error = msg;
    }

    @Override
    public void error(String msg, Object... objects) {
        error = msg;
    }

    public String getError() {
        return error;
    }
}
