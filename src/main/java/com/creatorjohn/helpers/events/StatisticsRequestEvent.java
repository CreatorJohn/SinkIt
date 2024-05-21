package com.creatorjohn.helpers.events;

final public class StatisticsRequestEvent extends Event implements ClientEvent {

    public StatisticsRequestEvent() {
        super(Type.STATISTICS_REQUEST);
    }
}
