package console;

import org.junit.Test;

import java.util.Map;

public class ScriptServiceImplTest {

    ScriptService scriptService = new ScriptServiceImpl();

    @Test
    public void testExecuteScript() {
        String script = "System.out.println(\"aok...\"); 3*2;";
        Map<String, String> resultMap = scriptService.executeScript(script);
        System.out.println(resultMap);
    }
}
