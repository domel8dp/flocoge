package pl.dpawlak.flocoge.generator.util;

import java.util.LinkedHashMap;
import java.util.Map;

import pl.dpawlak.flocoge.generator.CodeBlock;
import pl.dpawlak.flocoge.generator.CodeSwitch;

public class TestCodeSwitch implements CodeSwitch {

    public final Map<String, TestCodeBlock> cases = new LinkedHashMap<>();

    @Override
    public CodeBlock _case(String name) {
        TestCodeBlock _case = new TestCodeBlock();
        cases.put(name, _case);
        return _case;
    }
}