package com.crikkit.webserver;

import com.crikkit.webserver.logs.CrikkitLogger;

import javax.script.*;
import java.io.*;

public class CSEM {

    private static CSEM instance;

    public static CSEM getInstance() {
        if (instance == null) {
            instance = new CSEM();
        }

        return instance;
    }

    private ScriptEngine engine;
    private StringWriter stringWriter;
    private final String symbolStart = "<?sjs";
    private final String symbolEnd = "?>";

    private CSEM() {
        stringWriter = new StringWriter();
        engine = new ScriptEngineManager().getEngineByName("nashorn");
        Bindings customBindings = engine.createBindings();
        customBindings.put("$version", Settings.getInstance().getVersion());
        engine.setBindings(customBindings, ScriptContext.ENGINE_SCOPE);
        engine.getContext().setWriter(stringWriter);
    }

    private Object eval(String statement) throws ScriptException {
        return engine.eval(statement);
    }

    public String translate(String html) {
        while(html.contains(symbolStart) && html.contains(symbolEnd)) {
            int indexOfStart = html.indexOf(symbolStart);
            int indexOfEnd = html.indexOf(symbolEnd);

            if (indexOfStart >= 0 && indexOfEnd >= 0) {
                String script = html.substring(indexOfStart + symbolStart.length(), indexOfEnd);
                try {
                    eval(script);
                } catch (ScriptException e) {
                    CrikkitLogger.getInstance().severe(e);
                } finally {
                    String before = html.substring(0, indexOfStart);
                    String after = html.substring(indexOfEnd + symbolEnd.length());
                    html = before + stringWriter + after;
                }
            }

            stringWriter.getBuffer().setLength(0);
        }

        return html;
    }

}
