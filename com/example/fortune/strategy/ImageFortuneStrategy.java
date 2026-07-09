package com.example.fortune.strategy;

import com.example.fortune.model.FortuneFace;
import com.example.fortune.model.FortuneResult;

import java.util.List;

public class ImageFortuneStrategy implements FortuneStrategy {
    @Override
    public FortuneResult buildResult(List<FortuneFace> faces, boolean[] reversed) {
        String advice = buildRichImageAdvice(faces);
        String text = "抽取组合：" + names(faces, "、") + "\n"
                + "总评建议：" + advice + "\n\n"
                + messages(faces);
        return new FortuneResult(faces, null, text, advice);
    }

    private static String buildRichImageAdvice(List<FortuneFace> faces) {
        if (faces.isEmpty()) {
            return "暂时没有可解读的图像，请重新抽取。";
        }
        FortuneFace first = faces.get(0);
        FortuneFace second = faces.get(Math.min(1, faces.size() - 1));
        FortuneFace third = faces.get(Math.min(2, faces.size() - 1));

        return "第一张“" + first.getName() + "（" + first.getKeyword() + "）”代表你当下的状态："
                + imageDetail(first) + "\n\n"
                + "第二张“" + second.getName() + "（" + second.getKeyword() + "）”是正在影响你的力量："
                + imageDetail(second) + "\n\n"
                + "第三张“" + third.getName() + "（" + third.getKeyword() + "）”提示接下来的方向："
                + imageDetail(third) + "\n\n"
                + "把这三层叠在一起看："
                + imageIntegration(first, second, third);
    }

    private static String imageDetail(FortuneFace face) {
        String name = face.getName();
        if (name.contains("太阳")) {
            return "你心里有股想被看见、想放开的劲儿，但可能还没找到合适的出口。太阳的能量提醒你，勇敢一点，先把最真实的一面亮出来。";
        }
        if (name.contains("月亮")) {
            return "有些情绪藏在暗处，可能是没被说破的委屈，也可能是对未知的担忧。月亮告诉你，先承认这些感觉存在，它们就会慢慢变淡。";
        }
        if (name.contains("星星")) {
            return "你内心还有没灭掉的希望，即使事情看起来模糊。星星鼓励你保持一点微小的信心，它会帮你找到下一步。";
        }
        if (name.contains("云朵")) {
            return "事情还没完全明朗，像隔着一层云。这时候别急着下结论，等雾气散开一点再决定，反而更稳。";
        }
        if (name.contains("火焰")) {
            return "你身上有热情，也可能有点急躁。火焰提醒你，能量要花在刀刃上，别被一时的情绪烧坏了自己的节奏。";
        }
        if (name.contains("叶子")) {
            return "变化正在悄悄发生，像叶子慢慢舒展。给自己一点时间，别强求马上看到结果，生长本身就是收获。";
        }
        return face.getMessage();
    }

    private static String imageIntegration(FortuneFace first, FortuneFace second, FortuneFace third) {
        String fKeyword = first.getKeyword();
        String sKeyword = second.getKeyword();
        String tKeyword = third.getKeyword();
        return "你现在的“" + fKeyword + "”被“" + sKeyword + "”推动着，最终会走向“" + tKeyword + "”。\n"
                + "不用太着急，今天可以先做一件跟“" + fKeyword + "”有关的小事，比如把心里想说的话写下来，或者整理一下眼前最乱的一块。\n"
                + "等那团“" + sKeyword + "”稍微安静下来，你自然知道下一步往哪走。";
    }

    private static String names(List<FortuneFace> faces, String separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < faces.size(); i++) {
            if (i > 0) builder.append(separator);
            builder.append(faces.get(i).getName());
        }
        return builder.toString();
    }

    private static String messages(List<FortuneFace> faces) {
        StringBuilder builder = new StringBuilder();
        for (FortuneFace face : faces) {
            builder.append(face.getName()).append("：").append(face.getMessage()).append("\n");
        }
        return builder.toString();
    }
}