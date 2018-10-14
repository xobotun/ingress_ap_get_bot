package com.xobotun.ingress_ap_get_bot;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.xobotun.ingress_ap_get_bot.calculator.Calculator;
import com.xobotun.ingress_ap_get_bot.calculator.Results;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.util.Comparator;

import static spark.Spark.*;

@Slf4j
class ApGetBot {
    public static void main(String[] args) {
        port(8088);
        post("/", ApGetBot::processCommand);
    }

    public static String processCommand(Request request, Response response) {
        JSONObject body = new JSONObject(request.body());

        String text = body.getJSONObject("message").getString("text");
        long chatId = body.getJSONObject("message").getJSONObject("chat").getLong("id");
        String username = body.getJSONObject("message").getJSONObject("from").getString("username");

        if (text.startsWith("/help")) {
            sendMessage(chatId, getHelp());
            return "helped";
        }
        if (text.startsWith("/calc")) {
            sendMessage(chatId, calculateApDelta(text));
            return "calculated";
        }

        sendMessage(chatId, badRequest());
        return "bad request";
    }

    private static void sendMessage(long chatId, String text) {
        String token = "691111851:AAEKsz5at1ezfixJbenXV8OCEM5BVhRpiKw";
        String urlForLogging = "null";
        try {
        val request = Unirest.get(String.format("https://api.telegram.org/bot%s/sendMessage", token))
                .queryString("chat_id", Long.toString(chatId))
                .queryString("text", text)
                .queryString("parse_mode", "Markdown");

        urlForLogging = request.getUrl();
        request.asString(); // send it
        } catch (UnirestException e) {
            log.info(String.format("Could not send request to %s", urlForLogging));
        }
    }

    private static String getHelp() {
        return "`/help` — returns this message\n" +
               "`/calc num1 num2` — calculates delta between `num1` and `num2` and tells you what to do.";
    }

    private static String badRequest() {
        return "I don't know how to respond to your request. :(";
    }

    private static String calculateApDelta(String text) {
        String[] tokens = text.replaceAll("\\s{2}", " ").trim().split(" ");     // never can be too much escaping

        //#region validate input
        long apCountLesser;
        long apCountGreater;
        try {
            apCountLesser = Long.valueOf(tokens[1]);
            apCountGreater = Long.valueOf(tokens[2]);
        } catch (NumberFormatException e) {
            return "Just for you to know, AP is represented by digits only.";
        }

        if (apCountGreater < apCountLesser) {
            long tmp = apCountGreater;
            apCountGreater = apCountLesser;
            apCountLesser = tmp;
        }
        // #endregion

        Results result = Calculator.calculate(apCountLesser, apCountGreater);
        StringBuilder response = new StringBuilder(
                String.format("You want to increase your AP from `%d` to `%d` by `%d`.\n\n",
                        apCountLesser,
                        apCountGreater,
                        apCountGreater - apCountLesser
                )
        );

        if (result.getApNotDistributed() > 0) {
            int lastDigitShouldBe = (int) (apCountLesser % 10 + result.getApNotDistributed()) % 10;
            response.append(String.format("First, consider glyphing until your AP ends with digit `%d`.\n" +
                                          "Anyway, your next steps will be like:\n\n", lastDigitShouldBe));
        } else {
            response.append("You just need to\n\n");
        }

        result.getNumOfEvents().entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> entry.getKey().getIncreaseAmount()))
                .forEach(countedApEvent -> {
                    response.append(String.format("%s `%d` times for `%d` AP gain\n",
                            countedApEvent.getKey().getDescription(),
                            countedApEvent.getValue(),
                            countedApEvent.getKey().getIncreaseAmount() * countedApEvent.getValue()));
                });



        return response.toString();
    }
}

//{
//  "update_id": 292501094,
//  "message": {
//    "message_id": 7,
//    "from": {
//      "id": 279964437,
//      "is_bot": false,
//      "first_name": "Paul Maminov",
//      "last_name": "@Xobotun",
//      "username": "Xobotun",
//      "language_code": "en-US"
//    },
//    "chat": {
//      "id": 279964437,
//      "first_name": "Paul Maminov",
//      "last_name": "@Xobotun",
//      "username": "Xobotun",
//      "type": "private"
//    },
//    "date": 1539500523,
//    "text": "/help",
//    "entities": [
//      {
//        "offset": 0,
//        "length": 5,
//        "type": "bot_command"
//      }
//    ]
//  }
//}