package com.trepudox;

import com.trepudox.logger.CustomLogger;
import com.trepudox.worker.WorkerThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final CustomLogger LOGGER = CustomLogger.getLogger();
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) {
        LOGGER.info("De acordo com a potência da máquina local, o Server terá um total de %s threads disponíveis.", AVAILABLE_PROCESSORS);

        ExecutorService executorService = Executors.newFixedThreadPool(AVAILABLE_PROCESSORS);
        for(int i = 0; i < AVAILABLE_PROCESSORS; i++) {
            executorService.execute(new WorkerThread(i));
        }
    }

}
