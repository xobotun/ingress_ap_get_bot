package com.xobotun.ingress_ap_get_bot;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Lists some ap raising events from http://ingress.wikia.com/wiki/Access_Points
 */
@AllArgsConstructor
@Getter
public enum ApEvent {
    PORTAL_RECHARGE(10),
    RESONATOR_UPGRADE(65),
    HACK(100),
    RESONATOR_DEPLOY(125),
    MOD_INSTALL(125),
    CREATE_LINK(313),
    FIRST_RESONATOR_DEPLOY(500 + 125),
    CONTROL_FIELD_CREATE(1250),
    PORTAL_CAPTURE(500 + 325 + 125 * 8);
    // Hard to use IRL, decided to exclude them.
//    LAST_RESONATOR_DEPLOY(325 + 125);
//    CONTROL_FIELD_DESTROY(750),
//    LINK_DESTROY(187),

    /**
     * How much users AP should increase under normal circumstanses.
     */
    int increaseAmount;
}
