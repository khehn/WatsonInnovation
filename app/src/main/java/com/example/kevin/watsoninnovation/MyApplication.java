package com.example.kevin.watsoninnovation;

import android.app.Application;

import java.util.Map;

public class MyApplication extends Application {
    private int history;
    private int art;
    private int calm;
    private int action;
    private int adventure;
    private int young;
    private int group;
    private int family;
    public int couple;
    public int mystery;
    private boolean questRunning;
    public String runningQuest;
    private String currentQuestKey;
    Map<String,DBQuest> dbQuestMap;
    Map<String,Boolean> quests;

    public boolean isQuestRunning() {
        return questRunning;
    }
    public void setQuestRunning(boolean questRunning) {
        this.questRunning = questRunning;
    }

    public String getCurrentQuestKey() {
        return currentQuestKey;
    }

    public void setCurrentQuestKey(String currentQuestKey) {
        this.currentQuestKey = currentQuestKey;
    }

    public Map<String, DBQuest> getDbQuestMap() {
        return dbQuestMap;
    }

    public void setDbQuestMap(Map<String, DBQuest> dbQuestMap) {
        this.dbQuestMap = dbQuestMap;
    }

    public int getHistory() {
        return history;
    }

    public void setHistory(int history) {
        this.history = history;
    }

    public int getArt() {
        return art;
    }

    public void setArt(int art) {
        this.art = art;
    }

    public int getCalm() {
        return calm;
    }

    public void setCalm(int calm) {
        this.calm = calm;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getMystery() {
        return mystery;
    }

    public void setMystery(int mystery) {
        this.mystery = mystery;
    }

    public int getAdventure() {
        return adventure;
    }

    public void setAdventure(int adventure) {
        this.adventure = adventure;
    }

    public int getYoung() {
        return young;
    }

    public void setYoung(int young) {
        this.young = young;
    }

    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getCouple() {
        return couple;
    }

    public void setCouple(int couple) {
        this.couple = couple;
    }

    public int getFamily() {
        return family;
    }

    public void setFamily(int family) {
        this.family = family;
    }

    public Map<String, Boolean> getQuests() {
        return quests;
    }

    public void setQuests(Map<String, Boolean> quests) {
        this.quests = quests;
    }

    public String getRunningQuest() {
        return runningQuest;
    }

    public void setRunningQuest(String runningQuest) {
        this.runningQuest = runningQuest;
    }
}
