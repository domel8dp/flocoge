package pl.dpawlak.flocoge.generator.util;

import pl.dpawlak.flocoge.generator.CodeBlock;
import pl.dpawlak.flocoge.generator.CodeIf;

public class TestCodeIf implements CodeIf {

    public TestCodeBlock _then;
    public TestCodeBlock _else;

    @Override
    public CodeBlock _then() {
        _then = new TestCodeBlock();
        return _then;
    }

    @Override
    public CodeBlock _else() {
        _else = new TestCodeBlock();
        return _else;
    }
}