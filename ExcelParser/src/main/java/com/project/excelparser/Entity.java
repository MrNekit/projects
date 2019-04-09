
package com.project.excelparser;


public class Entity {
    
    private int parT;
    private int parI;
    private int parX;
    private float parZ;
    private float parQ;
    private float parV;
    private float parW;

    public Entity(int parT, int parI, int parX, float parZ, float parQ, float parV, float parW) {
        this.parT = parT;
        this.parI = parI;
        this.parX = parX;
        this.parZ = parZ;
        this.parQ = parQ;
        this.parV = parV;
        this.parW = parW;
    }
            
    public Entity(){
        
    }

    public int getParT() {
        return parT;
    }

    public void setParT(int parT) {
        this.parT = parT;
    }

    public int getParI() {
        return parI;
    }

    public void setParI(int parI) {
        this.parI = parI;
    }

    public int getParX() {
        return parX;
    }

    public void setParX(int parX) {
        this.parX = parX;
    }

    public float getParZ() {
        return parZ;
    }

    public void setParZ(float parZ) {
        this.parZ = parZ;
    }

    public float getParQ() {
        return parQ;
    }

    public void setParQ(float parQ) {
        this.parQ = parQ;
    }

    public float getParV() {
        return parV;
    }

    public void setParV(float parV) {
        this.parV = parV;
    }

    public float getParW() {
        return parW;
    }

    public void setParW(float parW) {
        this.parW = parW;
    }
    
    @Override
    public String toString() {
        return "Entity{" + "parT=" + parT + ", parI=" + parI + ", parX=" + parX + ", parZ=" + parZ + ", parQ=" + parQ + ", parV=" + parV + ", parW=" + parW + '}';
    }
}
