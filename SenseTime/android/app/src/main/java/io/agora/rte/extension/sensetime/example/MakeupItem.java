package io.agora.rte.extension.sensetime.example;

import android.graphics.Bitmap;


public class MakeupItem {

    public String name;
    public Bitmap icon;
    public String iconUrl;
    public String path;
    public String groupName;
//    public SenseArMaterial material;
    public EffectType effectType;
    public Boolean selected;

    public MakeupItem(String name, Bitmap icon, String path) {
        this.name = name;
        this.icon = icon;
        this.path = path;
    }

    public MakeupItem(String name, Bitmap icon, String path, String iconUrl) {
        this.name = name;
        this.icon = icon;
        this.path = path;
        this.iconUrl = iconUrl;
    }

    @Override
    public String toString() {
        return "MakeupItem{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                '}';
    }
}
