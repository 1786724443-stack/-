package test;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FortuneImageRepository {
    public static final int FACE_SIZE = 96;

    private static final ModeProfile IMAGE_PROFILE = new ModeProfile(
            new String[]{"太阳", "月亮", "星星", "云朵", "火焰", "叶子"},
            new String[]{"能量", "直觉", "希望", "变化", "热情", "生长"},
            new String[]{
                    "行动力正在恢复，适合把想法推到台前。",
                    "内心的判断很重要，别急着被外界声音带走。",
                    "会出现新的灵感或机会，保持开放会更容易看见它。",
                    "局势还在流动，先观察再决定会更稳妥。",
                    "动力很足，但需要控制节奏，避免一时冲动。",
                    "适合整理基础、慢慢积累，不必急着立刻见效。"
            }
    );
    private static final ModeProfile TAROT_PROFILE = createTarotProfile();
    private static final ModeProfile LIU_YAO_PROFILE = new ModeProfile(
            new String[]{"乾卦", "坤卦", "震卦", "巽卦", "坎卦", "离卦"},
            new String[]{"天行", "承载", "发动", "入微", "险阻", "明辨"},
            new String[]{
                    "势能充足，适合主动开局，但要守住方向。",
                    "以稳为先，承接与配合会带来更好的结果。",
                    "变化已经启动，先动起来再逐步修正。",
                    "细节决定成败，柔和推进比强行突破更顺。",
                    "前方有阻隔，谨慎行事能减少反复。",
                    "信息逐渐清楚，适合辨明重点后再行动。"
            }
    );

    private final Map<FortuneMode, List<ImageIcon>> iconsByMode = new EnumMap<>(FortuneMode.class);
    private final Map<FortuneMode, List<FortuneFace>> facesCache = new ConcurrentHashMap<>();
    private final Map<String, BufferedImage> imageCache = new ConcurrentHashMap<>();

    public FortuneImageRepository() {
        restoreDefaults();
        loadCustomImagesFromDisk();
    }

    public void restoreDefaults() {
        for (FortuneMode mode : FortuneMode.values()) {
            restoreDefaults(mode);
        }
        facesCache.clear();
    }

    public void restoreDefaults(FortuneMode mode) {
        List<ImageIcon> defaultIcons = new ArrayList<>();
        ModeProfile profile = getProfile(mode);
        final FortuneMode finalMode = mode;
        for (int i = 0; i < profile.size(); i++) {
            final int finalIndex = i;
            String cacheKey = mode.name() + "_default_" + i;
            BufferedImage cachedImage = imageCache.computeIfAbsent(cacheKey, k -> createDefaultImage(finalMode, finalIndex));
            defaultIcons.add(new ImageIcon(cachedImage));
        }
        iconsByMode.put(mode, defaultIcons);
        facesCache.remove(mode);
    }

    private void loadCustomImagesFromDisk() {
        for (FortuneMode mode : FortuneMode.values()) {
            File customDir = new File("custom_images", mode.name().toLowerCase());
            if (!customDir.exists() || !customDir.isDirectory()) {
                continue;
            }
            
            File[] customFiles = customDir.listFiles((dir, name) -> name.endsWith(".png"));
            if (customFiles == null || customFiles.length == 0) {
                continue;
            }
            
            List<String> faceNames = getFaceNames(mode);
            List<ImageIcon> icons = iconsByMode.get(mode);
            if (icons == null) {
                continue;
            }
            
            for (File file : customFiles) {
                String fileName = file.getName();
                String faceName = fileName.substring(0, fileName.length() - 4);
                
                int index = faceNames.indexOf(faceName);
                if (index >= 0 && index < icons.size()) {
                    try {
                        BufferedImage image = ImageIO.read(file);
                        if (image != null) {
                            icons.set(index, new ImageIcon(image));
                        }
                    } catch (IOException e) {
                        // 忽略加载失败的图片
                    }
                }
            }
            
            facesCache.remove(mode);
        }
    }

    public int loadCustomImages(FortuneMode mode, File[] files) throws IOException {
        if (files == null || files.length == 0) {
            return 0;
        }

        ModeProfile profile = getProfile(mode);
        List<ImageIcon> icons = iconsByMode.get(mode);
        List<ImageIcon> customIcons = new ArrayList<>(icons);
        int count = Math.min(files.length, profile.size());
        for (int i = 0; i < count; i++) {
            final File finalFile = files[i];
            String cacheKey = mode.name() + "_custom_" + finalFile.getAbsolutePath();
            BufferedImage image = imageCache.computeIfAbsent(cacheKey, k -> {
                try {
                    return ImageIO.read(finalFile);
                } catch (IOException e) {
                    return null;
                }
            });
            if (image == null) {
                throw new IOException("无法读取图片：" + files[i].getName());
            }
            customIcons.set(i, scaleImage(image));
        }
        iconsByMode.put(mode, customIcons);
        facesCache.remove(mode);
        return count;
    }

    public List<FortuneFace> getFaces(FortuneMode mode) {
        return facesCache.computeIfAbsent(mode, m -> loadFaces(m));
    }

    private List<FortuneFace> loadFaces(FortuneMode mode) {
        ModeProfile profile = getProfile(mode);
        List<ImageIcon> icons = iconsByMode.get(mode);
        List<FortuneFace> faces = new ArrayList<>(profile.size());
        for (int i = 0; i < profile.size(); i++) {
            faces.add(new FortuneFace(
                    profile.names[i],
                    profile.keywords[i],
                    profile.messages[i],
                    icons.get(i)
            ));
        }
        return faces;
    }

    public int getFaceCount(FortuneMode mode) {
        return getProfile(mode).size();
    }

    public List<String> getFaceNames(FortuneMode mode) {
        return Arrays.asList(getProfile(mode).names);
    }

    public javax.swing.Icon getFaceIcon(FortuneMode mode, String faceName) {
        ModeProfile profile = getProfile(mode);
        int index = -1;
        for (int i = 0; i < profile.names.length; i++) {
            if (profile.names[i].equals(faceName)) {
                index = i;
                break;
            }
        }
        
        if (index == -1) {
            return null;
        }
        
        List<javax.swing.ImageIcon> icons = iconsByMode.get(mode);
        if (icons == null) {
            restoreDefaults(mode);
            icons = iconsByMode.get(mode);
        }
        
        return icons.get(index);
    }

    public boolean restoreDefaultImage(FortuneMode mode, String faceName) {
        ModeProfile profile = getProfile(mode);
        int index = -1;
        for (int i = 0; i < profile.names.length; i++) {
            if (profile.names[i].equals(faceName)) {
                index = i;
                break;
            }
        }
        
        if (index == -1) {
            return false;
        }
        
        final FortuneMode finalMode = mode;
        final int finalIndex = index;
        
        List<javax.swing.ImageIcon> icons = iconsByMode.get(mode);
        if (icons == null) {
            restoreDefaults(mode);
            icons = iconsByMode.get(mode);
        }
        
        String cacheKey = mode.name() + "_default_" + index;
        BufferedImage cachedImage = imageCache.computeIfAbsent(cacheKey, k -> createDefaultImage(finalMode, finalIndex));
        icons.set(index, new ImageIcon(cachedImage));
        
        File customDir = new File("custom_images", mode.name().toLowerCase());
        File destFile = new File(customDir, faceName + ".png");
        if (destFile.exists()) {
            destFile.delete();
        }
        
        facesCache.remove(mode);
        return true;
    }

    public boolean replaceImage(FortuneMode mode, String faceName, File imageFile) throws IOException {
        ModeProfile profile = getProfile(mode);
        int index = -1;
        for (int i = 0; i < profile.names.length; i++) {
            if (profile.names[i].equals(faceName)) {
                index = i;
                break;
            }
        }
        
        if (index == -1) {
            return false;
        }

        BufferedImage image = ImageIO.read(imageFile);
        if (image == null) {
            throw new IOException("无法读取图片：" + imageFile.getName());
        }

        List<ImageIcon> icons = iconsByMode.get(mode);
        if (icons == null) {
            restoreDefaults(mode);
            icons = iconsByMode.get(mode);
        }
        icons.set(index, scaleImage(image));
        
        File customDir = new File("custom_images", mode.name().toLowerCase());
        customDir.mkdirs();
        File destFile = new File(customDir, faceName + ".png");
        BufferedImage scaled = new BufferedImage(FACE_SIZE, FACE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(image.getScaledInstance(FACE_SIZE, FACE_SIZE, Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();
        ImageIO.write(scaled, "png", destFile);
        
        facesCache.remove(mode);
        return true;
    }

    private static ImageIcon scaleImage(BufferedImage source) {
        String cacheKey = "scaled_" + source.hashCode();
        Image scaled = source.getScaledInstance(FACE_SIZE, FACE_SIZE, Image.SCALE_SMOOTH);
        BufferedImage image = new BufferedImage(FACE_SIZE, FACE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(scaled, 0, 0, null);
        g.dispose();
        return new ImageIcon(image);
    }

    private static BufferedImage createDefaultImage(FortuneMode mode, int index) {
        BufferedImage image = new BufferedImage(FACE_SIZE, FACE_SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.setColor(new Color(252, 253, 255));
        g.fillRoundRect(0, 0, FACE_SIZE - 1, FACE_SIZE - 1, 18, 18);
        g.setColor(new Color(190, 198, 208));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(1, 1, FACE_SIZE - 3, FACE_SIZE - 3, 18, 18);

        if (mode == FortuneMode.TAROT) {
            drawTarotCard(g, index);
        } else if (mode == FortuneMode.LIU_YAO) {
            drawHexagram(g, index);
        } else if (index == 0) {
            drawSun(g);
        } else if (index == 1) {
            drawMoon(g);
        } else if (index == 2) {
            drawStar(g);
        } else if (index == 3) {
            drawCloud(g);
        } else if (index == 4) {
            drawFlame(g);
        } else {
            drawLeaf(g);
        }

        g.dispose();
        return image;
    }

    private static ModeProfile getProfile(FortuneMode mode) {
        if (mode == FortuneMode.TAROT) {
            return TAROT_PROFILE;
        }
        if (mode == FortuneMode.LIU_YAO) {
            return LIU_YAO_PROFILE;
        }
        return IMAGE_PROFILE;
    }

    private static ModeProfile createTarotProfile() {
        String[] majorNames = {
                "愚者", "魔术师", "女祭司", "皇后", "皇帝", "教皇", "恋人", "战车", "力量", "隐者",
                "命运之轮", "正义", "倒吊人", "死神", "节制", "恶魔", "高塔", "星星", "月亮", "太阳",
                "审判", "世界"
        };
        String[] majorKeywords = {
                "启程", "创造", "洞察", "丰盛", "秩序", "信念", "选择", "推进", "勇气", "沉思",
                "转机", "平衡", "等待", "蜕变", "调和", "束缚", "突变", "希望", "潜意识", "清朗",
                "觉醒", "完成"
        };
        String[] suits = {"权杖", "圣杯", "宝剑", "星币"};
        String[] suitKeywords = {"行动", "情感", "思辨", "现实"};
        String[] ranks = {"Ace", "二", "三", "四", "五", "六", "七", "八", "九", "十", "侍从", "骑士", "王后", "国王"};
        String[] rankKeywords = {"开端", "选择", "成长", "稳定", "挑战", "调整", "试炼", "推进", "收获", "完成", "学习", "冲刺", "滋养", "掌控"};

        int total = majorNames.length + suits.length * ranks.length;
        String[] names = new String[total];
        String[] keywords = new String[total];
        String[] messages = new String[total];

        for (int i = 0; i < majorNames.length; i++) {
            names[i] = majorNames[i];
            keywords[i] = majorKeywords[i];
            messages[i] = "这张大阿卡那提示你关注“" + majorKeywords[i] + "”带来的核心课题。";
        }

        int index = majorNames.length;
        for (int suit = 0; suit < suits.length; suit++) {
            for (int rank = 0; rank < ranks.length; rank++) {
                names[index] = suits[suit] + ranks[rank];
                keywords[index] = suitKeywords[suit] + "·" + rankKeywords[rank];
                messages[index] = suits[suit] + "牌组提示你在“" + suitKeywords[suit] + "”领域处理“" + rankKeywords[rank] + "”阶段。";
                index++;
            }
        }
        return new ModeProfile(names, keywords, messages);
    }

    private static void drawSun(Graphics2D g) {
        g.setColor(new Color(255, 188, 66));
        g.fillOval(31, 31, 34, 34);
        g.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * i / 4;
            int x1 = 48 + (int) (24 * Math.cos(angle));
            int y1 = 48 + (int) (24 * Math.sin(angle));
            int x2 = 48 + (int) (34 * Math.cos(angle));
            int y2 = 48 + (int) (34 * Math.sin(angle));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private static void drawMoon(Graphics2D g) {
        g.setColor(new Color(91, 125, 204));
        g.fillOval(27, 20, 48, 56);
        g.setColor(new Color(252, 253, 255));
        g.fillOval(43, 16, 48, 58);
    }

    private static void drawStar(Graphics2D g) {
        int[] x = {48, 55, 77, 59, 66, 48, 30, 37, 19, 41};
        int[] y = {16, 38, 38, 50, 73, 60, 73, 50, 38, 38};
        g.setColor(new Color(247, 202, 24));
        g.fillPolygon(x, y, x.length);
        g.setColor(new Color(210, 160, 20));
        g.drawPolygon(x, y, x.length);
    }

    private static void drawCloud(Graphics2D g) {
        g.setColor(new Color(116, 185, 255));
        g.fillOval(22, 43, 28, 24);
        g.fillOval(39, 31, 30, 36);
        g.fillOval(58, 42, 24, 25);
        g.fillRoundRect(25, 53, 55, 20, 12, 12);
    }

    private static void drawFlame(Graphics2D g) {
        int[] x = {48, 66, 61, 73, 51, 43, 30, 37};
        int[] y = {17, 42, 51, 73, 82, 65, 76, 48};
        g.setColor(new Color(232, 89, 71));
        g.fillPolygon(x, y, x.length);
        g.setColor(new Color(255, 190, 90));
        g.fillOval(39, 52, 20, 26);
    }

    private static void drawLeaf(Graphics2D g) {
        g.setColor(new Color(67, 170, 139));
        g.fillOval(28, 22, 44, 58);
        g.setColor(new Color(252, 253, 255));
        g.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(48, 75, 59, 33);
        g.drawLine(49, 58, 37, 48);
        g.drawLine(53, 48, 64, 41);
    }

    private static void drawTarotCard(Graphics2D g, int index) {
        Color[] colors = {
                new Color(244, 177, 131),
                new Color(95, 158, 160),
                new Color(123, 104, 238),
                new Color(218, 165, 32),
                new Color(205, 92, 92),
                new Color(85, 130, 85)
        };
        String symbol = index < 22 ? String.valueOf(index) : String.valueOf(index - 21);
        Color color = colors[index % colors.length];

        g.setColor(color);
        g.fillRoundRect(25, 12, 46, 72, 10, 10);
        g.setColor(new Color(255, 250, 240));
        g.fillRoundRect(31, 18, 34, 60, 8, 8);
        g.setColor(color.darker());
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(25, 12, 46, 72, 10, 10);
        g.setFont(g.getFont().deriveFont(symbol.length() > 2 ? 14f : 18f));
        int textX = symbol.length() > 1 ? 38 : 43;
        g.drawString(symbol, textX, 55);
    }

    private static void drawHexagram(Graphics2D g, int index) {
        boolean[][] hexagrams = {
                {true, true, true, true, true, true},
                {false, false, false, false, false, false},
                {true, false, false, false, false, true},
                {false, true, true, true, true, false},
                {false, true, false, false, true, false},
                {true, false, true, true, false, true}
        };

        g.setColor(new Color(46, 61, 73));
        g.setStroke(new BasicStroke(5, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int i = 0; i < hexagrams[index].length; i++) {
            int y = 23 + i * 9;
            if (hexagrams[index][i]) {
                g.drawLine(24, y, 72, y);
            } else {
                g.drawLine(24, y, 43, y);
                g.drawLine(53, y, 72, y);
            }
        }
    }

    private static class ModeProfile {
        private final String[] names;
        private final String[] keywords;
        private final String[] messages;

        private ModeProfile(String[] names, String[] keywords, String[] messages) {
            this.names = names;
            this.keywords = keywords;
            this.messages = messages;
        }

        private int size() {
            return names.length;
        }
    }
}