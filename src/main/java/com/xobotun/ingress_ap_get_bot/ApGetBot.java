package com.xobotun.ingress_ap_get_bot;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

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
            sendMessage(chatId, "Not Implemented");
            return "calculated";
        }

        sendMessage(chatId, badRequest());
        return "bad request";
    }

    private static void sendMessage(long chatId, String text) {
        String token = "no-token";
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