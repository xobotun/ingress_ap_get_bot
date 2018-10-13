package com.xobotun.ingress_ap_get_bot.calculator;

import com.xobotun.ingress_ap_get_bot.ApEvent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Calculator {
    /**
     * As repeating `capture portal` action 40 times in a row may become a bit dull,
     * lets leave only some of these repetitive actions.
     * With 0.8 coefficient and 40 of the same actions there will only 32 remaining.
     */
    public static final int SIMILAR_ACTIONS_SIEVE_PERCENTAGE = 80;

    /**
     * Calculates plan for achieveing desired AP value.
     * {@code apNow} should always be lesser or equal to {@code apDesired}
     * @param apNow AP count user has right now
     * @param apDesired AP count user wants to reach
     * @return {@link Results} containing detailed plan on how to achieve users goal.
     */
    public Results calculate(long apNow, long apDesired) {
        final AtomicLong delta = new AtomicLong(apDesired - apNow);
        final Map<ApEvent, Integer> result = new EnumMap<>(ApEvent.class);

        Arrays.stream(ApEvent.values())
                .filter(apEvent -> true)    // There may be some options in the future...
                .sorted(Comparator.comparingInt(ApEvent::getIncreaseAmount).reversed())
                .peek(apEvent -> {

                });

        return new Results(result, delta.get());
    }

}
