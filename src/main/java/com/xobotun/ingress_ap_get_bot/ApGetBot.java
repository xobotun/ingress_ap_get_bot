package com.xobotun.ingress_ap_get_bot;

import static spark.Spark.*;

class ApGetBot {
    public static void main(String[] args) {
        get("/", ((request, response) -> response));
    }
}