package pl.dpawlak.flocoge.config;

import java.io.File;

public class Configuration {

    public final File diagramPath;
    public final File srcFolder;
    public final String packageName;
    public final String name;
    public final boolean stacktrace;
    public final boolean verbose;
    public final boolean printBareModel;
    public final boolean printModel;
    public final boolean trace;
    public final boolean dry;

    public Configuration(File diagramPath, File srcFolder, String packageName, String name, boolean stacktrace,
            boolean verbose, boolean printBareModel, boolean printModel, boolean trace, boolean dry) {
        this.diagramPath = diagramPath;
        this.srcFolder = srcFolder;
        this.packageName = packageName;
        this.name = name;
        this.stacktrace = stacktrace;
        this.verbose = verbose;
        this.printBareModel = printBareModel;
        this.printModel = printModel;
        this.trace = trace;
        this.dry = dry;
    }
}
