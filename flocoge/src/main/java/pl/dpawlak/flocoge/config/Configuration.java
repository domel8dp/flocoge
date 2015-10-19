package pl.dpawlak.flocoge.config;

import java.io.File;

public class Configuration {

    public final File diagramPath;
    public final File srcFolder;
    public final String baseName;
    public final String packageName;
    public final boolean stacktrace;
    public final boolean verbose;
    public final boolean printModel;
    public final boolean trace;

    public Configuration(File diagramPath, File srcFolder, String baseName, String packageName, boolean stacktrace,
            boolean verbose, boolean printModel, boolean trace) {
        this.diagramPath = diagramPath;
        this.srcFolder = srcFolder;
        this.baseName = baseName;
        this.packageName = packageName;
        this.stacktrace = stacktrace;
        this.verbose = verbose;
        this.printModel = printModel;
        this.trace = trace;
    }
}
