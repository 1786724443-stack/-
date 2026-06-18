package test;

import java.util.List;

public class FortuneResult {
    private final List<FortuneFace> faces;
    private final boolean[] reversed;
    private final String text;
    private final String advice;

    public FortuneResult(List<FortuneFace> faces, boolean[] reversed, String text, String advice) {
        this.faces = faces;
        this.reversed = reversed;
        this.text = text;
        this.advice = advice;
    }

    public List<FortuneFace> getFaces() {
        return faces;
    }

    public boolean isReversed(int index) {
        return reversed != null && index >= 0 && index < reversed.length && reversed[index];
    }

    public String getText() {
        return text;
    }

    public String getAdvice() {
        return advice;
    }

    public String getFaceSummary() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < faces.size(); i++) {
            if (i > 0) {
                builder.append("、");
            }
            FortuneFace face = faces.get(i);
            builder.append(face.getName()).append(orientationText(i)).append("：").append(face.getKeyword());
        }
        return builder.toString();
    }

    public String getPatternSummary() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < faces.size(); i++) {
            if (i > 0) {
                builder.append("、");
            }
            builder.append(faces.get(i).getName()).append(orientationText(i));
        }
        return builder.toString();
    }

    public String getKeywordSummary() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < faces.size(); i++) {
            if (i > 0) {
                builder.append("、");
            }
            builder.append(faces.get(i).getKeyword());
        }
        return builder.toString();
    }

    private String orientationText(int index) {
        if (reversed == null || reversed.length == 0) {
            return "";
        }
        return isReversed(index) ? "（逆位）" : "（正位）";
    }
}
