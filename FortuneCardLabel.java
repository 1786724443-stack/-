package test;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

public class FortuneCardLabel extends JLabel {
    private boolean selected;
    private float scale = 1.0f;
    private float rotation = 0f;
    private float alpha = 1f;
    private boolean showBack = false;
    private boolean isTarot = false;
    private String cardName = "";
    private String cardKeyword = "";

    public FortuneCardLabel(String text) {
        super(text, SwingConstants.CENTER);
        setHorizontalTextPosition(SwingConstants.CENTER);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 20, 8));
        setOpaque(false);
    }

    public void setCardInfo(String name, String keyword) {
        this.cardName = name != null ? name : "";
        this.cardKeyword = keyword != null ? keyword : "";
    }

    public void setSelectedCard(boolean selected) {
        this.selected = selected;
        repaint();
    }

    public void setScale(float scale) {
        this.scale = Math.max(0.8f, Math.min(1.1f, scale));
        repaint();
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        repaint();
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.max(0f, Math.min(1f, alpha));
        repaint();
    }

    public void setShowBack(boolean showBack) {
        this.showBack = showBack;
        repaint();
    }

    public boolean isShowBack() {
        return showBack;
    }

    public void setTarot(boolean tarot) {
        this.isTarot = tarot;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        if (scale != 1.0f || rotation != 0f) {
            AffineTransform transform = new AffineTransform();
            transform.translate(centerX, centerY);
            transform.rotate(Math.toRadians(rotation));
            transform.scale(scale, scale);
            transform.translate(-centerX, -centerY);
            g.transform(transform);
        }

        g.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));

        int shadowOffset = selected ? 6 : 3;
        int arc = 18;

        if (showBack && isTarot) {
            paintCardBack(g, shadowOffset, arc);
        } else {
            paintCardFace(g, shadowOffset, arc);
        }
        g.dispose();

        Graphics2D g2 = (Graphics2D) graphics.create();
        if (scale != 1.0f || rotation != 0f) {
            AffineTransform transform = new AffineTransform();
            transform.translate(centerX, centerY);
            transform.rotate(Math.toRadians(rotation));
            transform.scale(scale, scale);
            transform.translate(-centerX, -centerY);
            g2.transform(transform);
        }
        g2.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, alpha));
        
        if (isTarot) {
            javax.swing.Icon icon = getIcon();
            if (icon != null) {
                int contentMargin = 18;
                int cardHeight = Math.min(getHeight() - 45, 120);
                int contentWidth = getWidth() - 16 - contentMargin * 2;
                int contentHeight = cardHeight - contentMargin * 2;
                int iconX = 6 + contentMargin + (contentWidth - icon.getIconWidth()) / 2;
                int iconY = 6 + contentMargin + (contentHeight - icon.getIconHeight()) / 2;
                icon.paintIcon(this, g2, iconX, iconY);
            }
            drawTextBelowCard(graphics);
        } else {
            super.paintComponent(g2);
        }
        g2.dispose();
    }

    private void paintCardFace(Graphics2D g, int shadowOffset, int arc) {
        int width = getWidth() - 16;
        int cardHeight = isTarot ? Math.min(getHeight() - 45, 120) : getHeight() - 16;
        
        g.setColor(new Color(40, 48, 58, 28));
        g.fillRoundRect(8, 8 + shadowOffset, width, cardHeight, arc, arc);

        if (isTarot) {
            Color darkPurple = new Color(35, 25, 50);
            Color lightPurple = new Color(55, 40, 70);
            java.awt.GradientPaint bgGradient = new java.awt.GradientPaint(6, 6, lightPurple, 6 + width, 6 + cardHeight, darkPurple);
            g.setPaint(bgGradient);
            g.fillRoundRect(6, 6, width, cardHeight, arc, arc);

            Color gold = new Color(218, 165, 32);
            Color goldLight = new Color(255, 220, 120);
            
            g.setColor(gold);
            g.setStroke(new java.awt.BasicStroke(3));
            g.drawRoundRect(6, 6, width, cardHeight, arc, arc);

            g.setColor(goldLight);
            g.setStroke(new java.awt.BasicStroke(1));
            g.drawRoundRect(9, 9, width - 6, cardHeight - 6, arc - 3, arc - 3);

            int patternMargin = 12;
            int patternSize = 18;
            
            drawCornerPattern(g, 6 + patternMargin, 6 + patternMargin, patternSize, gold);
            drawCornerPattern(g, 6 + width - patternMargin - patternSize, 6 + patternMargin, patternSize, gold);
            drawCornerPattern(g, 6 + patternMargin, 6 + cardHeight - patternMargin - patternSize, patternSize, gold);
            drawCornerPattern(g, 6 + width - patternMargin - patternSize, 6 + cardHeight - patternMargin - patternSize, patternSize, gold);

            int innerFrameMargin = 14;
            g.setColor(gold);
            g.setStroke(new java.awt.BasicStroke(1.5f));
            g.drawRoundRect(6 + innerFrameMargin, 6 + innerFrameMargin, width - innerFrameMargin * 2, cardHeight - innerFrameMargin * 2, arc - 6, arc - 6);

            int contentMargin = 18;
            int contentWidth = width - contentMargin * 2;
            int contentHeight = cardHeight - contentMargin * 2;

            g.setColor(new Color(248, 245, 240));
            g.fillRoundRect(6 + contentMargin, 6 + contentMargin, contentWidth, contentHeight, arc - 8, arc - 8);

            if (selected) {
                g.setColor(new Color(255, 215, 150, 40));
                g.fillRoundRect(6, 6, width, cardHeight, arc, arc);

                g.setColor(new Color(255, 200, 100));
                g.setStroke(new java.awt.BasicStroke(2));
                g.drawRoundRect(6, 6, width, cardHeight, arc, arc);
            }
        } else {
            int height = getHeight() - 16;
            g.setColor(Color.WHITE);
            g.fillRoundRect(6, 6, width, height, arc, arc);

            g.setColor(new Color(205, 214, 224));
            g.drawRoundRect(6, 6, width, height, arc, arc);

            if (selected) {
                g.setColor(new Color(70, 110, 170, 58));
                g.fillRoundRect(6, 6, width, height, arc, arc);
                
                g.setColor(new Color(245, 170, 65));
                g.drawRoundRect(6, 6, width, height, arc, arc);
            }
        }
    }

    private void drawTextBelowCard(Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        String text = getText();
        if (text == null || text.isEmpty()) {
            g.dispose();
            return;
        }
        
        text = text.replace("<html>", "").replace("</html>", "")
                   .replace("<div style='text-align:center;'>", "").replace("</div>", "");
        
        String[] lines = text.split("<br>");
        
        int cardHeight = Math.min(getHeight() - 45, 120);
        int lineHeight = g.getFontMetrics().getHeight();
        int startY = cardHeight + 20;
        
        g.setColor(Color.BLACK);
        g.setFont(getFont());
        FontMetrics fm = g.getFontMetrics();
        
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            if (!line.isEmpty()) {
                java.awt.geom.Rectangle2D bounds = fm.getStringBounds(line, g);
                int textWidth = (int) bounds.getWidth();
                int textX = getWidth() / 2 - textWidth / 2;
                g.drawString(line, textX, startY + i * lineHeight);
            }
        }
        g.dispose();
    }

    private void drawCornerPattern(Graphics2D g, int x, int y, int size, Color color) {
        g.setColor(color);
        g.setStroke(new java.awt.BasicStroke(1.5f));

        int halfSize = size / 2;
        int cx = x + halfSize;
        int cy = y + halfSize;

        g.drawArc(x + 2, y + 2, size - 4, size - 4, 0, 90);

        int[] triX = {cx, cx + halfSize - 3, cx};
        int[] triY = {cy - halfSize + 3, cy, cy + halfSize - 3};
        g.drawPolygon(triX, triY, 3);

        int[] diamondX = {cx, cx + halfSize - 5, cx, cx - halfSize + 5};
        int[] diamondY = {cy - halfSize + 5, cy, cy + halfSize - 5, cy};
        g.drawPolygon(diamondX, diamondY, 4);
    }

    private int getTarotCardIndex(String name) {
        String[] majorArcana = {
                "愚者", "魔术师", "女祭司", "皇后", "皇帝", "教皇", "恋人", "战车", "力量", "隐者",
                "命运之轮", "正义", "倒吊人", "死神", "节制", "恶魔", "高塔", "星星", "月亮", "太阳",
                "审判", "世界"
        };
        for (int i = 0; i < majorArcana.length; i++) {
            if (majorArcana[i].equals(name)) {
                return i;
            }
        }
        return -1;
    }

    private void drawTarotCardArt(Graphics2D g, int x, int y, int w, int h, int cardIndex, Color gold, Color darkPurple) {
        int centerX = x + w / 2;
        int artTop = y + 8;
        int nameY = y + h - 22;
        int keywordY = y + h - 8;

        g.setFont(new Font("Microsoft YaHei", Font.BOLD, 9));
        g.setColor(darkPurple);
        g.drawString(cardName, centerX - g.getFontMetrics().stringWidth(cardName) / 2, nameY);

        g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 7));
        g.setColor(new Color(100, 80, 120));
        g.drawString(cardKeyword, centerX - g.getFontMetrics().stringWidth(cardKeyword) / 2, keywordY);

        int symbolSize = Math.min(w, h) - 45;
        int symbolCenterX = centerX;
        int symbolCenterY = artTop + symbolSize / 2 + 5;

        if (cardIndex >= 0 && cardIndex < 22) {
            drawMajorArcanaSymbol(g, symbolCenterX, symbolCenterY, symbolSize, cardIndex, gold, darkPurple, 4);
        } else {
            drawMinorArcanaSymbol(g, symbolCenterX, symbolCenterY, symbolSize, cardName, gold, darkPurple);
        }
    }

    private void drawMajorArcanaSymbol(Graphics2D g, int cx, int cy, int size, int index, Color gold, Color darkPurple, int margin) {
        int half = size / 2 - margin;

        switch (index) {
            case 0:
                drawFool(g, cx, cy, half, gold, darkPurple);
                break;
            case 1:
                drawMagician(g, cx, cy, half, gold, darkPurple);
                break;
            case 2:
                drawHighPriestess(g, cx, cy, half, gold, darkPurple);
                break;
            case 3:
                drawEmpress(g, cx, cy, half, gold, darkPurple);
                break;
            case 4:
                drawEmperor(g, cx, cy, half, gold, darkPurple);
                break;
            case 5:
                drawHierophant(g, cx, cy, half, gold, darkPurple);
                break;
            case 6:
                drawLovers(g, cx, cy, half, gold, darkPurple);
                break;
            case 7:
                drawChariot(g, cx, cy, half, gold, darkPurple);
                break;
            case 8:
                drawStrength(g, cx, cy, half, gold, darkPurple);
                break;
            case 9:
                drawHermit(g, cx, cy, half, gold, darkPurple);
                break;
            case 10:
                drawWheelOfFortune(g, cx, cy, half, gold, darkPurple);
                break;
            case 11:
                drawJustice(g, cx, cy, half, gold, darkPurple);
                break;
            case 12:
                drawHangedMan(g, cx, cy, half, gold, darkPurple);
                break;
            case 13:
                drawDeath(g, cx, cy, half, gold, darkPurple);
                break;
            case 14:
                drawTemperance(g, cx, cy, half, gold, darkPurple);
                break;
            case 15:
                drawDevil(g, cx, cy, half, gold, darkPurple);
                break;
            case 16:
                drawTower(g, cx, cy, half, gold, darkPurple);
                break;
            case 17:
                drawStar(g, cx, cy, half, gold, darkPurple);
                break;
            case 18:
                drawMoon(g, cx, cy, half, gold, darkPurple);
                break;
            case 19:
                drawSun(g, cx, cy, half, gold, darkPurple);
                break;
            case 20:
                drawJudgement(g, cx, cy, half, gold, darkPurple);
                break;
            case 21:
                drawWorld(g, cx, cy, half, gold, darkPurple);
                break;
        }
    }

    private void drawFool(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(144, 190, 109));
        g.fillOval(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawOval(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawLine(cx - half / 4, cy - half / 3, cx - half / 4, cy - half / 2 - 5);
        g.drawLine(cx + half / 4, cy - half / 3, cx + half / 4, cy - half / 2 - 5);
        g.drawLine(cx - half / 2 + 3, cy + half / 3, cx - half / 2 - 5, cy + half / 2);
        g.drawLine(cx + half / 2 - 3, cy + half / 3, cx + half / 2 + 5, cy + half / 2);
    }

    private void drawMagician(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(darkPurple);
        g.fillRect(cx - half / 6, cy - half / 2, half / 3, half);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawRect(cx - half / 6, cy - half / 2, half / 3, half);
        int[] topX = {cx - half / 3, cx + half / 3, cx};
        int[] topY = {cy - half / 3, cy - half / 3, cy - half / 2 - 3};
        g.fillPolygon(topX, topY, 3);
        g.drawLine(cx, cy - half / 3, cx, cy + half / 3);
        g.drawLine(cx - half / 3, cy + half / 3, cx + half / 3, cy + half / 3);
        g.drawLine(cx - half / 3, cy - half / 6, cx + half / 3, cy - half / 6);
    }

    private void drawHighPriestess(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(200, 180, 220));
        int[] moonX = {cx + half / 3, cx - half / 6, cx + half / 6};
        int[] moonY = {cy - half / 4, cy + half / 4, cy + half / 4};
        g.fillPolygon(moonX, moonY, 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawPolygon(moonX, moonY, 3);
        g.drawOval(cx - half / 6, cy - half / 3, half / 3, half / 3);
        g.drawLine(cx - half / 6, cy - half / 6, cx + half / 6, cy - half / 6);
        g.drawLine(cx, cy - half / 6, cx, cy + half / 6);
    }

    private void drawEmpress(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(255, 182, 193));
        g.fillOval(cx - half / 3, cy - half / 2, half * 2 / 3, half);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawOval(cx - half / 3, cy - half / 2, half * 2 / 3, half);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawLine(cx, cy - half / 3, cx, cy + half / 3);
        g.drawLine(cx - half / 4, cy - half / 6, cx + half / 4, cy - half / 6);
        g.drawArc(cx - half / 6, cy + half / 6, half / 3, half / 3, 0, 180);
    }

    private void drawEmperor(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(70, 130, 180));
        g.fillRect(cx - half / 3, cy - half / 3, half * 2 / 3, half * 2 / 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawRect(cx - half / 3, cy - half / 3, half * 2 / 3, half * 2 / 3);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawLine(cx - half / 4, cy - half / 6, cx + half / 4, cy - half / 6);
        g.drawLine(cx, cy - half / 6, cx, cy + half / 4);
        g.drawLine(cx - half / 3, cy + half / 3, cx + half / 3, cy + half / 3);
    }

    private void drawHierophant(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(139, 90, 43));
        int[] hatX = {cx - half / 3, cx + half / 3, cx};
        int[] hatY = {cy + half / 4, cy + half / 4, cy - half / 2};
        g.fillPolygon(hatX, hatY, 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawPolygon(hatX, hatY, 3);
        g.drawLine(cx, cy - half / 4, cx, cy + half / 2);
        g.drawLine(cx - half / 4, cy, cx + half / 4, cy);
        g.drawLine(cx - half / 4, cy + half / 3, cx + half / 4, cy + half / 3);
    }

    private void drawLovers(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(255, 160, 180));
        g.fillOval(cx - half / 3, cy - half / 2, half / 2, half / 2);
        g.setColor(new Color(100, 150, 200));
        g.fillOval(cx + half / 6, cy - half / 2, half / 2, half / 2);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawOval(cx - half / 3, cy - half / 2, half / 2, half / 2);
        g.drawOval(cx + half / 6, cy - half / 2, half / 2, half / 2);
        int[] arrowX = {cx - 5, cx + 5, cx};
        int[] arrowY = {cy + half / 4, cy + half / 4, cy + half / 2};
        g.fillPolygon(arrowX, arrowY, 3);
        g.drawLine(cx, cy, cx, cy + half / 4);
    }

    private void drawChariot(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(100, 100, 150));
        g.fillRect(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawRect(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawLine(cx - half / 2, cy - half / 6, cx + half / 2, cy - half / 6);
        g.drawLine(cx - half / 2, cy + half / 6, cx + half / 2, cy + half / 6);
        g.drawLine(cx - half / 2, cy, cx + half / 2, cy);
        g.drawLine(cx, cy - half / 3, cx, cy + half / 3);
    }

    private void drawStrength(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(205, 133, 63));
        g.fillOval(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawOval(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawLine(cx - half / 4, cy - half / 6, cx + half / 4, cy - half / 6);
        g.drawLine(cx, cy - half / 6, cx, cy + half / 4);
        int[] maneX = {cx - half / 3, cx - half / 2, cx - half / 4};
        int[] maneY = {cy - half / 3, cy - half / 2, cy - half / 3};
        g.drawPolyline(maneX, maneY, 3);
    }

    private void drawHermit(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(100, 100, 100));
        g.fillRect(cx - half / 4, cy - half / 2, half / 2, half);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawRect(cx - half / 4, cy - half / 2, half / 2, half);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawLine(cx - half / 4, cy - half / 3, cx + half / 4, cy - half / 3);
        g.setColor(new Color(255, 215, 0));
        g.fillOval(cx - half / 8, cy - half / 2 - half / 4, half / 4, half / 4);
    }

    private void drawWheelOfFortune(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(255, 200, 100));
        g.fillOval(cx - half / 2, cy - half / 2, half, half);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawOval(cx - half / 2, cy - half / 2, half, half);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawLine(cx, cy - half / 2, cx, cy + half / 2);
        g.drawLine(cx - half / 2, cy, cx + half / 2, cy);
        g.drawLine(cx - half / 3, cy - half / 3, cx + half / 3, cy + half / 3);
        g.drawLine(cx + half / 3, cy - half / 3, cx - half / 3, cy + half / 3);
        g.setColor(darkPurple);
        g.fillOval(cx - half / 6, cy - half / 6, half / 3, half / 3);
    }

    private void drawJustice(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(180, 180, 200));
        int[] triTopX = {cx - half / 2, cx + half / 2, cx};
        int[] triTopY = {cy - half / 3, cy - half / 3, cy - half / 2};
        g.fillPolygon(triTopX, triTopY, 3);
        g.fillRect(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawPolygon(triTopX, triTopY, 3);
        g.drawRect(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawLine(cx, cy - half / 3, cx, cy + half / 3);
        g.drawLine(cx - half / 3, cy, cx + half / 3, cy);
    }

    private void drawHangedMan(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(100, 150, 200));
        g.fillRect(cx - half / 6, cy - half / 2, half / 3, half);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawRect(cx - half / 6, cy - half / 2, half / 3, half);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawLine(cx - half / 6, cy - half / 4, cx - half / 3, cy - half / 2);
        g.drawLine(cx + half / 6, cy - half / 4, cx + half / 3, cy - half / 2);
        g.drawLine(cx - half / 6, cy + half / 4, cx - half / 3, cy + half / 2);
        g.drawLine(cx + half / 6, cy + half / 4, cx + half / 3, cy + half / 2);
        g.drawLine(cx - half / 6, cy, cx + half / 6, cy);
    }

    private void drawDeath(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(50, 50, 50));
        g.fillOval(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        g.setColor(new Color(255, 255, 255));
        g.fillRect(cx - half / 4, cy - half / 3, half / 2, half * 2 / 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawOval(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        g.drawRect(cx - half / 4, cy - half / 3, half / 2, half * 2 / 3);
    }

    private void drawTemperance(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(100, 180, 200));
        int[] wingL = {cx - half / 2, cx - half / 6, cx - half / 4};
        int[] wingLY = {cy, cy - half / 3, cy + half / 3};
        g.fillPolygon(wingL, wingLY, 3);
        int[] wingR = {cx + half / 2, cx + half / 6, cx + half / 4};
        g.fillPolygon(wingR, wingLY, 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawPolygon(wingL, wingLY, 3);
        g.drawPolygon(wingR, wingLY, 3);
        g.fillOval(cx - half / 6, cy - half / 6, half / 3, half / 3);
    }

    private void drawDevil(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(80, 40, 40));
        g.fillOval(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawOval(cx - half / 2, cy - half / 3, half, half * 2 / 3);
        int[] hornL = {cx - half / 3, cx - half / 2, cx - half / 4};
        int[] hornLY = {cy - half / 3, cy - half / 2, cy - half / 3};
        g.drawPolygon(hornL, hornLY, 3);
        int[] hornR = {cx + half / 3, cx + half / 2, cx + half / 4};
        g.drawPolygon(hornR, hornLY, 3);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawLine(cx - half / 4, cy, cx + half / 4, cy);
        g.drawLine(cx, cy, cx, cy + half / 3);
    }

    private void drawTower(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(139, 90, 43));
        g.fillRect(cx - half / 4, cy - half / 2, half / 2, half);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawRect(cx - half / 4, cy - half / 2, half / 2, half);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawLine(cx - half / 4, cy - half / 2, cx - half / 6, cy - half / 3);
        g.drawLine(cx + half / 4, cy - half / 2, cx + half / 6, cy - half / 3);
        g.setColor(new Color(255, 255, 100));
        g.drawLine(cx, cy - half / 2, cx - half / 4, cy - half / 2 - half / 4);
        g.drawLine(cx, cy - half / 2, cx + half / 4, cy - half / 2 - half / 4);
    }

    private void drawStar(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(255, 215, 0));
        int[] starX = {cx, cx + half / 6, cx + half / 3, cx + half / 6, cx + half / 4, cx, cx - half / 4, cx - half / 6, cx - half / 3, cx - half / 6};
        int[] starY = {cy - half / 2, cy - half / 4, cy - half / 4, cy, cy + half / 4, cy + half / 6, cy + half / 4, cy, cy - half / 4, cy - half / 4};
        g.fillPolygon(starX, starY, 10);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawPolygon(starX, starY, 10);
        g.setColor(new Color(180, 220, 255));
        g.fillOval(cx - half / 8, cy - half / 8, half / 4, half / 4);
    }

    private void drawMoon(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(200, 200, 230));
        g.fillOval(cx - half / 2, cy - half / 2, half, half);
        g.setColor(new Color(255, 255, 220));
        g.fillOval(cx - half / 3, cy - half / 3, half * 2 / 3, half * 2 / 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawOval(cx - half / 2, cy - half / 2, half, half);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawOval(cx - half / 3, cy - half / 3, half * 2 / 3, half * 2 / 3);
        int[] star1 = {cx + half / 3, cx + half / 3 + 3, cx + half / 3 + 6};
        int[] star1Y = {cy - half / 4, cy - half / 4 - 4, cy - half / 4};
        g.drawPolyline(star1, star1Y, 3);
        int[] star2 = {cx + half / 4, cx + half / 4 + 2, cx + half / 4 + 4};
        int[] star2Y = {cy + half / 4, cy + half / 4 - 3, cy + half / 4};
        g.drawPolyline(star2, star2Y, 3);
    }

    private void drawSun(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(255, 200, 50));
        g.fillOval(cx - half / 3, cy - half / 3, half * 2 / 3, half * 2 / 3);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(2));
        g.drawOval(cx - half / 3, cy - half / 3, half * 2 / 3, half * 2 / 3);
        g.setStroke(new java.awt.BasicStroke(1));
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * i / 4;
            int x1 = cx + (int) (half / 2 * Math.cos(angle));
            int y1 = cy - (int) (half / 2 * Math.sin(angle));
            int x2 = cx + (int) (half * 0.7 * Math.cos(angle));
            int y2 = cy - (int) (half * 0.7 * Math.sin(angle));
            g.drawLine(x1, y1, x2, y2);
        }
    }

    private void drawJudgement(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(255, 220, 180));
        g.fillOval(cx - half / 3, cy - half / 2, half * 2 / 3, half / 3);
        g.fillOval(cx - half / 6, cy - half / 6, half / 3, half / 3);
        g.fillOval(cx + half / 6, cy, half / 4, half / 4);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawOval(cx - half / 3, cy - half / 2, half * 2 / 3, half / 3);
        g.drawOval(cx - half / 6, cy - half / 6, half / 3, half / 3);
        g.drawOval(cx + half / 6, cy, half / 4, half / 4);
        g.drawLine(cx, cy + half / 2, cx, cy + half / 3);
        g.drawLine(cx - half / 3, cy + half / 2, cx + half / 3, cy + half / 2);
    }

    private void drawWorld(Graphics2D g, int cx, int cy, int half, Color gold, Color darkPurple) {
        g.setColor(new Color(100, 200, 100));
        g.fillOval(cx - half / 2, cy - half / 2, half, half);
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(2));
        g.drawOval(cx - half / 2, cy - half / 2, half, half);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawOval(cx - half / 3, cy - half / 3, half * 2 / 3, half * 2 / 3);
        g.drawOval(cx - half / 6, cy - half / 6, half / 3, half / 3);
        g.setColor(darkPurple);
        g.fillOval(cx - half / 8, cy - half / 8, half / 4, half / 4);
    }

    private void drawMinorArcanaSymbol(Graphics2D g, int cx, int cy, int half, String cardName, Color gold, Color darkPurple) {
        if (cardName.startsWith("权杖")) {
            g.setColor(new Color(200, 80, 60));
            g.fillRect(cx - half / 8, cy - half / 2, half / 4, half);
            g.setColor(gold);
            g.setStroke(new java.awt.BasicStroke(1.5f));
            g.drawRect(cx - half / 8, cy - half / 2, half / 4, half);
            g.drawLine(cx, cy - half / 2, cx - half / 4, cy - half / 2 - half / 4);
            g.drawLine(cx, cy - half / 2, cx + half / 4, cy - half / 2 - half / 4);
        } else if (cardName.startsWith("圣杯")) {
            g.setColor(new Color(80, 140, 180));
            g.fillOval(cx - half / 3, cy - half / 4, half * 2 / 3, half / 2);
            g.fillRect(cx - half / 6, cy - half / 4, half / 3, half / 2);
            g.fillRect(cx - half / 8, cy, half / 4, half / 3);
            g.setColor(gold);
            g.setStroke(new java.awt.BasicStroke(1.5f));
            g.drawOval(cx - half / 3, cy - half / 4, half * 2 / 3, half / 2);
            g.drawRect(cx - half / 6, cy - half / 4, half / 3, half / 2);
            g.drawRect(cx - half / 8, cy, half / 4, half / 3);
        } else if (cardName.startsWith("宝剑")) {
            g.setColor(new Color(150, 150, 180));
            g.fillRect(cx - half / 10, cy - half / 2, half / 5, half);
            g.fillRect(cx - half / 4, cy + half / 4, half / 2, half / 6);
            g.setColor(gold);
            g.setStroke(new java.awt.BasicStroke(1.5f));
            g.drawRect(cx - half / 10, cy - half / 2, half / 5, half);
            g.drawRect(cx - half / 4, cy + half / 4, half / 2, half / 6);
            g.drawLine(cx, cy - half / 2, cx, cy + half / 4);
        } else if (cardName.startsWith("星币")) {
            g.setColor(new Color(180, 150, 80));
            g.fillOval(cx - half / 3, cy - half / 3, half * 2 / 3, half * 2 / 3);
            g.setColor(gold);
            g.setStroke(new java.awt.BasicStroke(1.5f));
            g.drawOval(cx - half / 3, cy - half / 3, half * 2 / 3, half * 2 / 3);
            g.drawOval(cx - half / 6, cy - half / 6, half / 3, half / 3);
        } else {
            g.setColor(darkPurple);
            g.fillOval(cx - half / 3, cy - half / 3, half * 2 / 3, half * 2 / 3);
            g.setColor(gold);
            g.setStroke(new java.awt.BasicStroke(1.5f));
            g.drawOval(cx - half / 3, cy - half / 3, half * 2 / 3, half * 2 / 3);
        }
    }

    private void paintCardBack(Graphics2D g, int shadowOffset, int arc) {
        int width = getWidth() - 16;
        int cardHeight = Math.min(getHeight() - 45, 120);
        
        g.setColor(new Color(40, 48, 58, 35));
        g.fillRoundRect(8, 8 + shadowOffset, width, cardHeight, arc, arc);

        Color darkPurple = new Color(45, 25, 65);
        Color mediumPurple = new Color(65, 35, 85);
        Color lightPurple = new Color(85, 55, 105);
        
        java.awt.GradientPaint gradient = new java.awt.GradientPaint(6, 6, darkPurple, 6 + width, 6 + cardHeight, mediumPurple);
        g.setPaint(gradient);
        g.fillRoundRect(6, 6, width, cardHeight, arc, arc);

        Color gold = new Color(218, 165, 32);
        Color goldLight = new Color(255, 215, 100);
        
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(3));
        g.drawRoundRect(6, 6, width, cardHeight, arc, arc);

        g.setColor(goldLight);
        g.setStroke(new java.awt.BasicStroke(1));
        g.drawRoundRect(8, 8, width - 4, cardHeight - 4, arc - 2, arc - 2);

        int margin = 16;
        int innerWidth = width - margin * 2;
        int innerHeight = cardHeight - margin * 2;
        int innerArc = arc - 8;
        
        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(2));
        g.drawRoundRect(6 + margin, 6 + margin, innerWidth, innerHeight, innerArc, innerArc);

        int centerX = 6 + width / 2;
        int centerY = 6 + cardHeight / 2;
        int symbolSize = Math.min(innerWidth, innerHeight) / 2;

        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(2));
        for (int i = 0; i < 3; i++) {
            int ringSize = symbolSize - i * 12;
            g.drawOval(centerX - ringSize / 2, centerY - ringSize / 2, ringSize, ringSize);
        }

        int triangleSize = symbolSize / 3;
        g.setColor(goldLight);
        int[] triX = {centerX, centerX + triangleSize, centerX - triangleSize};
        int[] triY = {centerY - triangleSize, centerY + triangleSize / 2, centerY + triangleSize / 2};
        g.fillPolygon(triX, triY, 3);

        g.setColor(gold);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        g.drawPolygon(triX, triY, 3);

        int starRadius = symbolSize / 2 + 8;
        drawStar(g, centerX, centerY, starRadius, 6, gold);

        int cornerSize = 20;
        drawCornerSymbol(g, 6 + margin + 8, 6 + margin + 8, cornerSize, gold);
        drawCornerSymbol(g, 6 + width - margin - 8 - cornerSize, 6 + cardHeight - margin - 8 - cornerSize, cornerSize, gold);
        drawCornerSymbol(g, 6 + margin + 8, 6 + cardHeight - margin - 8 - cornerSize, cornerSize, gold);
        drawCornerSymbol(g, 6 + width - margin - 8 - cornerSize, 6 + margin + 8, cornerSize, gold);
    }

    private void drawStar(Graphics2D g, int centerX, int centerY, int radius, int points, Color color) {
        g.setColor(color);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        
        double angle = Math.PI / 2;
        double step = Math.PI * 2 / points;
        
        int[] xPoints = new int[points];
        int[] yPoints = new int[points];
        
        for (int i = 0; i < points; i++) {
            xPoints[i] = (int) (centerX + Math.cos(angle) * radius);
            yPoints[i] = (int) (centerY - Math.sin(angle) * radius);
            angle += step;
        }
        
        g.drawPolygon(xPoints, yPoints, points);
    }

    private void drawCornerSymbol(Graphics2D g, int x, int y, int size, Color color) {
        g.setColor(color);
        g.setStroke(new java.awt.BasicStroke(1.5f));
        
        int halfSize = size / 2;
        int cx = x + halfSize;
        int cy = y + halfSize;
        int radius = halfSize - 2;
        
        g.drawOval(cx - radius, cy - radius, radius * 2, radius * 2);
        
        int[] diamondX = {cx, cx + halfSize - 4, cx, cx - halfSize + 4};
        int[] diamondY = {cy - halfSize + 4, cy, cy + halfSize - 4, cy};
        g.drawPolygon(diamondX, diamondY, 4);
    }

    public void animateFlip(float progress) {
        if (progress >= 1) {
            this.scale = 1.0f;
            this.rotation = 0f;
            if (showBack) {
                showBack = false;
            }
        } else if (progress > 0) {
            float flipAngle = progress * 180f;
            float perspectiveScale = 1.0f - (float)(Math.sin(Math.toRadians(flipAngle)) * 0.3);
            
            if (flipAngle > 90 && showBack) {
                showBack = false;
            }
            
            this.scale = perspectiveScale;
            this.rotation = 0f;
            this.alpha = 1.0f;
        }
        repaint();
    }
    
    public void animateCardFlip(float progress) {
        if (progress >= 1) {
            this.scale = 1.0f;
            this.rotation = 0f;
            if (showBack) {
                showBack = false;
            }
        } else if (progress > 0) {
            float flipAngle = progress * 180f;
            float perspectiveScale = (float) Math.cos(Math.toRadians(flipAngle));
            
            if (flipAngle > 90 && showBack) {
                showBack = false;
            }
            
            this.scale = 0.9f + perspectiveScale * 0.1f;
            this.rotation = 0f;
        }
        repaint();
    }
}