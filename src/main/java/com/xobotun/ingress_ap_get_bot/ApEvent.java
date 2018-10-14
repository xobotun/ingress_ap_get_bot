package com.xobotun.ingress_ap_get_bot;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Lists some ap raising events from http://ingress.wikia.com/wiki/Access_Points
 */
@AllArgsConstructor
@Getter
public enum ApEvent {
    PORTAL_RECHARGE(10, false, "Recharge green portal"),
    RESONATOR_UPGRADE(65, true, "Upgrade other's resonator"),
    HACK(100, true, "Simply hack blue portal"),
    RESONATOR_DEPLOY(125, true, "Deploy a single resonator"),
    MOD_INSTALL(125, true, "Install any mod"),
    CREATE_LINK(313, true, "Create a link"),
    FIRST_RESONATOR_DEPLOY(500 + 125, true, "Capture gray portal"),
    CONTROL_FIELD_CREATE(1250 + 313, true, "Create control field"),   // with at least one link
    PORTAL_CAPTURE(500 + 325 + 125 * 8, true, "Fill all nodes with resonators");
    // Hard to use IRL, decided to exclude them.
//    LAST_RESONATOR_DEPLOY(325 + 125);
//    CONTROL_FIELD_DESTROY(750),
//    LINK_DESTROY(187),

    /**
     * How much users AP should increase under normal circumstanses.
     */
    int increaseAmount;
    /**
     * Whether to decrease amount of actions of this kind to reduce dullness.
     */
    boolean applySieve;
    /**
     * Action description to write in the beginning of a sentence.
     */
    String description;
}
