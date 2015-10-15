package console;

import java.util.Map;

public interface ScriptService {

    Map<String, String> executeScript(String script);
}