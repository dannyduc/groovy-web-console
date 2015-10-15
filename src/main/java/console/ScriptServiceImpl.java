package console;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.ui.SystemOutputInterceptor;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class ScriptServiceImpl implements ScriptService {

    @Autowired
    private ApplicationContext applicationContext;

    Logger logger = Logger.getLogger(getClass());

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public Map<String, String> executeScript(String script) {
        logger.info(String.format("Executing script: %s", script));

        try {
            return eval(script);
        } catch (Throwable t) {
            logger.info(t);
            Map<String, String> resultMap = new HashMap<String, String>();
            resultMap.put("error", t.getMessage());
            return resultMap;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    Map<String, String> eval(String script) {
        Map<String, String> resultMap = new HashMap<String, String>();
        resultMap.put("script", script);
        resultMap.put("startTime", DateTime.now().toString());

        SystemOutputInterceptorClosure outputCollector = new SystemOutputInterceptorClosure(null);
        SystemOutputInterceptor systemOutputInterceptor = new SystemOutputInterceptor(outputCollector);
        systemOutputInterceptor.start();

        try {
            Map<String, Object> bindingValues = new HashMap<String, Object>();
            resultMap.put("result", eval(script, bindingValues));
        } catch (Throwable t) {
            logger.info(t);
            resultMap.put("error", t.getMessage());
        } finally {
            systemOutputInterceptor.stop();
        }

        resultMap.put("output", outputCollector.getStringBuffer().toString().trim());
        resultMap.put("endTime", DateTime.now().toString());
        return resultMap;
    }

    String eval(String script, Map<String, Object> bindingValues) {
        GroovyShell shell = createShell(bindingValues);
        Object result = shell.evaluate(script);
        return result == null ? "null" : result.toString();
    }

    GroovyShell createShell(Map<String, Object> bindingValues) {
        bindingValues.put("ctx", applicationContext);
        bindingValues.put("logger", logger);
        return new GroovyShell(getClass().getClassLoader(), new Binding(bindingValues));
    }
}
