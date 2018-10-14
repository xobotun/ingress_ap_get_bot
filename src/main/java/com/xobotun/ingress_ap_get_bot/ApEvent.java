package com.xobotun.ingress_ap_get_bot;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Lists some ap raising events from http://ingress.wikia.com/wiki/Access_Points
 */
@AllArgsConstructor
@Getter
public enum ApEvent {
    PORTAL_RECHARGE(10, false, true),
    RESONATOR_UPGRADE(65, true, true),
    HACK(100, true, true),
    RESONATOR_DEPLOY(125, true, true),
    MOD_INSTALL(125, true, true),
    CREATE_LINK(313, true, false),
    FIRST_RESONATOR_DEPLOY(500 + 125, true, true),
    CONTROL_FIELD_CREATE(1250 + 313, true, false),   // with at least one link
    PORTAL_CAPTURE(500 + 250 + 125 * 8, true, true),
    LAST_RESONATOR_DEPLOY(250 + 125, true, false),
    CONTROL_FIELD_DESTROY(750, true, false),
    LINK_DESTROY(187, true, false);

    /**
     * How much users AP should increase under normal circumstanses.
     */
    int increaseAmount;
    /**
     * Whether to decrease amount of actions of this kind to reduce dullness.
     */
    boolean applySieve;
    /**
     * Whether to use in counting.
     */
    boolean use;

    public static String getCalculationDescription(ApEvent event) {
        if (event == PORTAL_RECHARGE) return "Перезаряди портал";
        if (event == RESONATOR_UPGRADE) return "Апни резонатор";
        if (event == HACK) return "Хакни вражеский портал";
        if (event == RESONATOR_DEPLOY) return "Поставь резонатор";
        if (event == MOD_INSTALL) return "Поставь мод";
        if (event == FIRST_RESONATOR_DEPLOY) return "Захвати портал";
        if (event == PORTAL_CAPTURE) return "Полностью заставь весь портал резонаторами";

        return String.format("Ой, я что-то запуталась. Здесь не должно быть %s!", event.name());
    }

    public static String getListDescription(ApEvent event) {
        if (event == PORTAL_RECHARGE) return "Перезарядка портала";
        if (event == RESONATOR_UPGRADE) return "Апгрейд резонатора";
        if (event == HACK) return "Взлом вражеского портала";
        if (event == RESONATOR_DEPLOY) return "Деплой резонатора";
        if (event == MOD_INSTALL) return "Установка мода";
        if (event == FIRST_RESONATOR_DEPLOY) return "Захват портала одним резонатором";
        if (event == PORTAL_CAPTURE) return "Установка всех резонаторов в портал суммарно";

        if (event == CREATE_LINK) return "Линковка";
        if (event == CONTROL_FIELD_CREATE) return "Наведение поля";
        if (event == LAST_RESONATOR_DEPLOY) return "Установка последнего резонатора";
        if (event == CONTROL_FIELD_DESTROY) return "Снос вражеского поля";
        if (event == LINK_DESTROY) return "Снос вражеского линка";

        return String.format("Ой, я что-то запуталась. Здесь не должно быть %s!", event.name());
    }
}
