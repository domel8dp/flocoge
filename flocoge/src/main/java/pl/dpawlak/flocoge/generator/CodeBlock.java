package pl.dpawlak.flocoge.generator;

public interface CodeBlock {

    void call(String name);
    void callDelegate(String name);
    void callExternal(String name);
    CodeIf _if(String name);
    CodeIf _ifNot(String name);
    CodeSwitch _switch(String name);
    void _return();
    void _break();
}