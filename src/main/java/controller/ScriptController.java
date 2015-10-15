package controller;

import console.ScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller("groovyconsole")
public class ScriptController {

    @Autowired
    ScriptService scriptService;

    @RequestMapping(value="/script", method=RequestMethod.POST)
    @ResponseBody
    public Object executeScript(@RequestBody String script) {
        return scriptService.executeScript(script);
    }
}
