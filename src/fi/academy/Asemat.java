package fi.academy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
class Asemat{
    private String stationShortCode;
    private String stationName;


    public String getStationShortCode() {return stationShortCode;}
    public String getStationName() { return stationName;}
}
