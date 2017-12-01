package com.example.kevin.watsoninnovation;

import android.app.Application;

import java.util.Map;

public class MyApplication extends Application {
    private boolean questRunning;
    private String currentQuestKey;
    Map<String,DBQuest> dbQuestMap;

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
}
