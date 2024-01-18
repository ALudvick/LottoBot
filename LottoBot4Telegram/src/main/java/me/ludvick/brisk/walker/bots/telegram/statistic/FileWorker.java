package me.ludvick.brisk.walker.bots.telegram.statistic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

interface FileWorker {
    Logger logger = LogManager.getLogger(FileWorker.class);

    void downloadFileFromURL();
    void deleteOldestFile();
    void deleteFileByName();

}
