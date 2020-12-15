package bgu.spl.mics.application.passiveObjects;

import java.util.List;


/**
 * Passive data-object representing an attack object.
 * You must not alter any of the given public methods of this class.
 * <p>
 * YDo not add any additional members/method to this class (except for getters).
 */
public class Attack {
    final List<Integer> serialNumbers;
    int duration; // needs to be final? - Eden //////////////////

    /**
     * Constructor.
     */
    public Attack(List<Integer> serialNumbers, int duration) {
        this.serialNumbers = serialNumbers;
        this.duration = duration;
    }

    public List<Integer> getSerialNumbers() {
        return serialNumbers;
    }

    public int getDuration() {
        return duration;
    }
}
