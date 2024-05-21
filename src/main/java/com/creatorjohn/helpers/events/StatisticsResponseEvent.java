package com.creatorjohn.helpers.events;

import com.creatorjohn.db.models.UserStats;

final public class StatisticsResponseEvent extends Event implements ServerEvent {
    final public UserStats stats;

    public StatisticsResponseEvent(UserStats stats) {
        super(Type.STATISTICS_RESPONSE);
        this.stats = stats;
    }
}
