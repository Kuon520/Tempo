//HAHA FUNNY EMOJIS GO BRRRRRRRRRRRRRRRR

package com.spotify.sdk.demo;

public class SongClassifier {


    static public final float[] speedTutel = {0, 60};
    static public final float[] speedStrolling = {60, 80};
    static public final float[] speedWalking = {80, 110};
    static public final float[] speedBriskWalk = {110, 130};
    static public final float[] speedJogging = {130, 150};
    static public final float[] speedRunning = {150, 170};
    static  public final float[] speedSprinting = {170, 210};
    static public final float[] speedRFYL = {210, 230};
    static public final float[] speedCheeto = {230, 1000};

    static public final float[] energyChill = {0, 0.6f};
    static  public final float[] energyModerate = {0.6f, 0.7f};
    static public final float[] energyEnergizing = {0.7f, 0.9f};
    static public final float[] energyBloodPumping = {0.9f, 1};

    static public String getTrackBPMClassify(float currentBPM,boolean isDoubleBeat) {
        String bpmCommentReturn ="";
        String bpmComment = "";
        if(true){
        if (currentBPM < 60) {
            bpmComment = "🐢";
        } else if (60 <= currentBPM && currentBPM < 80) {
            bpmComment = "🚶";
        } else if (80 <= currentBPM && currentBPM < 110) {
            bpmComment = "🚶️💨️";
        } else if (110 <= currentBPM && currentBPM < 130) {
            bpmComment = "🏃‍♂️";
        } else if (130 <= currentBPM && currentBPM < 150) {
            bpmComment = "🏃‍♂️💨️";
        } else if (150 <= currentBPM && currentBPM < 170) {
            bpmComment = "🏃‍♂️💨️💨️";
        } else if (170 <= currentBPM && currentBPM < 210) {
            bpmComment = "🏃‍♂️💨️⚡";
        } else if (210 <= currentBPM && currentBPM < 230) {
            bpmComment = "🏃‍♂️🐆";
        } else {
            bpmComment = "🏃‍♂️🐆🐅";
        }
            bpmCommentReturn=bpmComment;
        }
        if(isDoubleBeat){
            bpmCommentReturn=bpmComment;
            String bpmCommentHalf = "";
            String bpmCommentDouble = "";
            float currentBPMDouble = currentBPM*2;
            float currentBPMHalf = currentBPM/2;
            if(currentBPM >= 120) {
                if (currentBPMDouble < 60) {
                    bpmCommentDouble = "🐢";
                } else if (60 <= currentBPMDouble && currentBPMDouble < 80) {
                    bpmCommentDouble = "🚶";
                } else if (80 <= currentBPMDouble && currentBPMDouble < 110) {
                    bpmCommentDouble = "🚶️💨️";
                } else if (110 <= currentBPMDouble && currentBPMDouble < 130) {
                    bpmCommentDouble = "🏃‍♂️";
                } else if (130 <= currentBPMDouble && currentBPMDouble < 150) {
                    bpmCommentDouble = "🏃‍♂️💨️";
                } else if (150 <= currentBPMDouble && currentBPMDouble < 170) {
                    bpmCommentDouble = "🏃‍♂️💨️💨️";
                } else if (170 <= currentBPMDouble && currentBPMDouble < 210) {
                    bpmCommentDouble = "🏃‍♂️💨️⚡";
                } else if (210 <= currentBPMDouble && currentBPMDouble < 230) {
                    bpmCommentDouble = "🏃‍♂️🐆";
                } else {
                    bpmCommentDouble = "🏃‍♂️🐆🐅";
                }
//                bpmCommentReturn = bpmCommentDouble;
            }
            else if(currentBPM<120) {
                if (currentBPMHalf < 60) {
                    bpmCommentHalf = "🐢";
                } else if (60 <= currentBPMHalf && currentBPMHalf < 80) {
                    bpmCommentHalf = "🚶";
                } else if (80 <= currentBPMHalf && currentBPMHalf < 110) {
                    bpmCommentHalf = "🚶️💨️";
                } else if (110 <= currentBPMHalf && currentBPMHalf < 130) {
                    bpmCommentHalf = "🏃‍♂️";
                } else if (130 <= currentBPMHalf && currentBPMHalf < 150) {
                    bpmCommentHalf = "🏃‍♂️💨️";
                } else if (150 <= currentBPMHalf && currentBPMHalf < 170) {
                    bpmCommentHalf = "🏃‍♂️💨️💨️";
                } else if (170 <= currentBPMHalf && currentBPMHalf < 210) {
                    bpmCommentHalf = "🏃‍♂️💨️⚡";
                } else if (210 <= currentBPMHalf && currentBPMHalf < 230) {
                    bpmCommentHalf = "🏃‍♂️🐆";
                } else {
                    bpmCommentHalf = "🏃‍♂️🐆🐅";
                }
//                bpmCommentReturn = bpmCommentHalf;
            }

//
        }

        return bpmCommentReturn;
    }

    static public String getTrackEnergyClassify(float currentBPM){
        String bpmComment = "";

        if (currentBPM < 0.6f) {
            bpmComment = "😀";
        } else if (0.6 <= currentBPM && currentBPM < 0.7) {
            bpmComment = "🔥";
        } else if (0.7 <= currentBPM && currentBPM < 0.9) {
            bpmComment = "🔥🔥";
        } else if (0.9 <= currentBPM && currentBPM < 2) {
            bpmComment = "🔥🔥🚒️";
        }
        return bpmComment;
    }
}





