package test;

import java.util.List;

public class TarotFortuneStrategy implements FortuneStrategy {
    @Override
    public FortuneResult buildResult(List<FortuneFace> faces, boolean[] reversed) {
        String[] positions = {"过去", "现在", "未来"};
        String advice = buildRichTarotAdvice(faces, reversed);
        StringBuilder builder = new StringBuilder();
        builder.append("塔罗：\n");
        for (int i = 0; i < faces.size(); i++) {
            FortuneFace face = faces.get(i);
            boolean isReversed = reversed != null && i < reversed.length && reversed[i];
            builder.append(positions[Math.min(i, positions.length - 1)]).append("：")
                    .append(face.getName()).append(isReversed ? "（逆位）" : "（正位）").append(" - ")
                    .append(face.getKeyword()).append("。")
                    .append(isReversed ? buildReversedMessage(face) : face.getMessage()).append("\n");
        }
        builder.append("\n解读：").append(advice);
        return new FortuneResult(faces, reversed, builder.toString(), advice);
    }

    private static String buildRichTarotAdvice(List<FortuneFace> faces, boolean[] reversed) {
        if (faces.isEmpty()) {
            return "暂时没有可解读的塔罗牌，请重新抽牌。";
        }
        FortuneFace past = faces.get(0);
        FortuneFace present = faces.get(Math.min(1, faces.size() - 1));
        FortuneFace future = faces.get(Math.min(2, faces.size() - 1));
        boolean pastRev = isReversed(reversed, 0);
        boolean presentRev = isReversed(reversed, 1);
        boolean futureRev = isReversed(reversed, 2);

        return "【过去的根基】" + tarotPastDesc(past, pastRev) + "\n\n"
                + "【现在的关键】" + tarotPresentDesc(present, presentRev) + "\n\n"
                + "【未来的趋势】" + tarotFutureDesc(future, futureRev) + "\n\n"
                + "💡 整合建议：" + tarotIntegration(past, present, future, pastRev, presentRev, futureRev);
    }

    private static String tarotPastDesc(FortuneFace face, boolean rev) {
        String base = "你抽到了「" + face.getName() + "」" + (rev ? "（逆位）" : "（正位）") + "。";
        String keyword = face.getKeyword();
        if (rev) {
            return base + "「" + keyword + "」的逆位，暗示过去的这段经历可能留下了未完成的课题。那些被搁置的情绪、没说出口的话，或是半途而废的计划，至今仍在潜意识里轻轻拉扯着你。\n"
                    + "不用急着清理它们，给自己一点时间，像整理旧物一样，慢慢翻看、轻轻触摸。当你愿意直面时，它们自会找到安放的位置。";
        } else {
            return base + "「" + keyword + "」的正位代表着过去的积累与沉淀。你曾在这方面投入过真心与努力，或许经历过挑战，也收获过成长。\n"
                    + "这段经历不是包袱，而是你随身携带的锦囊。当现在遇到困惑时，不妨想想当时的自己是如何克服困难的，那份勇气依然属于你。";
        }
    }

    private static String tarotPresentDesc(FortuneFace face, boolean rev) {
        String base = "现在正处于「" + face.getName() + "」" + (rev ? "（逆位）" : "（正位）") + "的能量中。";
        String keyword = face.getKeyword();
        if (rev) {
            return base + "「" + keyword + "」的逆位提示你，当前可能正经历能量的阻塞或内在的冲突。你或许感到力不从心，或者对现状有些失望。\n"
                    + "这不是失败，而是一个暂停的信号。给自己一些空间，允许情绪自然流动。当内在的能量重新整合后，答案会自然而然地浮现。";
        } else {
            return base + "「" + keyword + "」的正位意味着你正处在一个充满潜力的时刻。你拥有所需的资源与能力，只需要相信自己的直觉，勇敢地迈出第一步。\n"
                    + "牌面鼓励你抓住眼前的机会，不必过度思虑。有时候，行动本身就是答案，而不是等待完美的计划。";
        }
    }

    private static String tarotFutureDesc(FortuneFace face, boolean rev) {
        String base = "未来将迎来「" + face.getName() + "」" + (rev ? "（逆位）" : "（正位）") + "的能量。";
        String keyword = face.getKeyword();
        if (rev) {
            return base + "「" + keyword + "」的逆位预示着，未来可能需要你重新审视某些事情。也许是一段关系的转变，或者是一个计划的调整。\n"
                    + "这并不一定是坏事，而是一个邀请——邀请你放慢脚步，重新评估方向。有时候，绕路是为了更好地抵达。";
        } else {
            return base + "「" + keyword + "」的正位预示着积极的变化与成长。你将在这个领域获得突破，或者找到期待已久的答案。\n"
                    + "保持开放的心态，相信宇宙会以它自己的方式为你安排。你已经走了这么远，值得拥有美好的结果。";
        }
    }

    private static String tarotIntegration(FortuneFace past, FortuneFace present, FortuneFace future,
                                           boolean pastRev, boolean presentRev, boolean futureRev) {
        String pastName = past.getName();
        String presentName = present.getName();
        String futureName = future.getName();
        String pastWord = past.getKeyword();
        String nowWord = present.getKeyword();
        String futureWord = future.getKeyword();
        
        if (pastRev && !presentRev && !futureRev) {
            return "从「" + pastName + "」的逆位，到「" + presentName + "」的正位，再到「" + futureName + "」的正位——这是一个非常积极的转变！\n"
                    + "过去的阻碍正在被当下的「" + nowWord + "」能量所转化。请珍惜现在手中的机会，继续保持专注。\n"
                    + "未来的「" + futureWord + "」正在向你招手，坚持走下去，你会收获应有的回报。";
        } else if (!pastRev && presentRev && !futureRev) {
            return "「" + pastName + "」的正位为你奠定了坚实的基础，但「" + presentName + "」的逆位提示你当前需要调整方向。\n"
                    + "这不是失败，而是成长的必经之路。给自己一些时间，重新审视「" + nowWord + "」这个领域。\n"
                    + "好消息是，未来的「" + futureName + "」预示着重回正轨。保持信心，调整后再出发。";
        } else if (!pastRev && !presentRev && !futureRev) {
            return "三张正位牌连成一片光明！「" + pastName + "」的积累，「" + presentName + "」的行动，将带你走向「" + futureName + "」的圆满。\n"
                    + "这是宇宙在为你加油打气！大胆前行吧，你已经准备好迎接美好的未来了。";
        } else if (pastRev && presentRev && !futureRev) {
            return "前两张牌的逆位提醒你，需要先处理内在的阻碍。「" + pastName + "」和「" + presentName + "」的挑战都是成长的礼物。\n"
                    + "不要害怕面对这些课题，每解决一个，你就离「" + futureName + "」的光明更近一步。";
        } else {
            return "三张牌共同描绘了你的生命旅程：\n"
                    + "🌙 过去：「" + pastName + "」教会了你「" + pastWord + "」\n"
                    + "✨ 现在：「" + presentName + "」正在教会你「" + nowWord + "」\n"
                    + "🌟 未来：「" + futureName + "」将带给你「" + futureWord + "」\n"
                    + "这是属于你独一无二的旅程，请带着觉察与耐心去体验每一个当下。答案不在远方，就在你前行的每一步中。";
        }
    }

    private static boolean isReversed(boolean[] reversed, int index) {
        return reversed != null && index >= 0 && index < reversed.length && reversed[index];
    }

    private static String buildReversedMessage(FortuneFace face) {
        return "这张牌的能量处于逆位，提示“" + face.getKeyword() + "”可能被延迟、压抑或需要从内在重新调整。";
    }
}