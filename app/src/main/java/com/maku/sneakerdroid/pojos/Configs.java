
package com.maku.sneakerdroid.pojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Configs {

    @SerializedName("regex")
    @Expose
    private List<Object> regex = null;

    public List<Object> getRegex() {
        return regex;
    }

    public void setRegex(List<Object> regex) {
        this.regex = regex;
    }

}
