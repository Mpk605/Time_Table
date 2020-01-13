package com.jules.takemehomecountrytable.Fragments.Map.Room;

import com.jules.takemehomecountrytable.R;

import java.util.ArrayList;

public class Room {
    protected RoomType type;
    private String num;
    private String name;
    private int pos;
    protected ArrayList<String> nomsEnseignant;

    public Room(int pos) {
        this.pos = pos;
    }

    public Room(RoomType type, String num, String name, int pos) {
        this.type = type;
        this.num = num;
        this.name = name;
        this.pos = pos;
    }

    public Room(RoomType type, ArrayList<String> nomsEnseignant, int pos) {
        this.type = type;
        this.pos = pos;
        this.nomsEnseignant = nomsEnseignant;
    }

    public Room(RoomType type, int pos) {
        this.type = type;
        this.pos = pos;
    }

    public Room(RoomType type, String num, int pos) {
        this.num = num;
        this.pos = pos;
    }

    public RoomType getType() {
        return type;
    }

    public String getNum() {
        return num;
    }

    public String getName() {
        return name;
    }

    public int getPos() {
        return pos;
    }

    public ArrayList<String> getNomsEnseignant() {
        return nomsEnseignant;
    }

    public int getColor() {
        switch (this.type) {
            case TP:
                return R.color.tp;
            case TD:
                return R.color.td;
            case BUREAU:
                return R.color.bureau;
            case WC:
                return R.color.WC;
            case STAIR:
                return R.color.stair;
            default:
                return 0;
        }
    }

    public String getProf(int index){
        return this.nomsEnseignant.get(index);
    }

    public int getNumberOfProf(){
        return this.nomsEnseignant.size();
    }
}
