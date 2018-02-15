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
    public static void main(String[] args) {
        lueJunanJSONData();
    }
    public static String syöteLahto() {
        Scanner lukija = new Scanner(System.in);
        // Käsitellään lähtöäaseman syöte pieniksi kirjaimiksi.
        System.out.println("Anna lähtöasema:");
        String kayttajanLahtoAsema = lukija.nextLine().toLowerCase();
        String kayttajanLahtoAsemaEkaKirjain = kayttajanLahtoAsema.substring(0, 1).toUpperCase();
        String kayttajanLahtoAsemaEkaIsolla = kayttajanLahtoAsemaEkaKirjain + kayttajanLahtoAsema.substring(1);
        return kayttajanLahtoAsemaEkaIsolla;
    }
    public static String syötePääte() {
        Scanner lukija = new Scanner(System.in);
        // Käsitellään pääteäaseman syöte pieniksi kirjaimiksi.
        System.out.println("Anna pääteasema:");
        String kayttajanPaateAsema = lukija.nextLine().toLowerCase();
        String kayttajanPaateAsemaEkaKirjain = kayttajanPaateAsema.substring(0, 1).toUpperCase();
        String kayttajanPaateAsemaEkaIsolla = kayttajanPaateAsemaEkaKirjain + kayttajanPaateAsema.substring(1);
        return kayttajanPaateAsemaEkaIsolla;
    }
    private static void lueJunanJSONData() {
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        Scanner lukija = new Scanner(System.in);
        String annettuLahtoAsema = syöteLahto();
        String annettuPaateAsema = syötePääte();

        // Kysytään käyttäjältä lähtöaikaa
        System.out.println("Anna tunnit!");
        int annetutTunnit = lukija.nextInt();

        while(annetutTunnit < 0|| annetutTunnit > 23) {
            System.out.println("Anna tunti väliltä 0-23.");
            System.out.println("Anna tunnit!");
            annetutTunnit = lukija.nextInt();
        }

        System.out.println("Anna minuutit!");
        int annetutMinuutit = lukija.nextInt();

        while(annetutMinuutit < 0 || annetutMinuutit > 59) {
            System.out.println("Anna minuutit väliltä 0-59.");
            System.out.println("Anna minuutit!");
            annetutMinuutit = lukija.nextInt();
        }

        // Alustettu tyhjillä merkkijonoilla, jotta URL ei herjaa
        String lahtoAsemaLyhenne= "";
        String paateAsemaLyhenne = "";

        // Käsitellään käyttäjän antamaa aikaa
        Calendar kalenteri = new GregorianCalendar();
        kalenteri.set(Calendar.MINUTE, annetutMinuutit);
        kalenteri.set(Calendar.HOUR_OF_DAY, annetutTunnit);
        System.out.println("Haetaan junia ajasta " + kalenteri.getTime() +" eteenpäin." + "\n");
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
                System.out.println("Ookko nää tyhymä vai ekkä nää vaa ossaa kirjottaa lähtöasemaa? Kokeiles uudestaan:");
                System.out.println("");
                lueJunanJSONData();
            }

            // Haetaan käyttäjän syötteen mukaista pääteasemaa listalta
            int annetunPaateAsemanIndeksi = -1;
            for (int i = 0; i < asemat.size(); i++) {
                if (asemat.get(i).getStationName().startsWith(annettuPaateAsema)) {
                    annetunPaateAsemanIndeksi = i;
                    break;
                }
            }
            // Antaa indeksiä ja aseman koko nimeä vastaavan lyhenteen
            paateAsemaLyhenne = asemat.get(annetunPaateAsemanIndeksi).getStationShortCode();

            if (paateAsemaLyhenne.equals("AHO")) {
                System.out.println("Ookko nää tyhymä vai ekkä nää vaa ossaa kirjottaa pääteasemaa? Turha luulo et me sua palveltais!!!");
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

            // Etsitään käyttäjän syötettä vastaavia junalähtöjä ja tulostetaan vaihtoehdot
            int lahtevaJuna;
            int loydettyja = 0;
            ulompi:

            for (int i = 0; i < junat.size(); i++) {
                for (int j = 0; j < junat.get(i).getTimeTableRows().size() ; j++) {
                    if (junat.get(i).getTimeTableRows().get(j).getStationShortCode().equals(lahtoAsemaLyhenne) && junat.get(i).getTimeTableRows().get(j).getType().equals("DEPARTURE") && junat.get(i).getTimeTableRows().get(j).getCommercialStop() && junat.get(i).getTimeTableRows().get(j).getScheduledTime().after(aika2)){
                        System.out.print("Lähtöaika: " + "\t" +"\t" +"\t" +"\t" +"\t"+"\t"+"\t"+"\t"+ junat.get(i).getTimeTableRows().get(j).getScheduledTime());
                        for (int k = j+1; k < junat.get(i).getTimeTableRows().size(); k++) {
                            if(junat.get(i).getTimeTableRows().get(k).getStationShortCode().equals(paateAsemaLyhenne)){
                                System.out.println("\n"+"Saapumisaika: " +"\t" +"\t"+"\t"+"\t"+"\t" +"\t"+"\t" +junat.get(i).getTimeTableRows().get(k).getScheduledTime());
                                System.out.println("Valitse tämä matka indeksistä " + i +"." + "\t"+ "\t"+ "Matka-aika on " + (((double)junat.get(i).getTimeTableRows().get(k).getScheduledTime().getTime() - junat.get(i).getTimeTableRows().get(j).getScheduledTime().getTime())/60000) + " minuuttia.");
                                System.out.println();
                                loydettyja++;
                                if(loydettyja>=5){
                                    break ulompi;

                                }break;
                            }

                        }


                    }
                }
            }


            // Kysytään millä junalla käyttäjä haluaa matkustaa
            System.out.println("Anna valitsemasi junan indeksi: ");
            lahtevaJuna = lukija.nextInt();

            // ottaa talteen saapuvan juna-aseman indeksin ja tulostaa tarvittavat tiedot
            for (int i = 0; i < junat.get(lahtevaJuna).getTimeTableRows().size(); i++) {
                if (junat.get(lahtevaJuna).getTimeTableRows().get(i).getStationShortCode().equals(paateAsemaLyhenne)) {
                    System.out.println("Saavut paikkaan: " + annettuPaateAsema);
                    System.out.println("Saapumisaika: " + junat.get(lahtevaJuna).getTimeTableRows().get(i).getScheduledTime());
                    break;
                }

            }
            int saapuvaJuna;
            int loytyy = 0;
            Collections.reverse(junat);
            saapuva:
            for (int i = 0; i < junat.size(); i++) {
                //for (int i = junat.size(); i > junat.size(); i--) {
                Juna nykyinenJuna = junat.get(i);
                List<TimeTableRow> nykyisetAikataulurivit = nykyinenJuna.getTimeTableRows();
                for (int j = 0; j < nykyisetAikataulurivit.size(); j++) {
                    //for (int j = junat.get(i).getTimeTableRows().size(); j > junat.get(i).getTimeTableRows().size(); j--) {
                    TimeTableRow nykyinenRivi = nykyisetAikataulurivit.get(j);
                    if (nykyinenRivi.getStationShortCode().equals(paateAsemaLyhenne) && nykyinenRivi.getType().equals("ARRIVAL") && nykyinenRivi.getScheduledTime().before(aika2)) {

                        System.out.println("Aikataulun mukainen saapumisaika: " + nykyinenRivi.getScheduledTime() + "Junan numero: " + nykyinenJuna.getTrainNumber() + " indeksi" + i);
                        loytyy++;
                        if (loytyy >= 5) {
                            break saapuva;
                        }

                    }
                }
            }

            System.out.println("Anna haluamasi junan indeksi: ");
            saapuvaJuna = lukija.nextInt();

//            for (int i = 0; i < junat.size() ; i++) {
//                if(junat.get(i).getTimeTableRows().get)
//            }
//
            // ottaa talteen saapuvan juna-aseman indeksin ja tulostaa tarvittavat tiedot
            for (int i = 0; i < junat.get(saapuvaJuna).getTimeTableRows().size(); i++) {
                if (junat.get(saapuvaJuna).getTimeTableRows().get(i).getStationShortCode().equals(lahtoAsemaLyhenne) && junat.get(saapuvaJuna).getTimeTableRows().get(i).getType().equals("DEPARTURE")) {
                    System.out.println("Lähtöasema: " + junat.get(saapuvaJuna).getTimeTableRows().get(i).getStationShortCode());
                    System.out.println("Junan lähtöaika: " + junat.get(saapuvaJuna).getTimeTableRows().get(i).getScheduledTime());
                    break;
                }
            }

        } catch (MismatchedInputException e){
            System.out.println("Hyvää päivänjatkoa!");
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
