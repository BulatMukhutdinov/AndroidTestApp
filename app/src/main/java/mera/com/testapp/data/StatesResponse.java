package mera.com.testapp.data;

import com.google.gson.annotations.Expose;

import java.util.List;

import lombok.Getter;

public class StatesResponse {
    @Expose
    @Getter
    private long time;

    @Expose
    @Getter
    private List<List<String>> states;
}