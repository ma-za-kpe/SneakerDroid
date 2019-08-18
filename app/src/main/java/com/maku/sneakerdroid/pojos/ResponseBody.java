
package com.maku.sneakerdroid.pojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseBody {

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    @SerializedName("probes")
    @Expose
    private List<Probe> probes = null;
    @SerializedName("participant-details")
    @Expose
    private ParticipantDetails participantDetails;
    @SerializedName("project-details")
    @Expose
    private ProjectDetails projectDetails;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public List<Probe> getProbes() {
        return probes;
    }

    public void setProbes(List<Probe> probes) {
        this.probes = probes;
    }

    public ParticipantDetails getParticipantDetails() {
        return participantDetails;
    }

    public void setParticipantDetails(ParticipantDetails participantDetails) {
        this.participantDetails = participantDetails;
    }

    public ProjectDetails getProjectDetails() {
        return projectDetails;
    }

    public void setProjectDetails(ProjectDetails projectDetails) {
        this.projectDetails = projectDetails;
    }

}
