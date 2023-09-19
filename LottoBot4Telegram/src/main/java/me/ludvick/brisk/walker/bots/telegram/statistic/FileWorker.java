package me.ludvick.brisk.walker.bots.telegram.statistic;

interface FileWorker {

    void downloadFileFromURL();
    void deleteOldestFile();
    void deleteFileByName();

}
