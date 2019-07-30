package com.printer.epos.rtpl.reports;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by android-pc3 on 20/4/15.
 */
public class StaffTimeTrackingData {

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("log_in_time")
    @Expose
    private String logInTime;
    @SerializedName("log_out_time")
    @Expose
    private String logOutTime;
    @Expose
    private String duration;

    /**
     * @return The userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId The user_id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return The createdDate
     */
    public String getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate The created_date
     */
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return The userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName The user_name
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return The logInTime
     */
    public String getLogInTime() {
        return logInTime;
    }

    /**
     * @param logInTime The log_in_time
     */
    public void setLogInTime(String logInTime) {
        this.logInTime = logInTime;
    }

    /**
     * @return The logOutTime
     */
    public String getLogOutTime() {
        return logOutTime;
    }

    /**
     * @param logOutTime The log_out_time
     */
    public void setLogOutTime(String logOutTime) {
        this.logOutTime = logOutTime;
    }

    /**
     * @return The duration
     */
    public String getDuration() {
        return duration;
    }

    /**
     * @param duration The duration
     */
    public void setDuration(String duration) {
        this.duration = duration;
    }


}
