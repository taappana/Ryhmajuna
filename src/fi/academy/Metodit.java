package fi.academy;

import java.util.*;

public class Metodit {
    static String kayttajanValinta;

    public static String syöteLahto() {
        Scanner lukija = new Scanner(System.in);
        // Käsitellään lähtöäaseman syöte pieniksi kirjaimiksi.
        System.out.print("Anna lähtöasema: ");
        String kayttajanLahtoAsema = lukija.nextLine().toLowerCase();
        String kayttajanLahtoAsemaEkaKirjain = kayttajanLahtoAsema.substring(0, 1).toUpperCase();
        String kayttajanLahtoAsemaEkaIsolla = kayttajanLahtoAsemaEkaKirjain + kayttajanLahtoAsema.substring(1);
        return kayttajanLahtoAsemaEkaIsolla;
    }
    public static String syötePääte() {
        Scanner lukija = new Scanner(System.in);
        // Käsitellään pääteäaseman syöte pieniksi kirjaimiksi.
        System.out.print("Anna pääteasema: ");
        String kayttajanPaateAsema = lukija.nextLine().toLowerCase();
        String kayttajanPaateAsemaEkaKirjain = kayttajanPaateAsema.substring(0, 1).toUpperCase();
        String kayttajanPaateAsemaEkaIsolla = kayttajanPaateAsemaEkaKirjain + kayttajanPaateAsema.substring(1);
        return kayttajanPaateAsemaEkaIsolla;
    }

    public static String randomSaatila() {
        List<String> saatila = new ArrayList<String>();
        saatila.add("harvinaisen aurinkoista");
        saatila.add("etanan lupaamaa poutaa");
        saatila.add("sataa mummoja hameet korvissa");
        saatila.add("keltaista lumisadetta");
        saatila.add("lumimyrskyä");
        saatila.add("räntäsadetta");
        saatila.add("sataa kissoja ja koiria");
        Random rnd = new Random();
        int celsius = rnd.nextInt(5 + 1 + 6) - 6;
        int saanIndeksi = rnd.nextInt(saatila.size());
        String randomSaa = saatila.get(saanIndeksi);
        return ("Sää perillä: " + celsius + " celsiusastetta ja " + randomSaa + ".");
    }

    public static void valittuLahtoTaiSaapuminen() {
        Scanner lukija = new Scanner(System.in);
        System.out.println(" Valitse 1, jos haluat aikataulun lähtöajan mukaan. \n Valitse 2, jos haluat aikataulun saapumisajan mukaan.");
        kayttajanValinta = lukija.nextLine();
        while (!(kayttajanValinta.equalsIgnoreCase("1") || kayttajanValinta.equalsIgnoreCase("2"))) {
            System.out.println("Valinnan täytyy olla 1 tai 2. \n Valitse 1, jos haluat aikataulun lähtöajan mukaan. \n Valitse 2, jos haluat aikataulun saapumisajan mukaan.");
            kayttajanValinta = lukija.nextLine();
        }
        kayttajanValinta1tai2(kayttajanValinta);
    }

    public static void kayttajanValinta1tai2(String kayttajanValinta) {
        if (kayttajanValinta.equalsIgnoreCase("1")) {
            //junatLahtoajanMukaan(lukija, annettuPaateAsema, lahtoAsemaLyhenne, paateAsemaLyhenne, aika2, junat);
            if(kayttajanValinta.equalsIgnoreCase("2")) {
                //junatSaapumisajanMukaan(lukija, lahtoAsemaLyhenne, paateAsemaLyhenne, aika2, junat);
            }
        }
    }

    public static void junatSaapumisajanMukaan(Scanner lukija, String lahtoAsemaLyhenne, String paateAsemaLyhenne, Date aika2, List<Juna> junat) {
        int saapuvaJuna;
        int loytyy = 0;
        Collections.reverse(junat);
        saapuva:
        for (int i = 0; i < junat.size(); i++) {
            Juna nykyinenJuna = junat.get(i);
            List<TimeTableRow> nykyisetAikataulurivit = nykyinenJuna.getTimeTableRows();
            TimeTableRow lahtorivi = null;
            for(TimeTableRow rivi : nykyisetAikataulurivit) {
                if(rivi.getStationShortCode().equals(lahtoAsemaLyhenne)) {
                    lahtorivi = rivi;
                } else if (lahtorivi != null) {
                    if(rivi.getStationShortCode().equals(paateAsemaLyhenne)) {
                        if (rivi.getScheduledTime().before(aika2)) {
                            System.out.println("\n"+"Lähtöaika: " + "\t" +"\t" +"\t" +"\t" +"\t"+"\t"+"\t"+"\t"+ lahtorivi.getScheduledTime());
                            System.out.println("Saapumisaika: " +"\t" +"\t"+"\t"+"\t"+"\t" +"\t"+"\t"+ rivi.getScheduledTime());
                            long difference = rivi.getScheduledTime().getTime() - lahtorivi.getScheduledTime().getTime();
                            System.out.println("Valitse tämä matka indeksistä " + i +"." + "\t"+ "\t"+ "Matka-aika on " +  (difference/60000) +  " minuuttia.");

                            loytyy++;
                            if(loytyy >= 5) break saapuva;
                            break;
                        }
                    }
                }
            }
        }

        System.out.println("\n" + "Anna haluamasi junan indeksi: ");
        saapuvaJuna = lukija.nextInt();

        // ottaa talteen saapuvan juna-aseman indeksin ja tulostaa tarvittavat tiedot
        for (int i = 0; i < junat.get(saapuvaJuna).getTimeTableRows().size(); i++) {
            if (junat.get(saapuvaJuna).getTimeTableRows().get(i).getStationShortCode().equals(lahtoAsemaLyhenne) && junat.get(saapuvaJuna).getTimeTableRows().get(i).getType().equals("DEPARTURE")) {
                System.out.println("Lähtöasema: " + junat.get(saapuvaJuna).getTimeTableRows().get(i).getStationShortCode());
                System.out.println("Junan lähtöaika: " + junat.get(saapuvaJuna).getTimeTableRows().get(i).getScheduledTime());
                System.out.println(randomSaatila());
                break;
            }
        }
    }

    public static void junatLahtoajanMukaan(Scanner lukija, String annettuPaateAsema, String lahtoAsemaLyhenne, String paateAsemaLyhenne, Date aika2, List<Juna> junat) {
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
                System.out.println(randomSaatila());
                break;
            }

        }
    }
}