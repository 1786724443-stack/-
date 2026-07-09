package com.example.fortune.strategy;

import com.example.fortune.model.FortuneFace;
import com.example.fortune.model.FortuneResult;

import java.util.ArrayList;
import java.util.List;

public class LiuYaoFortuneStrategy implements FortuneStrategy {
    @Override
    public FortuneResult buildResult(List<FortuneFace> faces, boolean[] reversed) {
        String advice = buildRichLiuYaoAdvice(faces);
        StringBuilder yao = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            FortuneFace source = faces.get(i % faces.size());
            boolean yang = Math.abs(source.getName().hashCode() + i) % 2 == 0;
            yao.append("第").append(i + 1).append("爻：")
                    .append(yang ? "阳爻" : "阴爻")
                    .append("（取象：").append(source.getName()).append("）\n");
        }

        String text = "六爻取象：由六次取象组成六爻\n"
                + yao + "\n"
                + "解读：" + advice;
        return new FortuneResult(faces, null, text, advice);
    }

    private static String buildRichLiuYaoAdvice(List<FortuneFace> faces) {
        if (faces.isEmpty()) {
            return "暂时没有可解读的卦象，请重新起卦。";
        }
        List<FortuneFace> six = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            six.add(faces.get(i % faces.size()));
        }
        FortuneFace inner1 = six.get(0);
        FortuneFace inner2 = six.get(1);
        FortuneFace inner3 = six.get(2);
        FortuneFace outer1 = six.get(3);
        FortuneFace outer2 = six.get(4);
        FortuneFace outer3 = six.get(5);

        StringBuilder advice = new StringBuilder();
        advice.append("📜 初爻（根基）：「").append(inner1.getName()).append("」— ").append(inner1.getKeyword()).append("\n");
        advice.append("   这是整件事的底层状态，就像房子的地基。").append(innerAdvice(inner1)).append("\n\n");
        advice.append("⚡ 二爻（内部变化）：「").append(inner2.getName()).append("」— ").append(inner2.getKeyword()).append("\n");
        advice.append("   你内心正在经历调整，").append(inner2.getMessage()).append("\n\n");
        advice.append("🌱 三爻（过渡点）：「").append(inner3.getName()).append("」— ").append(inner3.getKeyword()).append("\n");
        advice.append("   这是内外交汇的关口，").append(transitionAdvice(inner3)).append("\n\n");
        advice.append("🌄 四爻（外部环境）：「").append(outer1.getName()).append("」— ").append(outer1.getKeyword()).append("\n");
        advice.append("   外界正在发生的事：").append(outerAdvice(outer1)).append("\n\n");
        advice.append("🔥 五爻（核心转折）：「").append(outer2.getName()).append("」— ").append(outer2.getKeyword()).append("\n");
        advice.append("   这是整件事情的“主位”，").append(coreAdvice(outer2)).append("\n\n");
        advice.append("✨ 上爻（最终趋势）：「").append(outer3.getName()).append("」— ").append(outer3.getKeyword()).append("\n");
        advice.append("   如果顺着发展走下去，").append(finalAdvice(outer3)).append("\n\n");
        advice.append("💬 综合建议：").append(liuYaoCombined(inner1, outer2, six));
        return advice.toString();
    }

    private static String innerAdvice(FortuneFace face) {
        if (face.getName().contains("坤") || face.getKeyword().contains("承载")) {
            return "你其实已经准备好了，只是还没意识到。先把眼前最确定的三件事列出来，哪怕很小。";
        }
        if (face.getName().contains("震") || face.getKeyword().contains("发动")) {
            return "变化已经开始了，别压制它。今天就可以动起来，哪怕只是整理一下桌面或发一条消息。";
        }
        return "先别急着向外求，回头看看你自己手里有什么、能做什么。";
    }

    private static String transitionAdvice(FortuneFace face) {
        if (face.getName().contains("坎") || face.getKeyword().contains("险阻")) {
            return "这里可能会有点卡，但正因如此才值得慢下来。别绕路，直接面对那个让你犹豫的点。";
        }
        if (face.getName().contains("离") || face.getKeyword().contains("明辨")) {
            return "信息开始清楚了，但你得先放下偏见才能看见真相。";
        }
        return "这一步需要一点耐心，别被情绪推着走。";
    }

    private static String outerAdvice(FortuneFace face) {
        if (face.getName().contains("巽") || face.getKeyword().contains("入微")) {
            return "外面的人或事正悄悄影响你，注意那些细节和暗示，别忽略小事。";
        }
        if (face.getName().contains("乾") || face.getKeyword().contains("天行")) {
            return "外部的节奏很快，机会在动，但你要选准自己擅长的那一块出手。";
        }
        return "观察一下最近谁在靠近你、什么事在反复出现，那往往是答案。";
    }

    private static String coreAdvice(FortuneFace face) {
        if (face.getName().contains("乾")) {
            return "主动权在你手上，但不要过度控制。像天一样，给事情留出自然发展的空间。";
        }
        if (face.getName().contains("坤")) {
            return "现在适合承接、配合、积累，而不是硬闯。先稳住自己的场子。";
        }
        return "这一爻提示你，要抓住那个最让你心动的选择，哪怕它看起来有点难。";
    }

    private static String finalAdvice(FortuneFace face) {
        if (face.getName().contains("兑") || face.getKeyword().contains("明辨")) {
            return "结果是让人舒心的，但前提是你已经做了该做的整理。";
        }
        return "顺着目前的趋势，只要你不逃避关键问题，结局会比预想的好。";
    }

    private static String liuYaoCombined(FortuneFace innerBase, FortuneFace outerCore, List<FortuneFace> all) {
        String innerName = innerBase.getName();
        String innerKw = innerBase.getKeyword();
        String outerName = outerCore.getName();
        String outerKw = outerCore.getKeyword();

        if ((innerName.contains("坤") || innerKw.contains("承载")) && (outerName.contains("乾") || outerKw.contains("天行"))) {
            return "内卦为“" + innerName + "”（承载），外卦为“" + outerName + "”（天行）。你内在已经准备好了，只差外部的推动。今天适合先稳住自己的底盘，等风来时顺势而为，不必硬闯。";
        }
        if ((innerName.contains("震") || innerKw.contains("发动")) && (outerName.contains("巽") || outerKw.contains("入微"))) {
            return "内卦“" + innerName + "”（发动）说明你内心很想动，外卦“" + outerName + "”（入微）提醒你注意细节。别一腔热血冲出去，先花半小时把计划中最容易出错的一环检查一遍。";
        }
        if ((innerName.contains("坎") || innerKw.contains("险阻")) && (outerName.contains("离") || outerKw.contains("明辨"))) {
            return "内卦“" + innerName + "”（险阻）意味着前面有坎，外卦“" + outerName + "”（明辨）告诉你亮点藏在暗处。与其硬闯，不如先停下来观察，等看清了水有多深再迈步。";
        }
        if ((innerName.contains("离") || innerKw.contains("明辨")) && (outerName.contains("坎") || outerKw.contains("险阻"))) {
            return "内卦“" + innerName + "”（明辨）让你看得很清，但外卦“" + outerName + "”（险阻）提示外部有隐藏风险。今天适合把决策权先留给自己，不要轻易被外界带节奏。";
        }
        if (innerName.equals(outerName)) {
            return "内卦与外卦都是“" + innerName + "”，能量高度统一。这意味着你最近的核心课题非常集中，不必分散精力。抓住“" + innerKw + "”这个关键词，一天只做一件与之相关的事。";
        }

        return "内卦以“" + innerName + "”（" + innerKw + "）为底，外卦以“" + outerName + "”（" + outerKw + "）为核。\n"
                + "你可以这样落地：先处理“" + innerKw + "”有关的内在准备（比如整理自己的情绪或资源），\n"
                + "再用“" + outerKw + "”的方式应对外界（比如顺势而为、注重细节或果断行动）。今天只选其中一件最小的事做完即可。";
    }
}