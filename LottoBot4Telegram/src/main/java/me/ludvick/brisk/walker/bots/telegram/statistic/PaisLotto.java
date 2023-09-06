package me.ludvick.brisk.walker.bots.telegram.statistic;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PaisLotto extends FileWorkerAbs {
    @Override
    public String downloadFileFromURL(URL url, File outputFile) throws IOException {
        FileUtils.copyURLToFile(url, outputFile);
        return outputFile.getName();
    }

    @Override
    public void deleteOldestFile(String folder) throws IOException {

    }

    @Override
    public void deleteFileByName(File fileToDelete) throws IOException {
        FileUtils.delete(fileToDelete);
    }
}
