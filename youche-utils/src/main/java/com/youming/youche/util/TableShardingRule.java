package com.youming.youche.util;

public class TableShardingRule {
    public TableShardingRule() {
    }

    public static String getShardingIdx(int index) {
        return completionLetter(index);
    }

    public static String getShardingIdx(long pk) {
        return completionLetter(Long.valueOf(pk % 10L).intValue());
    }

    public static String completionLetter(int index) {
        return index < 10 ? "0" + index : String.valueOf(index);
    }

    public static class Rule {
        public static final String ShardingIdx = "IDX";

        public Rule() {
        }
    }
}
