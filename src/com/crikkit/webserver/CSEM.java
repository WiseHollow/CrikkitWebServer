package com.crikkit.webserver;

import com.crikkit.webserver.logs.CrikkitLogger;
import com.crikkit.webserver.requests.HttpRequest;

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
        initializeBindings();
        engine.getContext().setWriter(stringWriter);
    }

    private void initializeBindings() {
        Bindings customBindings = engine.createBindings();
        customBindings.put("$_VERSION", Settings.getInstance().getVersion());
        engine.setBindings(customBindings, ScriptContext.ENGINE_SCOPE);
    }

    public void includeHeaderData(HttpRequest httpRequest) {
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

        bindings.put("$_ISPOST", httpRequest.getPostData().size() > 0);
        bindings.put("$_ISGET", httpRequest.getGetData().size() > 0);
        bindings.put("$_POST", httpRequest.getPostData());
        bindings.put("$_GET", httpRequest.getGetData());

        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
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

        initializeBindings();

        return html;
    }

}
