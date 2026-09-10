package com.liquido.core.common.exception;

/**
 * The next action after receiving the request
 */
public enum NextAction {
    /**
     * jump to next page
     */
    JUMP("jump"),

    /**
     * stay current page
     */
    STAY("stay"),

    /**
     * jump to success page
     */
    JUMP_TO_SUC("jumpToSuc"),

    /**
     * jump to fail page
     */
    JUMP_TO_FAIL("jumpToFail");

    /**
     * action
     */
    private String action;

    NextAction(final String action) {
        this.action = action;
    }

    /**
     * @param action
     * @return
     */
    public static boolean isExists(final String action) {
        for (final NextAction na : NextAction.values()) {
            if (na.action.equals(action)) {
                return true;
            }
        }
        return false;
    }

    public String getAction() {
        return action;
    }

    public String toString() {
        return this.action;
    }
}
