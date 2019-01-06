package com.samsung.nmt.cmenrichment.client;

import com.samsung.nmt.cmenrichment.exceptions.IllegalActionException;

/**
 * This class define enum for each action received from collector.
 *
 */
public enum Action {
    ADD, UPDATE, DELETE;

    public static Action getAction(String action) {
        Action eaction = null;

        switch (action) {
        case "UPDATE":
            eaction = Action.UPDATE;
            break;

        case "ADD":
            eaction = Action.ADD;
            break;

        case "DELETE":
            eaction = Action.DELETE;
            break;

        default:
            throw new IllegalActionException("Illegal Action : " + action);
        }

        return eaction;
    }
}
