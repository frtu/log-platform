package com.github.frtu.logs.dummy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TransferService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransferService.class);

    public boolean transfer(long amount) {
        // connects to the remote service to actually transfer money
        beforeTransfer(amount);
        LOGGER.info("MAKE TRANSFER : {}", amount);
        boolean outcome = true;
        afterTransfer(amount, outcome);
        return outcome;
    }

    abstract protected void beforeTransfer(long amount);

    abstract protected void afterTransfer(long amount, boolean outcome);
}
