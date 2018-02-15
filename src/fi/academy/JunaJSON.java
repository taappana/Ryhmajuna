package fi.academy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.net.URI;
import java.net.URL;
import java.util.*;

public class JunaJSON {

    public static void lueJunanJSONData() {

        String baseurl = "https://rata.digitraffic.fi/api/v1";
        Scanner lukija = new Scanner(System.in);

        Metodit.valittuLahtoTaiSaapuminen();

        String annettuLahtoAsema = Metodit.syöteLahto();
        String annettuPaateAsema = Metodit.syötePääte();

        // Kysytään käyttäjältä lähtöaikaa
        System.out.print("\nAnna tunnit: ");
        int annetutTunnit = lukija.nextInt();

        while(annetutTunnit < 0|| annetutTunnit > 23) {
            System.out.println("\nAnna tunti väliltä 0-23. \n");
            System.out.print("Anna tunnit: ");
            annetutTunnit = lukija.nextInt();
        }

        System.out.print("Anna minuutit: ");
        int annetutMinuutit = lukija.nextInt();

        while(annetutMinuutit < 0 || annetutMinuutit > 59) {
            System.out.println("\nAnna minuutit väliltä 0-59. \n");
            System.out.print("Anna minuutit: ");
            annetutMinuutit = lukija.nextInt();
        }

        // Alustettu tyhjillä merkkijonoilla, jotta URL ei herjaa
        String lahtoAsemaLyhenne= "";
        String paateAsemaLyhenne = "";

        // Käsitellään käyttäjän antamaa aikaa
        Calendar kalenteri = new GregorianCalendar();
        kalenteri.set(Calendar.MINUTE, annetutMinuutit);
        kalenteri.set(Calendar.HOUR_OF_DAY, annetutTunnit);
        // System.out.println("Haetaan junia ajasta " + kalenteri.getTime() +" eteenpäin." + "\n");
        System.out.println("Haetaan junia: " + kalenteri.getTime());
        System.out.println("\n" +
                "|￣￣￣￣￣￣￣￣| \n" +
                "| Haetaan     | \n" +
                "| aikatauluja |\n" +
                "|＿＿＿＿＿＿＿＿| \n" +
                "(\\__/)|| \n" +
                "(•ㅅ•) || \n" +
                "/ 　 づ" + "\n");
        Date aika2 = kalenteri.getTime();

        // Käsitellään asemadataa. Saadaan aseman kokonimi ja sitä vastaava lyhenne
        try {
            URL url = new URL("https://rata.digitraffic.fi/api/v1/metadata/stations");
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Asemat.class);
            List<Asemat> asemat = mapper.readValue(url, tarkempiListanTyyppi);

            // Haetaan käyttäjän syötteen mukaista lähtöasemaa listalta
            int annetunLahtoAsemanIndeksi = 0;
            for (int i = 0; i < asemat.size(); i++) {
                if (asemat.get(i).getStationName().startsWith(annettuLahtoAsema)) {
                    annetunLahtoAsemanIndeksi = i;
                    break;
                }
            }
            // Antaa indeksiä ja aseman koko nimeä vastaavan lyhenteen
            lahtoAsemaLyhenne = asemat.get(annetunLahtoAsemanIndeksi).getStationShortCode();

            if (lahtoAsemaLyhenne.equals("AHO")) {
                System.out.println("Ookko nää tyhymä vai ekkä nää vaa ossaa kirjottaa lähtöasemaa? Kokeiles uudestaan.");
                System.out.println("");
                lueJunanJSONData();
            }

            // Haetaan käyttäjän syötteen mukaista pääteasemaa listalta
            int annetunPaateAsemanIndeksi = 0;
            for (int i = 0; i < asemat.size(); i++) {
                if (asemat.get(i).getStationName().startsWith(annettuPaateAsema)) {
                    annetunPaateAsemanIndeksi = i;
                    break;
                }
            }
            // Antaa indeksiä ja aseman koko nimeä vastaavan lyhenteen
            paateAsemaLyhenne = asemat.get(annetunPaateAsemanIndeksi).getStationShortCode();

            if (paateAsemaLyhenne.equals("AHO")) {
                System.out.println("Ookko nää tyhymä vai ekkä nää vaa ossaa kirjottaa pääteasemaa? Kokeiles uudestaan.");
                lueJunanJSONData();
            }

        } catch (Exception exe) {
            System.out.println(exe);
        }

        // Käsitellään junadataa. Muokataan haettavaa URL-osoitetta käyttäjän syöttämien lähtö- ja pääteasemine mukaan
        try {
            URL Junaurl = new URL(URI.create(baseurl + "/live-trains/station/" + lahtoAsemaLyhenne + "/" + paateAsemaLyhenne).toASCIIString());
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(Junaurl, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi
            // LÄHTÖAJAN MUKAAN:
            if(Metodit.kayttajanValinta.equalsIgnoreCase("1")) {
                Metodit.junatLahtoajanMukaan(lukija, annettuPaateAsema, lahtoAsemaLyhenne, paateAsemaLyhenne, aika2, junat);
            }
            // SAAPUMISAJAN MUKAAN:
            if(Metodit.kayttajanValinta.equalsIgnoreCase("2")) {
                Metodit.junatSaapumisajanMukaan(lukija, lahtoAsemaLyhenne, paateAsemaLyhenne, aika2, junat);
            }
        } catch (MismatchedInputException e){
            System.out.println("Hyvää päivänjatkoa!");
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
