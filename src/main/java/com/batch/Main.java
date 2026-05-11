package com.batch;

import com.batch.config.AppConfig;
import com.batch.watcher.FolderWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("=== CSV Batch Processor - Demarrage ===");

        try {
            AppConfig config = new AppConfig();
            FolderWatcher watcher = new FolderWatcher(config);
            watcher.traiterDossier();
        } catch (Exception e) {
            log.error("Erreur critique : {}", e.getMessage(), e);
            System.exit(1);
        }

        log.info("=== CSV Batch Processor - Termine ===");
    }
}
