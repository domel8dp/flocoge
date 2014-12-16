package pl.dpawlak.flocoge.config;

import java.io.File;

/**
 * Created by dpawlak on Dec 14, 2014
 */
public class Configuration {
    
    public final File diagramPath;
    public final File srcFolder;
    public final String packageName;
    
    public Configuration(File diagramPath, File srcFolder, String packageName) {
        this.diagramPath = diagramPath;
        this.srcFolder = srcFolder;
        this.packageName = packageName;
    }

}
