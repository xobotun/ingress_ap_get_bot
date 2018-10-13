package com.xobotun.ingress_ap_get_bot;

import com.xobotun.ingress_ap_get_bot.calculator.Calculator;
import lombok.val;

import static spark.Spark.*;

class ApGetBot {
    public static void main(String[] args) {
        val tmp = Calculator.calculate(4_547_431, 4567890);

        get("/", ((request, response) -> response));
    }
}