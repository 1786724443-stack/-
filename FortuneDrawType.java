package test;

public enum FortuneDrawType {
    DAILY("每日运势"),
    ENDLESS("无尽抽取");

    private final String displayName;

    FortuneDrawType(String displayName) {
        this.displayName = displayName;
    }

    public boolean isDaily() {
        return this == DAILY;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
