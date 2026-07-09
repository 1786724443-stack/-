package com.example.fortune.view;

import com.example.fortune.controller.FortuneController;
import com.example.fortune.model.FortuneDrawType;
import com.example.fortune.model.FortuneHistoryRecord;
import com.example.fortune.model.FortuneMode;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Icon;
import javax.imageio.ImageIO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FortuneFrame {
    private final FortuneController controller;

    private final JFrame frame = new JFrame("多模式图片运势生成器");
    private final JTextArea resultArea = new JTextArea();
    private final JComboBox<FortuneMode> modeBox = new JComboBox<>(FortuneMode.values());
    private final JComboBox<FortuneDrawType> drawTypeBox = new JComboBox<>(FortuneDrawType.values());
    private JPanel facePanel;
    private JPanel cardContainer;
    private JButton actionButton;
    private JLabel[] faceLabels = new JLabel[0];
    private JLabel progressLabel;
    private FortuneCardLabel deckCard;

    public FortuneFrame(FortuneController controller) {
        this.controller = controller;
    }

    public void show() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(600, 500));
        frame.setPreferredSize(new Dimension(900, 700));
        frame.setContentPane(createContent());
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
        applyMode(getSelectedMode());
    }

    private JPanel createContent() {
        JPanel root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        root.setBackground(new Color(245, 247, 250));

        JLabel title = new JLabel("多模式图片运势生成器", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        title.setForeground(new Color(34, 45, 50));
        title.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
        root.add(title);
        root.add(Box.createVerticalStrut(8));

        JPanel topPanel = new JPanel(new BorderLayout(8, 0));
        topPanel.setOpaque(false);
        topPanel.add(createFacePanel(), BorderLayout.CENTER);
        topPanel.add(createResultPanel(), BorderLayout.EAST);
        root.add(topPanel);
        root.add(Box.createVerticalStrut(8));

        root.add(createToolPanel());
        return root;
    }

    private JPanel createFacePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        cardContainer = new JPanel();
        cardContainer.setOpaque(false);

        deckCard = new FortuneCardLabel("");
        deckCard.setPreferredSize(new Dimension(110, 165));
        deckCard.setMinimumSize(new Dimension(95, 145));
        deckCard.setMaximumSize(new Dimension(130, 185));
        deckCard.setTarot(true);
        deckCard.setShowBack(true);
        deckCard.setBorder(BorderFactory.createEmptyBorder(8, 8, 30, 8));

        JPanel cardsPanel = new JPanel(new BorderLayout());
        cardsPanel.setOpaque(false);
        cardsPanel.add(cardContainer, BorderLayout.CENTER);

        panel.add(cardsPanel, BorderLayout.CENTER);
        
        JPanel deckPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        deckPanel.setOpaque(false);
        deckPanel.add(deckCard);
        panel.add(deckPanel, BorderLayout.NORTH);

        facePanel = panel;
        rebuildFaceLabels(getSelectedMode());
        return panel;
    }

    private JScrollPane createResultPanel() {
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 15));
        resultArea.setText(getSelectedMode().getInitialText());

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setPreferredSize(new Dimension(350, 400));
        scrollPane.setMinimumSize(new Dimension(250, 300));
        return scrollPane;
    }

    private JPanel createToolPanel() {
        JPanel toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
        toolPanel.setOpaque(false);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        topRow.setOpaque(false);
        
        modeBox.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        modeBox.setPreferredSize(new Dimension(110, 30));
        modeBox.addActionListener(event -> applyMode(getSelectedMode()));
        topRow.add(modeBox);

        drawTypeBox.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        drawTypeBox.setPreferredSize(new Dimension(90, 30));
        topRow.add(drawTypeBox);

        actionButton = new JButton(getSelectedMode().getActionText());
        actionButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        actionButton.setBackground(new Color(66, 133, 244));
        actionButton.setForeground(Color.WHITE);
        actionButton.setBorderPainted(false);
        actionButton.setFocusPainted(false);
        actionButton.setPreferredSize(new Dimension(130, 35));
        actionButton.addActionListener(event -> animateRoll());
        topRow.add(actionButton);

        JButton viewButton = createToolButton("查看图片");
        viewButton.addActionListener(event -> viewImages());
        topRow.add(viewButton);

        JButton saveTextButton = createToolButton("保存文本");
        saveTextButton.addActionListener(event -> saveResultText());
        topRow.add(saveTextButton);

        toolPanel.add(topRow);

        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        bottomRow.setOpaque(false);

        JButton saveImageButton = createToolButton("生成图片");
        saveImageButton.addActionListener(event -> saveResultImage());
        bottomRow.add(saveImageButton);

        JButton historyButton = createToolButton("历史记录");
        historyButton.addActionListener(event -> showHistory());
        bottomRow.add(historyButton);

        JButton copyButton = createToolButton("复制结果");
        copyButton.addActionListener(event -> copyToClipboard());
        bottomRow.add(copyButton);

        progressLabel = new JLabel("");
        progressLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        progressLabel.setForeground(new Color(66, 133, 244));
        bottomRow.add(Box.createHorizontalStrut(10));
        bottomRow.add(progressLabel);

        toolPanel.add(bottomRow);

        return toolPanel;
    }

    private JButton createToolButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        button.setPreferredSize(new Dimension(100, 30));
        return button;
    }

    private void animateRoll() {
        if (!actionButton.isEnabled()) {
            return;
        }
        
        if (!controller.canDraw()) {
            return;
        }
        
        FortuneMode mode = getSelectedMode();
        actionButton.setEnabled(false);
        resultArea.setText(mode.getLoadingText());
        progressLabel.setText("正在抽取中...");

        controller.roll(mode, getSelectedDrawType(), faceLabels, resultArea, actionButton);
    }

    public void onRollFinished() {
        progressLabel.setText("");
        actionButton.setEnabled(true);
    }

    private void copyToClipboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("运势方式：").append(getSelectedMode()).append("\n\n");
        for (JLabel label : faceLabels) {
            sb.append(toPlainLabelText(label.getText())).append("\n");
        }
        sb.append("\n").append(resultArea.getText());

        try {
            java.awt.Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new java.awt.datatransfer.StringSelection(sb.toString()), null);
            JOptionPane.showMessageDialog(frame, "结果已复制到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "复制失败：" + e.getMessage(), "复制失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void applyMode(FortuneMode mode) {
        rebuildFaceLabels(mode);
        if (actionButton != null) {
            actionButton.setText(mode.getActionText());
        }
        controller.refreshInitialFaces(mode, faceLabels);
        resultArea.setText(mode.getInitialText());
        progressLabel.setText("");
        
        if (deckCard != null) {
            deckCard.setVisible(mode == FortuneMode.TAROT);
        }
    }

    private void rebuildFaceLabels(FortuneMode mode) {
        int count = mode.getDrawCount();
        faceLabels = new JLabel[count];
        cardContainer.removeAll();
        boolean isTarotMode = mode == FortuneMode.TAROT;

        if (count == 6) {
            cardContainer.setLayout(new java.awt.GridLayout(2, 3, 12, 12));
            for (int i = 0; i < count; i++) {
                FortuneCardLabel card = new FortuneCardLabel(formatFaceLabel("图片 " + (i + 1)));
                card.setPreferredSize(new Dimension(110, 130));
                card.setMinimumSize(new Dimension(90, 110));
                card.setTarot(false);
                card.setShowBack(false);
                faceLabels[i] = card;
                cardContainer.add(faceLabels[i]);
            }
        } else {
            cardContainer.setLayout(new java.awt.GridBagLayout());
            java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
            gbc.fill = java.awt.GridBagConstraints.NONE;
            gbc.anchor = java.awt.GridBagConstraints.CENTER;
            gbc.weightx = 1.0;
            gbc.weighty = 0.0;
            gbc.insets = new java.awt.Insets(5, 10, 5, 10);

            for (int i = 0; i < count; i++) {
                FortuneCardLabel card = new FortuneCardLabel(formatFaceLabel("图片 " + (i + 1)));
                card.setPreferredSize(new Dimension(110, 185));
                card.setMinimumSize(new Dimension(95, 165));
                card.setMaximumSize(new Dimension(130, 205));
                card.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
                card.setBorder(javax.swing.BorderFactory.createEmptyBorder(8, 8, 40, 8));
                card.setTarot(isTarotMode);
                card.setShowBack(isTarotMode);
                faceLabels[i] = card;
                gbc.gridx = i;
                gbc.gridy = 0;
                cardContainer.add(card, gbc);
            }
        }

        cardContainer.revalidate();
        cardContainer.repaint();
        facePanel.revalidate();
        facePanel.repaint();
    }

    private FortuneMode getSelectedMode() {
        Object selected = modeBox.getSelectedItem();
        return selected instanceof FortuneMode ? (FortuneMode) selected : FortuneMode.IMAGE_ORACLE;
    }

    private FortuneDrawType getSelectedDrawType() {
        Object selected = drawTypeBox.getSelectedItem();
        return selected instanceof FortuneDrawType ? (FortuneDrawType) selected : FortuneDrawType.DAILY;
    }

    private void viewImages() {
        FortuneMode mode = getSelectedMode();
        List<String> existingImages = controller.getImageRepository().getFaceNames(mode);
        
        if (existingImages.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "当前模式没有可查看的图片", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JPanel imagePanel = new JPanel(new java.awt.GridLayout(0, 3, 10, 10));
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (String imageName : existingImages) {
            FortuneCardLabel card = new FortuneCardLabel(imageName);
            card.setPreferredSize(new Dimension(100, 120));
            card.setTarot(false);
            card.setShowBack(false);
            
            Icon icon = controller.getImageRepository().getFaceIcon(mode, imageName);
            if (icon != null) {
                card.setIcon(icon);
            }
            
            final String finalImageName = imageName;
            final FortuneCardLabel finalCard = card;
            
            javax.swing.JPopupMenu popupMenu = new javax.swing.JPopupMenu();
            javax.swing.JMenuItem resetItem = new javax.swing.JMenuItem("重置图片");
            resetItem.addActionListener(event -> {
                resetImageByName(mode, finalImageName, finalCard);
            });
            popupMenu.add(resetItem);
            
            card.setComponentPopupMenu(popupMenu);
            
            card.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    replaceImageByName(mode, finalImageName, finalCard);
                }
                
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    finalCard.setBorder(BorderFactory.createLineBorder(new Color(66, 133, 244), 2));
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    finalCard.setBorder(BorderFactory.createEmptyBorder(8, 8, 40, 8));
                }
            });
            
            imagePanel.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(imagePanel);
        scrollPane.setPreferredSize(new Dimension(480, 350));
        
        JOptionPane.showMessageDialog(frame, scrollPane, "查看图片（点击图片可替换）", JOptionPane.INFORMATION_MESSAGE);
    }

    private void replaceImageByName(FortuneMode mode, String imageName, FortuneCardLabel card) {
        int confirm = JOptionPane.showConfirmDialog(frame, 
            "确定要替换图片：" + imageName + " 吗？", 
            "确认替换", 
            JOptionPane.OK_CANCEL_OPTION);
        
        if (confirm != JOptionPane.OK_OPTION) return;

        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileFilter(new FileNameExtensionFilter("图片文件", "png", "jpg", "jpeg", "bmp", "gif"));

        int result = chooser.showOpenDialog(frame);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File selectedFile = chooser.getSelectedFile();
        if (selectedFile == null) return;

        try {
            boolean success = controller.getImageRepository().replaceImage(mode, imageName, selectedFile);
            if (success) {
                Icon newIcon = controller.getImageRepository().getFaceIcon(mode, imageName);
                if (newIcon != null && card != null) {
                    card.setIcon(newIcon);
                    card.repaint();
                }
                controller.refreshInitialFaces(mode, faceLabels);
                resultArea.setText("已成功替换图片：" + imageName);
            } else {
                JOptionPane.showMessageDialog(frame, "替换失败，请重试", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "图片替换失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetImageByName(FortuneMode mode, String imageName, FortuneCardLabel card) {
        int confirm = JOptionPane.showConfirmDialog(frame, 
            "确定要重置图片：" + imageName + " 吗？", 
            "确认重置", 
            JOptionPane.OK_CANCEL_OPTION);
        
        if (confirm != JOptionPane.OK_OPTION) return;

        boolean success = controller.getImageRepository().restoreDefaultImage(mode, imageName);
        if (success) {
            Icon newIcon = controller.getImageRepository().getFaceIcon(mode, imageName);
            if (newIcon != null && card != null) {
                card.setIcon(newIcon);
                card.repaint();
            }
            controller.refreshInitialFaces(mode, faceLabels);
            resultArea.setText("已成功重置图片：" + imageName);
        } else {
            JOptionPane.showMessageDialog(frame, "重置失败，请重试", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void saveResultText() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("文本文件", "txt"));
        chooser.setSelectedFile(new File(getSelectedMode() + "_运势结果.txt"));

        int result = chooser.showSaveDialog(frame);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".txt")) {
            file = new File(file.getParent(), file.getName() + ".txt");
        }

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            writer.println("运势方式：" + getSelectedMode());
            writer.println();
            for (JLabel label : faceLabels) {
                writer.println(toPlainLabelText(label.getText()));
            }
            writer.println();
            writer.println(resultArea.getText());
            JOptionPane.showMessageDialog(frame, "已保存文本：" + file.getAbsolutePath(), "保存成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "保存失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveResultImage() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("PNG 图片", "png"));
        chooser.setSelectedFile(new File(getSelectedMode() + "_运势结果.png"));

        int result = chooser.showSaveDialog(frame);
        if (result != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".png")) {
            file = new File(file.getParent(), file.getName() + ".png");
        }

        try {
            ImageIO.write(createResultImage(), "png", file);
            JOptionPane.showMessageDialog(frame, "已生成图片：" + file.getAbsolutePath(), "保存成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(frame, ex.getMessage(), "保存失败", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showHistory() {
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        historyArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));

        JButton dailyButton = new JButton("每日运势");
        dailyButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        dailyButton.addActionListener(event -> {
            if (controller.getHistoryService().isDailyEmpty()) {
                historyArea.setText("还没有每日运势历史记录。");
            } else {
                StringBuilder builder = new StringBuilder();
                List<FortuneHistoryRecord> records = controller.getHistoryService().getDailyRecords();
                for (int i = 0; i < records.size(); i++) {
                    if (i > 0) builder.append("\n\n");
                    builder.append(records.get(i).toDisplayText(i + 1));
                }
                historyArea.setText(builder.toString());
            }
            historyArea.setCaretPosition(0);
        });

        JButton endlessButton = new JButton("无尽抽取");
        endlessButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        endlessButton.addActionListener(event -> {
            if (controller.getHistoryService().isEndlessEmpty()) {
                historyArea.setText("还没有无尽抽取历史记录。");
            } else {
                StringBuilder builder = new StringBuilder();
                List<FortuneHistoryRecord> records = controller.getHistoryService().getEndlessRecords();
                for (int i = 0; i < records.size(); i++) {
                    if (i > 0) builder.append("\n\n");
                    builder.append(records.get(i).toDisplayText(i + 1));
                }
                historyArea.setText(builder.toString());
            }
            historyArea.setCaretPosition(0);
        });

        JButton allButton = new JButton("全部记录");
        allButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        allButton.addActionListener(event -> {
            if (controller.getHistoryService().isEmpty()) {
                historyArea.setText("还没有历史记录。完成一次抽取后会自动保存最近 10 次结果。");
            } else {
                StringBuilder builder = new StringBuilder();
                List<FortuneHistoryRecord> dailyRecords = controller.getHistoryService().getDailyRecords();
                if (!dailyRecords.isEmpty()) {
                    builder.append("=== 每日运势 ===\n");
                    for (int i = 0; i < dailyRecords.size(); i++) {
                        if (i > 0) builder.append("\n\n");
                        builder.append(dailyRecords.get(i).toDisplayText(i + 1));
                    }
                }
                List<FortuneHistoryRecord> endlessRecords = controller.getHistoryService().getEndlessRecords();
                if (!endlessRecords.isEmpty()) {
                    if (builder.length() > 0) builder.append("\n\n");
                    builder.append("=== 无尽抽取 ===\n");
                    for (int i = 0; i < endlessRecords.size(); i++) {
                        if (i > 0) builder.append("\n\n");
                        builder.append(endlessRecords.get(i).toDisplayText(i + 1));
                    }
                }
                historyArea.setText(builder.toString());
            }
            historyArea.setCaretPosition(0);
        });

        if (controller.getHistoryService().isEmpty()) {
            historyArea.setText("还没有历史记录。完成一次抽取后会自动保存最近 10 次结果。");
        } else {
            StringBuilder builder = new StringBuilder();
            List<FortuneHistoryRecord> dailyRecords = controller.getHistoryService().getDailyRecords();
            if (!dailyRecords.isEmpty()) {
                builder.append("=== 每日运势 ===\n");
                for (int i = 0; i < dailyRecords.size(); i++) {
                    if (i > 0) builder.append("\n\n");
                    builder.append(dailyRecords.get(i).toDisplayText(i + 1));
                }
            }
            List<FortuneHistoryRecord> endlessRecords = controller.getHistoryService().getEndlessRecords();
            if (!endlessRecords.isEmpty()) {
                if (builder.length() > 0) builder.append("\n\n");
                builder.append("=== 无尽抽取 ===\n");
                for (int i = 0; i < endlessRecords.size(); i++) {
                    if (i > 0) builder.append("\n\n");
                    builder.append(endlessRecords.get(i).toDisplayText(i + 1));
                }
            }
            historyArea.setText(builder.toString());
            historyArea.setCaretPosition(0);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        buttonPanel.add(dailyButton);
        buttonPanel.add(endlessButton);
        buttonPanel.add(allButton);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(8));
        
        JScrollPane scrollPane = new JScrollPane(historyArea);
        scrollPane.setPreferredSize(new Dimension(500, 350));
        contentPanel.add(scrollPane);

        JOptionPane.showMessageDialog(frame, contentPanel, "历史记录", JOptionPane.INFORMATION_MESSAGE);
    }

    private BufferedImage createResultImage() {
        int width = 850;
        int margin = 35;
        int cardSize = 100;
        int cardGap = 15;
        int labelHeight = 40;
        int cardStartY = 100;
        
        int rows = (faceLabels.length + 2) / 3;
        int cardsHeight = rows * (cardSize + cardGap) + labelHeight;
        int textTop = cardStartY + cardsHeight + 35;
        int lineHeight = 28;

        BufferedImage measureImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D measureGraphics = measureImage.createGraphics();
        Font textFont = new Font("Microsoft YaHei", Font.PLAIN, 16);
        measureGraphics.setFont(textFont);
        List<String> resultLines = wrapText(resultArea.getText(), measureGraphics.getFontMetrics(), width - margin * 2);
        measureGraphics.dispose();

        int height = textTop + resultLines.size() * lineHeight + margin + 20;
        BufferedImage image = new BufferedImage(width, Math.max(height, 500), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(245, 247, 250));
        g.fillRect(0, 0, width, height);

        g.setColor(new Color(34, 45, 50));
        g.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        g.drawString("多模式图片运势生成器", margin, 48);
        g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        g.drawString("运势方式：" + getSelectedMode(), margin, 76);

        drawFaceCards(g, margin, cardStartY, cardSize, cardGap);

        g.setFont(textFont);
        g.setColor(new Color(42, 48, 56));
        int y = textTop;
        for (String line : resultLines) {
            g.drawString(line, margin, y);
            y += lineHeight;
        }
        g.dispose();
        return image;
    }

    private void drawFaceCards(Graphics2D g, int startX, int startY, int cardSize, int cardGap) {
        for (int i = 0; i < faceLabels.length; i++) {
            int row = i / 3;
            int column = i % 3;
            int x = startX + column * (cardSize + cardGap);
            int y = startY + row * (cardSize + cardGap);

            g.setColor(Color.WHITE);
            g.fillRoundRect(x, y, cardSize, cardSize, 8, 8);
            g.setColor(new Color(210, 216, 222));
            g.drawRoundRect(x, y, cardSize, cardSize, 8, 8);

            Icon icon = faceLabels[i].getIcon();
            if (icon != null) {
                icon.paintIcon(frame, g, x + (cardSize - icon.getIconWidth()) / 2, y + 8);
            }

            g.setColor(new Color(34, 45, 50));
            g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
            drawCenteredString(g, toPlainLabelText(faceLabels[i].getText()), x, y + cardSize - 12, cardSize);
        }
    }

    private void drawCenteredString(Graphics2D g, String text, int x, int y, int width) {
        FontMetrics metrics = g.getFontMetrics();
        int textX = x + (width - metrics.stringWidth(text)) / 2;
        g.drawString(text, textX, y);
    }

    private List<String> wrapText(String text, FontMetrics metrics, int maxWidth) {
        List<String> lines = new ArrayList<>();
        for (String rawLine : text.split("\\R")) {
            if (rawLine.isEmpty()) {
                lines.add("");
                continue;
            }
            StringBuilder line = new StringBuilder();
            for (char ch : rawLine.toCharArray()) {
                if (metrics.stringWidth(line.toString() + ch) > maxWidth && line.length() > 0) {
                    lines.add(line.toString());
                    line.setLength(0);
                }
                line.append(ch);
            }
            lines.add(line.toString());
        }
        return lines;
    }

    public static String formatFaceLabel(String text) {
        int splitIndex = text.indexOf("：");
        if (splitIndex < 0) {
            return "<html><div style='text-align:center;'>" + escapeHtml(text) + "</div></html>";
        }
        String name = text.substring(0, splitIndex);
        String keyword = text.substring(splitIndex + 1);
        return "<html><div style='text-align:center;'>" + escapeHtml(name) + "<br>" + escapeHtml(keyword) + "</div></html>";
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private static String toPlainLabelText(String text) {
        return text.replace("<html><div style='text-align:center;'>", "")
                .replace("</div></html>", "").replace("<br>", "：")
                .replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&");
    }
}