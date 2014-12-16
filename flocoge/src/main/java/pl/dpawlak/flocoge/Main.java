package pl.dpawlak.flocoge;

import pl.dpawlak.flocoge.config.CommandLineConfigParser;

/**
 * Created by dpawlak on Dec 14, 2014
 */
public class Main {

    public static void main(String[] args) {
        CommandLineConfigParser parser = new CommandLineConfigParser();
        if (parser.parse(args)) {
            parser.getConfiguration();
        }
    }
}
