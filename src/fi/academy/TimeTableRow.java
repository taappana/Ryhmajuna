package fi.academy;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
