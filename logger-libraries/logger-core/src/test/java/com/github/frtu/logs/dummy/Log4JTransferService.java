package com.github.frtu.logs.dummy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4JTransferService extends TransferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(Log4JTransferService.class);

    @Override
    protected void beforeTransfer(long amount) {
        LOGGER.info("Preparing to transfer {}$.", amount);
    }

    @Override
    protected void afterTransfer(long amount, boolean outcome) {
        LOGGER.info("Has transfer of {}$ completed successfully ? {}.", amount, outcome);
    }
}
