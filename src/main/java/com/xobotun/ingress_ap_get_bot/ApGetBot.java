package com.xobotun.ingress_ap_get_bot;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.xobotun.ingress_ap_get_bot.calculator.Calculator;
import com.xobotun.ingress_ap_get_bot.calculator.Results;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.config.Lookup;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.json.JSONException;
import org.json.JSONObject;
import spark.Request;
import spark.Response;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import static spark.Spark.*;

@Slf4j
class ApGetBot {
    static {
        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("username", "password"));
        clientBuilder.useSystemProperties();
        clientBuilder.setProxy(new HttpHost("host", 3128));
        clientBuilder.setDefaultCredentialsProvider(credsProvider);
        clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
        Lookup<AuthSchemeProvider> authProviders = RegistryBuilder.<AuthSchemeProvider>create()
                .register(AuthSchemes.BASIC, new BasicSchemeFactory())
                .build();
        clientBuilder.setDefaultAuthSchemeRegistry(authProviders);
        Unirest.setHttpClient(clientBuilder.build());
    }

    public static void main(String[] args) {
        port(8088);
        post("/", ApGetBot::processCommand);
    }

    public static String processCommand(Request request, Response response) {
        log.info(request.body());

        JSONObject body = new JSONObject(request.body());

        String text;
        long chatId;
        try {
            text = body.getJSONObject("message").getString("text");
            chatId = body.getJSONObject("message").getJSONObject("chat").getLong("id");
        } catch (JSONException e) {
            text = body.getJSONObject("edited_message").getString("text");
            chatId = body.getJSONObject("edited_message").getJSONObject("chat").getLong("id");
        }

        if (text.startsWith("/start")) {
            sendMessage(chatId, getGreetings());
            return "started";
        }
        if (text.startsWith("/help")) {
            sendMessage(chatId, getHelp());
            return "helped";
        }
        if (text.startsWith("/calc")) {
            sendMessage(chatId, calculateApDelta(text));
            return "calculated";
        }
        if (text.startsWith("/list")) {
            sendMessage(chatId, getAllEvents());
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
            val response = request.asString(); // send it
            if (response.getStatus() != 200) {
                log.warn(String.format("%d %s %s", response.getStatus(), response.getStatusText(), response.getBody()));
                log.warn(urlForLogging);
            }
        } catch (UnirestException e) {
            log.warn(String.format("Could not send request to %s", urlForLogging));
        }
    }

    private static String getHelp() {
        return "`/help` — я всегда подскажу, как ко мне обратиться. :3\n" +
               "`/calc num1 num2` — то, для чего я и была создана – считать твои действия до няфферки. Просто укажи мне два числа – и я подскажу как пройти от одного к другому. :3\n" +
               "`/calc num1 num2 mult` — если на этой неделе Нянтик ввели множитель АП, напиши его в конце, я не очень слежу за новостями. -_-'\n" +
               "`/list` — если тебе интересно, какие действия я умею учитывать при подсчёте АП до следующей няфферки.";
    }

    private static String badRequest() {
        return "Я тебя не понимаю. :(\n" +
               "Я пыталась вызвать у тебя /help, но ты, похоже, не бот...";
    }

    private static String getGreetings() {
        return "Ой, привет! Я – НяфферкоБот, я умею считать АП до следующей няфферки и люблю это делать. Просто скажи мне твоё текущее АП и я скажу тебе, что тебе надо сделать до следующей няфферки. :3\n" +
               "А ещё я умею делать всякое другое:\n\n" +
                getHelp();
    }

    private static String calculateApDelta(String text) {
        String[] tokens = text.replaceAll("\\s{2}", " ").trim().split(" ");     // never can be too much escaping

        if (tokens.length < 3)
            return "Мне надо посчитать разницу между текущим АП и требуемым, а их пришло как-то мало. :(";

        //#region validate input
        long apCountLesser;
        long apCountGreater;
        try {
            apCountLesser = Long.valueOf(tokens[1]);
            apCountGreater = Long.valueOf(tokens[2]);
        } catch (NumberFormatException e) {
            return "Эм-м-м... Кажется, тут интернет барахлит, мне почему-то вместо АП пришли ещё какие-то другие символы.";
        }

        if (apCountGreater < apCountLesser) {
            long tmp = apCountGreater;
            apCountGreater = apCountLesser;
            apCountLesser = tmp;
        }

        double multiplier = 1.0;
        if (tokens.length == 4) {
            try {
                multiplier = NumberFormat.getInstance(Locale.ROOT).parse(tokens[3].replaceAll(",", ".")).doubleValue();
            } catch (ParseException e) {
                // Do nothing: default value is already set and this code succesfully parses 95% input.
            }
        }
        // #endregion

        Results result = Calculator.calculate(apCountLesser, apCountGreater, multiplier);
        StringBuilder response = new StringBuilder(
                String.format("Итак, у тебя сейчас `%d` АП и ты хочешь няфферку `%d` АП. Тебе надо получить `%d` АП! :3.\n\n",
                        apCountLesser,
                        apCountGreater,
                        apCountGreater - apCountLesser
                )
        );

        if (result.getApNotDistributed() > 0) {
            int lastDigitNow = (int) apCountLesser % 5;
            int lastDigitShouldBe = (lastDigitNow + (int) result.getApNotDistributed() % 5) % 5;
            if (lastDigitShouldBe == apCountLesser % 10) lastDigitShouldBe += 10;   // In case there is x2 multiplier and there is no way to get 10 AP.
            response.append(String.format("Во-первых, поглифуй порталы, пока твоё АП не будет заканчиваться на цифру `%d` или `%d`.\n" +
                                          "А потом обратись ко мне, я тебе пересчитаю шаги до няфферки. Я люблю это делать! :3\n" +
                                          "Твой план будет такой:\n\n", lastDigitShouldBe, lastDigitShouldBe + 5));
        } else {
            response.append("Делай так и у тебя всё получится!\n");
        }

        final double finalMultiplier = multiplier;
        result.getNumOfEvents().entrySet().stream()
                .sorted(Comparator.comparingInt(entry -> ((Map.Entry<ApEvent, Integer>)entry).getKey().getIncreaseAmount()).reversed())
                .forEach(countedApEvent -> {
                    int apIncrease = (int) (countedApEvent.getKey().getIncreaseAmount() * finalMultiplier);

                    response.append(String.format("• %s `%d` %s, чтобы получить `%d` АП (`%d` за штуку)\n",
                            ApEvent.getCalculationDescription(countedApEvent.getKey()),
                            countedApEvent.getValue(),
                            getTimesDeclination(countedApEvent.getValue()),
                            apIncrease * countedApEvent.getValue(),
                            apIncrease));
                });

        return response.toString();
    }

    private static String getAllEvents() {
        StringBuilder response = new StringBuilder("Я считаю только эти события, чтобы всем было проще получить заветную няфферку:\n");

        Arrays.stream(ApEvent.values())
                .filter(ApEvent::isUse)
                .sorted(Comparator.comparingInt(ApEvent::getIncreaseAmount).reversed())
                .map(event -> String.format("• %s даёт `%d` АП\n", ApEvent.getListDescription(event), event.getIncreaseAmount()))
                .forEach(response::append);

        response.append("\nА вообще, АП тебе принесут ещё и более сложноанализируемые действия, только не переусердствуй и не перескочи няфферку. :3\n");

        Arrays.stream(ApEvent.values())
                        .filter(event -> !event.isUse())
                        .sorted(Comparator.comparingInt(ApEvent::getIncreaseAmount).reversed())
                        .map(event -> String.format("• %s даёт `%d` АП\n", ApEvent.getListDescription(event), event.getIncreaseAmount()))
                        .forEach(response::append);

        return response.toString();
    }

    private static String getTimesDeclination(int times) {
        if (times % 100 != 12 &&
                (times % 10 == 2 ||
                 times % 10 == 3 ||
                 times % 10 == 4)
           )
            return "раза";
        else
            return "раз";
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
