package com.xobotun.ingress_ap_get_bot.calculator;

import com.xobotun.ingress_ap_get_bot.ApEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 *  {@link #numOfEvents} lists number of specific actions required to readh desired AP count.
 *  {@link #apNotDistributed} lists how much AP user has to hack with glyphs for his current AP
 *  count to become incrementable by {@link ApEvent} events. In the most cases last digit of
 *  delta (apDesired - apNow) has to be divisable by 5 (almost all actions) or 3 (link creation).
 */
@AllArgsConstructor
@Getter
public class Results {
    Map<ApEvent, Integer> numOfEvents;
    long apNotDistributed;
}
