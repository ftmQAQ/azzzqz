package com.example.azzzqz.javabean;

import androidx.recyclerview.widget.RecyclerView;

public class Msg {
    private int proposer,recipient;
    private String msg,date;
    private int result;
    private int type;
    public void setProposer(int proposer) {
        this.proposer = proposer;
    }

    public void setRecipient(int recipient) {
        this.recipient = recipient;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getProposer() {
        return proposer;
    }

    public int getRecipient() {
        return recipient;
    }

    public String getMsg() {
        return msg;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
