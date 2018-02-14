package fi.academy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.net.URL;
import java.util.*;

/*
Vaatii Jackson kirjaston:
File | Project Structure
Libraries >> Add >> Maven
Etsi "jackson-databind", valitse versio 2.0.5
Asentuu Jacksonin databind, sekä core ja annotations
 */

public class JunaJSON {
    public static void main(String[] args) {
        lueJunanJSONData();
    }

    //    static String lahtoAsemaLyhenne = "";
//    static String paateAsemaLyhenne = "";
    private static void lueJunanJSONData() {
        String baseurl = "https://rata.digitraffic.fi/api/v1";
        Scanner lukija = new Scanner(System.in);
        System.out.println("Anna lähtöasema:");
        String kayttajanLahtoAsema = lukija.nextLine();
        System.out.println("Anna pääteasema:");
        String kayttajanPaateAsema = lukija.nextLine();
        System.out.println("Anna tunnit!");
        int annetutTunnit = lukija.nextInt();
        System.out.println("Anna minuutit!");
        int annetutMinuutit = lukija.nextInt();
        String lahtoAsemaLyhenne = "";
        String paateAsemaLyhenne = "";

        Calendar kalenteri = new GregorianCalendar();
        kalenteri.set(Calendar.MINUTE, annetutMinuutit);
        kalenteri.set(Calendar.HOUR_OF_DAY, annetutTunnit);
        System.out.println(kalenteri.getTime());
        Date aika2 = kalenteri.getTime();

        try {
            URL url = new URL("https://rata.digitraffic.fi/api/v1/metadata/stations");
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Asemat.class);
            List<Asemat> asemat = mapper.readValue(url, tarkempiListanTyyppi);

            String annettuLahtoAsema = kayttajanLahtoAsema;
            int annetunLahtoAsemanIndeksi = 0;
            for (int i = 0; i < asemat.size(); i++) {
                if (asemat.get(i).getStationName().equalsIgnoreCase(annettuLahtoAsema)) {
                    annetunLahtoAsemanIndeksi = i;
                    break;
                }
            }
            lahtoAsemaLyhenne = asemat.get(annetunLahtoAsemanIndeksi).getStationShortCode();
            System.out.println("lyhennetty lähtöasema : " + asemat.get(annetunLahtoAsemanIndeksi).getStationShortCode());

            String annettuPaateAsema = kayttajanPaateAsema;
            int annetunPaateAsemanIndeksi = 0;
            for (int i = 0; i < asemat.size(); i++) {
                if (asemat.get(i).getStationName().equalsIgnoreCase(annettuPaateAsema)) {
                    annetunPaateAsemanIndeksi = i;
                    break;
                }
            }
            paateAsemaLyhenne = asemat.get(annetunPaateAsemanIndeksi).getStationShortCode();
            System.out.println("lyhennetty pääteasema : " + asemat.get(annetunPaateAsemanIndeksi).getStationShortCode());


        } catch (Exception exe) {
            System.out.println(exe);
        }

        try {
            URL Junaurl = new URL(baseurl+"/live-trains/station/" + lahtoAsemaLyhenne + "/" + paateAsemaLyhenne);
            ObjectMapper mapper = new ObjectMapper();
            CollectionType tarkempiListanTyyppi = mapper.getTypeFactory().constructCollectionType(ArrayList.class, Juna.class);
            List<Juna> junat = mapper.readValue(Junaurl, tarkempiListanTyyppi);  // pelkkä List.class ei riitä tyypiksi

            int lahtevaJuna;
            int loydettyja = 0;
            ulompi:

            for (int i = 0; i < junat.size(); i++) {

                for (int j = 0; j < junat.get(i).getTimeTableRows().size() ; j++) {
                    if (junat.get(i).getTimeTableRows().get(j).getStationShortCode().equals(lahtoAsemaLyhenne) && junat.get(i).getTimeTableRows().get(j).getType().equals("DEPARTURE") && junat.get(i).getTimeTableRows().get(j).getScheduledTime().after(aika2)){
                        System.out.println("Mahdolliset lähdöt: " + junat.get(i).getTimeTableRows().get(j).getScheduledTime() + " indeksi" + i);
                        loydettyja++;
                        if(loydettyja>=5){
                            break ulompi;
                        }

                    }
                }
            }

            System.out.println("Anna lähtevän junan indeksi: ");
            lahtevaJuna = lukija.nextInt();

//            for (int i = 0; i < junat.size() ; i++) {
//                if(junat.get(i).getTimeTableRows().get)
//            }
//
            // ottaa talteen saapuvan juna-aseman indeksin ja tulostaa tarvittavat tiedot
            for (int i = 0; i < junat.get(lahtevaJuna).getTimeTableRows().size(); i++) {
                if (junat.get(lahtevaJuna).getTimeTableRows().get(i).getStationShortCode().equals(paateAsemaLyhenne)) {
                    System.out.println("Olet saapunut paikkaan: " + junat.get(lahtevaJuna).getTimeTableRows().get(i).getStationShortCode());
                    System.out.println("Saapumisaika: " + junat.get(lahtevaJuna).getTimeTableRows().get(i).getScheduledTime());
                    break;
                }
//                System.out.println("Kokeillaas: " + junat.get(0).getTimeTableRows().get(i).getStationShortCode());
            }

        } catch (Exception ex) {
            System.out.println(ex);
        }

    }

}


class Juna {
    boolean cancelled;
    String commuterLineID;
    //LocalDate departureDate;  // Jackson ei oikein pidä Java 8 päivistä oletuksena
    Date departureDate;
    String operatorShortCode;
    int operatorUICCode;
    boolean runningCurrently;
    List<TimeTableRow> timeTableRows;
    Date timetableAcceptanceDate;
    String timetableType;
    String trainCategory;
    int trainNumber;
    String trainType;
    long version;

    @Override
    public String toString() {
        return "Juna{" + "cancelled=" + cancelled + ", commuterLineID='" + commuterLineID + '\'' + ", departureDate=" + departureDate + ", operatorShortCode='" + operatorShortCode + '\'' + ", operatorUICCode=" + operatorUICCode + ", runningCurrently=" + runningCurrently + ", timeTableRows=" + timeTableRows + ", timetableAcceptanceDate=" + timetableAcceptanceDate + ", timetableType='" + timetableType + '\'' + ", trainCategory='" + trainCategory + '\'' + ", trainNumber=" + trainNumber + ", trainType='" + trainType + '\'' + ", version=" + version + '}';
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getCommuterLineID() {
        return commuterLineID;
    }

    public void setCommuterLineID(String commuterLineID) {
        this.commuterLineID = commuterLineID;
    }

    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {
        this.departureDate = departureDate;
    }

    public String getOperatorShortCode() {
        return operatorShortCode;
    }

    public void setOperatorShortCode(String operatorShortCode) {
        this.operatorShortCode = operatorShortCode;
    }

    public int getOperatorUICCode() {
        return operatorUICCode;
    }

    public void setOperatorUICCode(int operatorUICCode) {
        this.operatorUICCode = operatorUICCode;
    }

    public boolean isRunningCurrently() {
        return runningCurrently;
    }

    public void setRunningCurrently(boolean runningCurrently) {
        this.runningCurrently = runningCurrently;
    }

    public List<TimeTableRow> getTimeTableRows() {
        return timeTableRows;
    }

    public void setTimeTableRows(List<TimeTableRow> timeTableRows) {
        this.timeTableRows = timeTableRows;
    }

    public Date getTimetableAcceptanceDate() {
        return timetableAcceptanceDate;
    }

    public void setTimetableAcceptanceDate(Date timetableAcceptanceDate) {
        this.timetableAcceptanceDate = timetableAcceptanceDate;
    }

    public String getTimetableType() {
        return timetableType;
    }

    public void setTimetableType(String timetableType) {
        this.timetableType = timetableType;
    }

    public String getTrainCategory() {
        return trainCategory;
    }

    public void setTrainCategory(String trainCategory) {
        this.trainCategory = trainCategory;
    }

    public int getTrainNumber() {
        return trainNumber;
    }

    public void setTrainNumber(int trainNumber) {
        this.trainNumber = trainNumber;
    }

    public String getTrainType() {
        return trainType;
    }

    public void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class TimeTableRow {
    private String stationShortCode;
    private Integer stationUICCode;
    private String countryCode;
    private String type;
    private Boolean trainStopping;
    private Boolean commercialStop;
    private String commercialTrack;
    private Boolean cancelled;
    @JsonFormat(pattern = "yyyy-MM-dd'T'hh:mm:ss.SSS'Z'", timezone = "UTC")
    private Date scheduledTime;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public String getStationShortCode() {
        return stationShortCode;
    }
    public void setStationShortCode(String stationShortCode) {
        this.stationShortCode = stationShortCode;
    }
    public Integer getStationUICCode() {
        return stationUICCode;
    }
    public void setStationUICCode(Integer stationUICCode) {
        this.stationUICCode = stationUICCode;
    }
    public String getCountryCode() {
        return countryCode;
    }
    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Boolean getTrainStopping() {
        return trainStopping;
    }
    public void setTrainStopping(Boolean trainStopping) {
        this.trainStopping = trainStopping;
    }
    public Boolean getCommercialStop() {
        return commercialStop;
    }
    public void setCommercialStop(Boolean commercialStop) {
        this.commercialStop = commercialStop;
    }
    public String getCommercialTrack() {
        return commercialTrack;
    }
    public void setCommercialTrack(String commercialTrack) {
        this.commercialTrack = commercialTrack;
    }
    public Boolean getCancelled() {
        return cancelled;
    }
    public void setCancelled(Boolean cancelled) {
        this.cancelled = cancelled;
    }
    public Date getScheduledTime() {
        return scheduledTime;
    }
    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Asemat{
    private String stationShortCode;
    private String stationName;


    public String getStationShortCode() {return stationShortCode;}
    public String getStationName() { return stationName;}
}
