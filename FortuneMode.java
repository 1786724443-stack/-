package test;

public enum FortuneMode {
    IMAGE_ORACLE(
            "图片运势",
            3,
            "抽取图片",
            "图片正在随机抽取中，请稍候...",
            "想看看今天的运势吗？点击“抽取图片”，我来为你选 3 张图片解读今日运势~"
    ),
    TAROT(
            "塔罗牌",
            3,
            "抽取塔罗",
            "塔罗牌正在洗牌中，请稍候...",
            "想探索你的过去、现在和未来吗？点击“抽取塔罗”，让塔罗牌为你揭示答案~"
    ),
    LIU_YAO(
            "六爻",
            6,
            "起卦",
            "六爻正在起卦中，请稍候...",
            "想通过传统卦象了解运势吗？点击“起卦”，让六爻为你指点迷津~"
    );

    private final String displayName;
    private final int drawCount;
    private final String actionText;
    private final String loadingText;
    private final String initialText;

    FortuneMode(String displayName, int drawCount, String actionText, String loadingText, String initialText) {
        this.displayName = displayName;
        this.drawCount = drawCount;
        this.actionText = actionText;
        this.loadingText = loadingText;
        this.initialText = initialText;
    }

    public int getDrawCount() {
        return drawCount;
    }

    public String getActionText() {
        return actionText;
    }

    public String getLoadingText() {
        return loadingText;
    }

    public String getInitialText() {
        return initialText;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
