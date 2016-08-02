package pl.dpawlak.flocoge.config;

import java.io.File;

public class ConfigurationBuilder {

    private File diagramPath;
    private File srcFolder;
    private String packageName;
    private String name;
    private boolean stacktrace;
    private boolean verbose;
    private boolean printBareModel;
    private boolean printModel;
    private boolean trace;
    private boolean dry;

    public ConfigurationBuilder withDiagramPath(File diagramPath) {
        this.diagramPath = diagramPath;
        return this;
    }

    public ConfigurationBuilder withSrcFolder(File srcFolder) {
        this.srcFolder = srcFolder;
        return this;
    }

    public ConfigurationBuilder withPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public ConfigurationBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ConfigurationBuilder printStacktrace() {
        stacktrace = true;
        return this;
    }

    public ConfigurationBuilder verbose() {
        verbose = true;
        return this;
    }

    public ConfigurationBuilder printBareModel() {
        printBareModel = true;
        return this;
    }

    public ConfigurationBuilder printModel() {
        printModel = true;
        return this;
    }

    public ConfigurationBuilder trace() {
        trace = true;
        return this;
    }

    public ConfigurationBuilder dryRun() {
        dry = true;
        return this;
    }

    public Configuration build() {
        return new Configuration(diagramPath, srcFolder, packageName, name, stacktrace, verbose, printBareModel,
            printModel, trace, dry);
    }
}
