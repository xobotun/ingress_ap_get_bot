package com.xobotun.ingress_ap_get_bot;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Lists some ap raising events from http://ingress.wikia.com/wiki/Access_Points
 */
@AllArgsConstructor
@Getter
public enum ApEvent {
    PORTAL_RECHARGE(10, false),
    RESONATOR_UPGRADE(65, true),
    HACK(100, true),
    RESONATOR_DEPLOY(125, true),
    MOD_INSTALL(125, true),
    CREATE_LINK(313, true),
    FIRST_RESONATOR_DEPLOY(500 + 125, true),
    CONTROL_FIELD_CREATE(1250 + 313, true),   // with at least one link
    PORTAL_CAPTURE(500 + 325 + 125 * 8, true);
    // Hard to use IRL, decided to exclude them.
//    LAST_RESONATOR_DEPLOY(325 + 125);
//    CONTROL_FIELD_DESTROY(750),
//    LINK_DESTROY(187),

    /**
     * How much users AP should increase under normal circumstanses.
     */
    int increaseAmount;
    boolean applySieve;
}
