package com.creatorjohn.helpers.events;

final public class NextRound extends Event {
    final public boolean myRound;

    public NextRound(boolean myRound) {
        super(Type.NEXT_ROUND);
        this.myRound = myRound;
    }
}
