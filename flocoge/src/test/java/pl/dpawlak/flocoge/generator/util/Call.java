package pl.dpawlak.flocoge.generator.util;

import java.util.Objects;

public class Call {

    public enum Type { LOCAL, DELEGATE, IF, SWITCH, EXTERNAL, RETURN, BREAK }

    public final String name;
    public final Type type;

    public Call(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Call)) {
            return false;
        }
        Call that = (Call)obj;
        return Objects.equals(this.name, that.name) && Objects.equals(this.type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}