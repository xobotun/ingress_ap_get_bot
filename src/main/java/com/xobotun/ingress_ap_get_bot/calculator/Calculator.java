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
    public static Results calculate(long apNow, long apDesired) {
        final AtomicLong apRemaining = new AtomicLong(apDesired - apNow);
        final Map<ApEvent, Integer> result = new EnumMap<>(ApEvent.class);

        Arrays.stream(ApEvent.values())
                .filter(apEvent -> true)    // There may be some options in the future...
                .sorted(Comparator.comparingInt(ApEvent::getIncreaseAmount).reversed())
                .forEach(apEvent -> {
                    if (apEvent.getIncreaseAmount() > apRemaining.get())
                        return;

                    int timesFitInDelta = apRemaining.intValue() / apEvent.getIncreaseAmount();
                    int sievedTimes;
                    // If it fits perfectly, do not sieve it. Be perfectionist. :)
                    if (timesFitInDelta * apEvent.getIncreaseAmount() == apRemaining.intValue())
                        sievedTimes = timesFitInDelta;
                    else
                        sievedTimes = sieveApEvents(apEvent, timesFitInDelta, SIMILAR_ACTIONS_SIEVE_PERCENTAGE);


                    long nextApRemaining = apRemaining.get() - sievedTimes * apEvent.getIncreaseAmount();
                    int lastDigitBefore = apRemaining.intValue() % 10;
                    int lastDigitAfter = (int)nextApRemaining % 10;

                    // We shall not reduce accuracy. I.e. If user has 400 apRemaining and we tell him to cast one link for 313 ap,
                    // he will freak out with remaining 87 ap. Let's not do that.
                    if (lastDigitAfter <= lastDigitBefore) {
                        result.put(apEvent, sievedTimes);
                        apRemaining.set(nextApRemaining);
                    }
                });

        return new Results(result, apRemaining.get());
    }

    private static int sieveApEvents(ApEvent event, int amount, int sievePercentage) {
        if (amount > 1 && event.isApplySieve()) {
            amount = sievePercentage * amount / 100;   // I hope there won't be number of attempts greater than 0x7FFFFFFF / sievePercentage. %)
        }
        return amount;
    }
}
