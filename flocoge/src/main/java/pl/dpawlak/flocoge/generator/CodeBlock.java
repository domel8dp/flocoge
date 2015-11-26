package pl.dpawlak.flocoge.generator;

interface CodeBlock {

    void call(String name);
    void callDelegate(String name);
    void callExternal(String name);
    CodeIf _if(String name);
    CodeSwitch _switch(String name);
    void _return();
    void _break();
}