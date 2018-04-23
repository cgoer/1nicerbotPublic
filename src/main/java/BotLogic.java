import com.fasterxml.jackson.core.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotLogic {

    private String[] matches = new String[2];
    private String[] currencies = {"(?i)(EUR|\\€)", "(?i)JPY", "(?i)GBP", "(?i)(USD|$)", "(?i)CHF", "(?i)CAD", "(?i)CNY", "(?i)AUD"};
    private String betragString = "";

    //spiegelt den eingegebenen String
    public String mirrorMsg(String message){
        String reverse = "";
        for ( int j = message.length()-1; j >= 0; j-- )
            reverse += message.charAt(j);
        return reverse;
    }

    public String currencyExchange(String message) throws Exception {
        //sucht die keywords für BETRAG WÄHRUNG WÄHRUNG
        if(message.matches("(?i).* *.\\d+(\\,|\\.)?\\d{0,2} (EUR|JPY|GBP|USD|CHF|CAD|CNY|AUD|\\$|\\€).* *.(EUR|JPY|GBP|USD|CHF|CAD|CNY|AUD|\\$|\\€).*")) {
            String answer = "";
            try {
                ApiCall apicall = new ApiCall();
                //holt sich die exchange rate mittels api call mit dem result der methode currpairs
                double rate = apicall.call(currPairs(message, currencies));
                //vverrechnet betrag mit wechselkurs
                double erg = compute(message, rate);
                //Antwort wird zusammengeklebt
                answer = betragString + " " + matches[0] + " entsprechen " + erg + " " + matches[1] + ".";

            }catch (Exception e){
                answer = "Leider habe ich deine Wechselkursanfrage nicht verstanden. Bitte versuche folgende Satzbeispiele:\n " +
                        "\"Wie viel sind 10 EUR in USD?\"\n" +
                        "\"Ich würde gerne 25,50 USD wechseln, wieviel GBP bekomme ich dafür?\"\n" +
                        "\"Ich habe 13,25 JPY wieviel ist das in EUR?\"\n" +
                        "Unterstützte Währungen:\n" +
                        "EUR, JPY, GBP, USD, CHF, CAD, CNY, AUD";
            }
            return answer;

        }else{
            return "Leider habe ich deine Wechselkursanfrage nicht verstanden. Bitte versuche folgende Satzbeispiele:\n " +
                    "\"Wie viel sind 10 EUR in USD?\"\n" +
                    "\"Ich würde gerne 25,50 USD wechseln, wieviel GBP bekomme ich dafür?\"\n" +
                    "\"Ich habe 13,25 JPY wieviel ist das in EUR?\"\n" +
                    "Unterstützte Währungen:\n" +
                    "EUR, JPY, GBP, USD, CHF, CAD, CNY, AUD";
        }

    }
    public double compute(String message, double rate){
        int x=0;
        double amount = 0;
        double result = 0.0;
        String[] verWalter = new String[2];
        int verWalterWalter = 0;
        //sucht nach zahlen
        Pattern pattern = Pattern.compile("\\d+(\\,|\\.)?\\d{0,2}");
        Matcher matcher = pattern.matcher(message);
        //erstes ergebnis als string
        while(matcher.find() && x==0){
            betragString = matcher.group().toString();
            x++;
        }
        //check ob komma oder punkt oder ganzzahl
        if(betragString.contains(".")){
            amount = Double.parseDouble(betragString);
        }else if(betragString.contains(",")){
            //bei punkt extrahiert vor und nachkommastellen und fügt mit punkt zusammen
            Pattern patt = Pattern.compile("\\d+");
            Matcher mat = patt.matcher(betragString);
            //bastelt die ergebnisse in array
            while(mat.find() && verWalterWalter<verWalter.length){
                verWalter[verWalterWalter] = mat.group();
                verWalterWalter++;
            }
            //bastelt die ergebnisse zusammen mit punkt in double
            StringBuilder walter = new StringBuilder();
            walter.append(verWalter[0]);
            walter.append(".");
            walter.append(verWalter[1]);
            amount = Double.parseDouble(walter.toString());
        }else{
            //bei ganzzahl wird in double konvertiert
            amount = Double.parseDouble(betragString);
        }

        //zahl aus anfrage wird mit wechselkurs verrechnet
        result = amount*rate;
        result = Math.round(100.0 * result) / 100.0;

        return result;
    }
   //die texteingabe wird auf währungs schlüsselwörter untersucht, die gefundenen werden dann in ein format zur weitergabe an den apicall umgewandelt
    public String currPairs(String text, String[] regex) {
        //array für die position der währung
        int position[] = new int[2];
        //array-index
        int x = 0;
        //checkt alle währungen
        for(int i=0; i<currencies.length;i++){
            //sucht regex matches
            Pattern pattern = Pattern.compile(regex[i]);
            Matcher matcher = pattern.matcher(text);
            //gibt alle erfolgreichen matches aus
            while (matcher.find() && x<matches.length) {
                System.out.println(matcher.group());
                System.out.println(matcher.start());
                position[x]=matcher.start();
                //gibt den gefundenen währungen eine nummer
                if(matcher.group().matches("(?i)(EUR|\\€)")){
                    matches[x] = "EUR";
                }else if(matcher.group().matches("(?i)JPY")){
                    matches[x] = "JPY";
                }else if(matcher.group().matches("(?i)GBP")){
                    matches[x] = "GBP";
                }else if(matcher.group().matches("(?i)(USD|\\$)")){
                    matches[x] = "USD";
                }else if(matcher.group().matches("(?i)CHF")){
                    matches[x] = "CHF";
                }else if(matcher.group().matches("(?i)CAD")){
                    matches[x] = "CAD";
                }else if(matcher.group().matches("(?i)CNY")){
                    matches[x] = "CNY";
                }else if(matcher.group().matches("(?i)AUD")){
                    matches[x] = "AUD";
                }
                x++;
            }
        }

        if(position[0]>position[1]){
            String swapper = matches[0];
           matches[0] = matches[1];
            matches[1] = swapper;
        }
     //das array wird in ein String zusammengefasst, welcher an den API call angeheftet wird
        StringBuilder result = new StringBuilder();
        result.append(matches[0]);
        result.append("/");
        result.append(matches[1]);
         String r = result.toString();
        System.out.println(r);
        return r;
    }

}
