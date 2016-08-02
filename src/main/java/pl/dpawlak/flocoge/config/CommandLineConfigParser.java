package pl.dpawlak.flocoge.config;

import java.io.File;

import pl.dpawlak.flocoge.log.Logger;

import static pl.dpawlak.flocoge.config.ConfigurationUtils.*;

public class CommandLineConfigParser {

    private final Logger log;
    private final ConfigurationBuilder builder;

    private Configuration configuration;
    private boolean valid;
    private boolean dry;
    private File diagramPath;
    private String name;

    public CommandLineConfigParser(Logger log) {
        this.log = log;
        builder = new ConfigurationBuilder();
        valid = true;
    }

    public boolean parse(String[] args) {
        if (args.length >= 3) {
            valid = parseFlags(args);
            setDiagramPath(new File(args[args.length - 3]));
            setSrcFolder(new File(args[args.length - 2]));
            setPackageName(args[args.length - 1]);
            setName();
            if (valid) {
                configuration = builder.build();
            }
            return valid;
        } else {
            printHelp();
            return false;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    private boolean parseFlags(String[] args) {
        for (int i = 0; i < args.length - 3; i++) {
            String arg = args[i];
            if ("--help".equals(arg) || "-h".equals(arg)) {
                printHelp();
                return false;
            } else if ("--stacktrace".equals(arg) || "-s".equals(arg)) {
                builder.printStacktrace();
            } else if ("--verbose".equals(arg) || "-v".equals(arg)) {
                builder.verbose();
            } else if ("--print-bare".equals(arg) || "-b".equals(arg)) {
                builder.printBareModel();
            } else if ("--print-model".equals(arg) || "-p".equals(arg)) {
                builder.printModel();
            } else if ("--trace".equals(arg) || "-t".equals(arg)) {
                builder.trace().printStacktrace();
            } else if ("--dry".equals(arg) || "-d".equals(arg)) {
                builder.dryRun();
                dry = true;
            } else if ("--name".equals(arg) || "-n".equals(arg)) {
                if (i < args.length - 4) {
                    builder.withName(args[++i]);
                } else {
                    log.error("Name was not specified after --name (-n) was used");
                    return false;
                }
            } else {
                log.error("Unrecognized flag: ({}), use --help to see available options", arg);
                return false;
            }
        }
        return true;
    }

    private void setDiagramPath(File diagramFile) {
        if (valid) {
            if (diagramExists(diagramFile)) {
                builder.withDiagramPath(diagramFile);
                diagramPath = diagramFile;
            } else {
                log.error("Diagram file does not exist ({})", diagramFile.getPath());
                valid = false;
            }
        }
    }

    private void setSrcFolder(File srcFolder) {
        if (valid && !dry) {
            if (srcFolderExists(srcFolder) || createIfMissing(srcFolder)) {
                builder.withSrcFolder(srcFolder);
            } else {
                log.error("Sources folder is not available and could not be created ({})", srcFolder.getPath());
                valid = false;
            }
        }
    }

    private void setPackageName(String packageName) {
        if (valid) {
            if (packageNameValid(packageName)) {
                builder.withPackageName(packageName);
            } else {
                log.error("Invalid package name ({})", packageName);
                valid = false;
            }
        }
    }

    private void setName() {
        if (valid) {
            if (name != null) {
                if (diagramNameValid(name)) {
                    builder.withName(name);
                } else {
                    log.error("Given name can not be used as a Java class name ({})", name);
                    valid = false;
                }
            } else if (!diagramNameValid(diagramPath)) {
                log.error("Diagram file name can not be used as a Java class name ({})", diagramPath.getName());
                valid = false;
            }
        }
    }

    private void printHelp() {
        log.log("");
        log.log("-------");
        log.log("Draw.io Flowchart Code Generator");
        log.log("-------");
        log.log("");
        log.log("Usage:");
        log.log("[flags] <diagram path> <output folder> <package name>");
        log.log("");
        log.log("Flags:");
        log.log("--help -h : print this help message");
        log.log("--verbose -v : print some information about execution");
        log.log("--name -n <class name> : use this name for output classes");
        log.log("");
        log.log("Troubleshooting flags:");
        log.log("--print-bare -b : print bare model before validation and transformation");
        log.log("--print-model -p : print model after validation and transformation");
        log.log("--stacktrace -s : print full stacktrace for exception");
        log.log("--trace -t : print every step, implies --verbose and --stacktrace flags");
        log.log("--dry -d : run without generating output files");
        log.log("");
    }
}
