package com.github.frtu.logs;

import ch.qos.logback.classic.LoggerContext;
import com.github.frtu.logs.dummy.Log4JTransferService;
import com.github.frtu.logs.dummy.Transfer;
import com.github.frtu.logs.dummy.TransferService;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Quick copy of baeldung MDC code for end-to-end logger demo
 * https://www.baeldung.com/mdc-in-log4j-2-logback
 */
public class TransferDemo {
    public static void main(String[] args) {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        //----------------------
        if (!lc.isStarted()) {
            lc.start();
        }
        //----------------------

//        ExecutorService executor = newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            final String transactionId = "transaction-" + i;
            final String sender = "sender-" + i;
            final long amount = i;
            Transfer tx = new Transfer(transactionId, sender, amount); //transactionFactory.newInstance();
            Log4JRunnable task = new Log4JRunnable(tx);
            task.activateMDC();
//            executor.submit(task);
            task.run();
        }
//        executor.shutdown();

        //----------------------
        lc.stop();
        //----------------------
    }

    public static class Log4JRunnable implements Runnable {
        private final Logger LOGGER = LoggerFactory.getLogger(Log4JRunnable.class);

        private Transfer tx;
        private TransferService transferService;
        private boolean isMDCActive = false;

        public Log4JRunnable(Transfer tx) {
            this.tx = tx;
            this.transferService = new Log4JTransferService();
        }

        public void activateMDC() {
            isMDCActive = true;
        }

        public void run() {
            if (isMDCActive) {
                MDC.put("transaction.id", tx.getTransactionId());
                MDC.put("transaction.owner", tx.getSender());
            }
            transferService.transfer(tx.getAmount());
            if (isMDCActive) {
                MDC.clear();
            }
        }
    }
}
