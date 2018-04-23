import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.regex.Pattern;

public class EchoBot extends TelegramLongPollingBot {

    public void onUpdateReceived(Update update) {

        if (update.hasMessage() &&
                update.getMessage().hasText()) {

            String response = null;
            try {
                response = getResponse(update.getMessage().getText());
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!response.isEmpty()) {
                SendMessage message = new SendMessage()
                        .setChatId(update.getMessage().getChatId())
                        .setText(response);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    public String getResponse(String message) throws Exception {
        if (message.matches("(?i)echo: .*")) {
            //Wiederholt die nachricht nach dem keyword
            return message.substring(6);
        }else if(message.matches("(?i)mirror: .*")){
            //führt die MirrorMsg methode aus und gibt den inhalt zurück
            BotLogic botLogic = new BotLogic();
            return botLogic.mirrorMsg(message.substring(8));
            //return message.substring(8);
        }else if(message.matches("(?i)legende")){
            //zeigt die legende
            return "Legende: \n \"mirror:\"  Spiegelt den Text nach dem Schlüsselwort \n \"echo:\"  Wiederholt die Nachricht nach dem Schlüsselwort \n\n" +
                    "Währungsrechner beispiele: \n" +
                    "\"Wie viel sind 10 EUR in USD?\"\n" +
                    "\"Ich würde gerne 25,50 USD wechseln, wieviel GBP bekomme ich dafür?\"\n" +
                    "\"Ich habe 13,25 JPY wieviel ist das in EUR?\"\n" +
                    "Unterstützte Währungen:\n" +
                    "EUR, JPY, GBP, USD, CHF, CAD, CNY, AUD";
        }else if(message.matches("(?i)(Wieviel|wie viel) (sind|ist) .* *.in.* *.?") || message.matches("(?i)ich habe .* *.(wieviel|wie viel).* *.?") ||
                message.matches("(?i)Ich (würde|möchte|will) .* *.wechseln.* *.(wieviel|wie viel).* *.?")){
            BotLogic botLogic = new BotLogic();
            return botLogic.currencyExchange(message);
        }
        //Falls keine verständlichen eingaben gemacht wurden, kommt diese msg.
        return "Ich habe dich leider nicht verstanden. \n Schreibe \"legende\" um die Legende anzuzeigen.";
    }


    public String getBotUsername() {
        return "PUTIN BOTUSERNAME HERE";
    }

    public String getBotToken() {
        return "PUTIN BOTTOKEN HERE";
    }
}
