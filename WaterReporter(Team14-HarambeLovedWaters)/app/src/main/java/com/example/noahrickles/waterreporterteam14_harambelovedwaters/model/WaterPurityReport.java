package com.example.noahrickles.waterreporterteam14_harambelovedwaters.model;
import com.google.android.gms.maps.model.LatLng;
import android.location.Address;
/**
 * Created by Matt on 2/27/2017.
 */

public class WaterPurityReport {

    private String dateAndTime;
    private Address address;
    //    private LatLng location;
    private String username;
    private int reportNumber = 0;
    private double virusPPM;
    private double contaminantPPM;
    private String conditionOfWater;


    /**
     * Constructor for the water report object
     * @param dateAndTime       A String representation of the current date and time of the report
    //     * @param location          The latitude/longitude location of the water
     * @param username          The user submitting the report
     * @param reportNumber      Autogenerated report number
     * @param virusPPM       The type of the water source
     * @param contaminantPPM      The type of the water source
     * @param conditionOfWater  The drinking condition of the water
     */
    public WaterPurityReport(String dateAndTime, Address address, String username,
                       int reportNumber, double contaminantPPM, double virusPPM, String conditionOfWater) {
        this.dateAndTime = dateAndTime;
        this.address = address;
//        this.location = location;
        this.username = username;
        this.reportNumber = reportNumber;
        this.virusPPM = virusPPM;
        this.contaminantPPM = contaminantPPM;
        this.conditionOfWater = conditionOfWater;
    }

    /**
     * Gets the date and time of a WaterReport
     * @return the WaterReport's dateAndTime
     */
    public String getDateAndTime() {
        return dateAndTime;
    }

    /**
     * Gets the address of a WaterReport
     * @return the WaterReport's address
     */
    public Address getAddress() {
        return address;
    }

    public String getAddressString() {
        StringBuilder output = new StringBuilder();
        int maxLine = address.getMaxAddressLineIndex();
        for (int i = 0; i <= maxLine; i++) {
            output.append("\n" + address.getAddressLine(i));
        }
        return output.toString();
    }
    /**
     * Gets the location of a WaterReport
     * @return the WaterReport's location
     */
    public LatLng getLocation() {
        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
        return latLng;
    }

    /**
     * Gets the username of a WaterReport
     * @return the WaterReport's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the report number of a WaterReport
     * @return the WaterReport's reportNumber
     */
    public int getReportNumber() {
        return reportNumber;
    }


    /**
     * Gets the condition of water of a WaterReport
     * @return the WaterReport's conditionOfWater
     */
    public String getConditionOfWater() {
        return conditionOfWater;
    }

    /**
     * Gets the condition of water of a WaterReport
     * @return the WaterReport's conditionOfWater
     */
    public double getContaminantPPMOfWater() {
        return contaminantPPM;
    }
    /**
     * Gets the condition of water of a WaterReport
     * @return the WaterReport's conditionOfWater
     */
    public double getVirusPPMOfWater() {
        return virusPPM;
    }
    /**
     * Gets a String representation of the WaterReport, including important
     * information about the WaterReport.
     * @return the String representation of the User
     */
    public String toString() {
        return "Date/Time: " + dateAndTime
                + "\nAddress: " + getAddressString()
                + "\nLat/Lng: (" + String.format("%.3f", address.getLatitude())
                + ", " + String.format("%.3f", address.getLongitude())
                + ")\nSubmitter: " + username
                + "\nReport number: " + reportNumber
                + "\nVirus PPM: " + virusPPM
                + "\nContaminant PPM: " + virusPPM
                + "\nCondition of Water: " + conditionOfWater;
    }

}