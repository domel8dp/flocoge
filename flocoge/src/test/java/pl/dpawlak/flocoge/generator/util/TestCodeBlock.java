package pl.dpawlak.flocoge.generator.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import pl.dpawlak.flocoge.generator.CodeBlock;
import pl.dpawlak.flocoge.generator.CodeIf;
import pl.dpawlak.flocoge.generator.CodeSwitch;

public class TestCodeBlock implements CodeBlock {

    public final List<Call> calls = new LinkedList<>();
    public final Map<String, TestCodeIf> ifs = new HashMap<>();
    public final Map<String, TestCodeSwitch> switches = new HashMap<>();

    @Override
    public void call(String name) {
        calls.add(new Call(name, Call.Type.LOCAL));
    }

    @Override
    public void callDelegate(String name) {
        calls.add(new Call(name, Call.Type.DELEGATE));
    }

    @Override
    public void callExternal(String name) {
        calls.add(new Call(name, Call.Type.EXTERNAL));
    }

    @Override
    public CodeIf _if(String name) {
        calls.add(new Call(name, Call.Type.IF));
        TestCodeIf _if = new TestCodeIf();
        ifs.put(name, _if);
        return _if;
    }

    @Override
    public CodeSwitch _switch(String name) {
        calls.add(new Call(name, Call.Type.SWITCH));
        TestCodeSwitch _switch = new TestCodeSwitch();
        switches.put(name, _switch);
        return _switch;
    }

    @Override
    public void _return() {
        calls.add(new Call("", Call.Type.RETURN));
    }

    @Override
    public void _break() {
        calls.add(new Call("", Call.Type.BREAK));
    }
}