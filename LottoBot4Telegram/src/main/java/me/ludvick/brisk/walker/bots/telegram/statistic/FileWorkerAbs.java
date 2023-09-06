package me.ludvick.brisk.walker.bots.telegram.statistic;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public abstract class FileWorkerAbs {
    public FileWorkerAbs() {}

    public abstract String downloadFileFromURL(URL url, File outputFile) throws IOException;

    public abstract void deleteOldestFile(String folder) throws IOException;

    public abstract void deleteFileByName(File fileToDelete) throws IOException;

}
