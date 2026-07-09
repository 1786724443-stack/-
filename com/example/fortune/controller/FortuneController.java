package com.example.fortune.controller;

import com.example.fortune.model.FortuneDrawType;
import com.example.fortune.model.FortuneFace;
import com.example.fortune.model.FortuneMode;
import com.example.fortune.model.FortuneResult;
import com.example.fortune.repository.FortuneImageRepository;
import com.example.fortune.service.FortuneHistoryService;
import com.example.fortune.service.FortuneService;
import com.example.fortune.view.FortuneCardLabel;
import com.example.fortune.view.FortuneFrame;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.Timer;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class FortuneController {
    private static final int ROLL_INTERVAL_MS = 80;
    private static final int ROLL_STEPS = 14;
    private static final int FLIP_STEPS = 12;
    private static final long MIN_DRAW_INTERVAL_MS = 3000;
    private long lastDrawTime = 0;

    private final FortuneImageRepository imageRepository;
    private final FortuneService fortuneService;
    private final FortuneHistoryService historyService;

    public FortuneController(FortuneImageRepository imageRepository, FortuneService fortuneService, FortuneHistoryService historyService) {
        this.imageRepository = imageRepository;
        this.fortuneService = fortuneService;
        this.historyService = historyService;
    }

    public FortuneImageRepository getImageRepository() {
        return imageRepository;
    }

    public FortuneHistoryService getHistoryService() {
        return historyService;
    }

    public boolean canDraw() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastDrawTime >= MIN_DRAW_INTERVAL_MS;
    }

    public void roll(FortuneMode mode, FortuneDrawType drawType, JLabel[] faceLabels, JTextArea resultArea, JButton rollButton) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDrawTime < MIN_DRAW_INTERVAL_MS) {
            return;
        }
        lastDrawTime = currentTime;

        rollButton.setEnabled(false);
        resultArea.setText(mode.getLoadingText());

        if (mode == FortuneMode.TAROT) {
            startTarotDrawAnimation(faceLabels, drawType, resultArea, rollButton);
        } else {
            startNormalRoll(mode, drawType, faceLabels, resultArea, rollButton);
        }
    }

    private void startNormalRoll(FortuneMode mode, FortuneDrawType drawType, JLabel[] faceLabels, JTextArea resultArea, JButton rollButton) {
        final FortuneMode finalMode = mode;
        final FortuneDrawType finalDrawType = drawType;
        final JLabel[] finalFaceLabels = faceLabels;
        final JTextArea finalResultArea = resultArea;
        final JButton finalRollButton = rollButton;

        final int[] step = {0};
        Timer timer = new Timer(ROLL_INTERVAL_MS, null);
        timer.addActionListener(event -> {
            List<FortuneFace> faces = imageRepository.getFaces(finalMode);
            int[] indexes = fortuneService.randomIndexes(finalFaceLabels.length, faces.size());
            updateLabels(finalFaceLabels, faces, indexes, false);
            step[0]++;

            if (step[0] >= ROLL_STEPS) {
                timer.stop();
                finishRoll(finalMode, finalDrawType, finalFaceLabels, finalResultArea, finalRollButton);
            }
        });
        timer.start();
    }

    private void startTarotDrawAnimation(JLabel[] faceLabels, FortuneDrawType drawType, JTextArea resultArea, JButton rollButton) {
        final JLabel[] finalFaceLabels = faceLabels;
        final FortuneDrawType finalDrawType = drawType;
        final JTextArea finalResultArea = resultArea;
        final JButton finalRollButton = rollButton;

        final int[] targetX = new int[faceLabels.length];
        final int[] targetY = new int[faceLabels.length];
        int maxX = 0;
        int minX = Integer.MAX_VALUE;
        int avgY = 0;

        for (int i = 0; i < faceLabels.length; i++) {
            targetX[i] = faceLabels[i].getX();
            targetY[i] = faceLabels[i].getY();
            maxX = Math.max(maxX, targetX[i]);
            minX = Math.min(minX, targetX[i]);
            avgY += targetY[i];
        }
        avgY /= faceLabels.length;

        final int deckX = (minX + maxX) / 2;
        final int deckY = avgY - 150;

        for (int i = 0; i < faceLabels.length; i++) {
            if (faceLabels[i] instanceof FortuneCardLabel) {
                FortuneCardLabel cardLabel = (FortuneCardLabel) faceLabels[i];
                cardLabel.setLocation(deckX, deckY);
                cardLabel.setShowBack(true);
                cardLabel.setScale(0.7f);
                cardLabel.setRotation(0f);
                cardLabel.setAlpha(0f);
                cardLabel.setSelectedCard(false);
                cardLabel.repaint();
            }
            faceLabels[i].setIcon(null);
            faceLabels[i].setText("");
        }

        final int[] cardIndex = {0};
        final int[] step = {0};
        final int stepsPerCard = 20;

        Timer timer = new Timer(40, null);
        timer.addActionListener(event -> {
            int currentCard = cardIndex[0];
            int cardStep = step[0] % stepsPerCard;

            float progress = (float) cardStep / stepsPerCard;

            for (int i = 0; i < finalFaceLabels.length; i++) {
                if (finalFaceLabels[i] instanceof FortuneCardLabel) {
                    FortuneCardLabel card = (FortuneCardLabel) finalFaceLabels[i];

                    if (i == currentCard) {
                        card.setAlpha(1f);

                        int currentX = deckX + (int) ((targetX[i] - deckX) * progress);
                        int currentY = deckY + (int) ((targetY[i] - deckY) * progress);

                        float scale = 0.7f + (0.3f * progress);

                        card.setLocation(currentX, currentY);
                        card.setScale(scale);
                    } else if (i < currentCard) {
                        card.setAlpha(1f);
                    } else {
                        card.setAlpha(0f);
                    }
                    card.repaint();
                }
            }

            step[0]++;

            if (cardStep == stepsPerCard - 1) {
                if (currentCard < finalFaceLabels.length && finalFaceLabels[currentCard] instanceof FortuneCardLabel) {
                    FortuneCardLabel card = (FortuneCardLabel) finalFaceLabels[currentCard];
                    card.setLocation(targetX[currentCard], targetY[currentCard]);
                    card.setScale(1.0f);
                    card.repaint();
                }
                cardIndex[0]++;
            }

            if (cardIndex[0] >= finalFaceLabels.length) {
                timer.stop();
                finishRoll(FortuneMode.TAROT, finalDrawType, finalFaceLabels, finalResultArea, finalRollButton);
            }
        });
        timer.start();
    }

    public void rollStep(FortuneMode mode, JLabel[] faceLabels, int step, int totalSteps) {
        if (mode == FortuneMode.TAROT) {
            for (JLabel label : faceLabels) {
                if (label instanceof FortuneCardLabel) {
                    FortuneCardLabel cardLabel = (FortuneCardLabel) label;
                    cardLabel.setScale(1.0f);
                    cardLabel.setShowBack(true);
                }
            }
        } else {
            List<FortuneFace> faces = imageRepository.getFaces(mode);
            int[] indexes = fortuneService.randomIndexes(faceLabels.length, faces.size());

            for (int i = 0; i < faceLabels.length; i++) {
                FortuneFace face = faces.get(indexes[i]);
                faceLabels[i].setIcon(face.getIcon());
                faceLabels[i].setText(FortuneFrame.formatFaceLabel(face.getName() + "：" + face.getKeyword()));
                faceLabels[i].setEnabled(true);

                if (faceLabels[i] instanceof FortuneCardLabel) {
                    FortuneCardLabel cardLabel = (FortuneCardLabel) faceLabels[i];
                    cardLabel.setScale(1.0f);
                }
            }
        }
    }

    private void updateLabelsWithScale(JLabel[] faceLabels, List<FortuneFace> faces, int[] indexes, float scale) {
        for (int i = 0; i < faceLabels.length; i++) {
            FortuneFace face = faces.get(indexes[i]);
            faceLabels[i].setIcon(face.getIcon());
            faceLabels[i].setText(FortuneFrame.formatFaceLabel(face.getName() + "：" + face.getKeyword()));
            faceLabels[i].setEnabled(true);

            if (faceLabels[i] instanceof FortuneCardLabel) {
                FortuneCardLabel cardLabel = (FortuneCardLabel) faceLabels[i];
                cardLabel.setScale(scale);
            }
        }
    }

    public void refreshInitialFaces(FortuneMode mode, JLabel[] faceLabels) {
        if (mode == FortuneMode.TAROT) {
            for (JLabel label : faceLabels) {
                if (label instanceof FortuneCardLabel) {
                    FortuneCardLabel cardLabel = (FortuneCardLabel) label;
                    cardLabel.setShowBack(true);
                    cardLabel.setScale(1.0f);
                    cardLabel.setRotation(0f);
                    cardLabel.setAlpha(1f);
                    cardLabel.setSelectedCard(false);
                }
                label.setIcon(null);
                label.setText("");
            }
        } else {
            List<FortuneFace> faces = imageRepository.getFaces(mode);
            int[] indexes = new int[faceLabels.length];
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = i;
            }
            updateLabels(faceLabels, faces, indexes, false);
            resetAnimations(faceLabels);
        }
    }

    private void resetAnimations(JLabel[] faceLabels) {
        for (JLabel label : faceLabels) {
            if (label instanceof FortuneCardLabel) {
                FortuneCardLabel cardLabel = (FortuneCardLabel) label;
                cardLabel.setShowBack(false);
                cardLabel.setScale(1.0f);
                cardLabel.setRotation(0f);
                cardLabel.setAlpha(1f);
            }
        }
    }

    public void finishRoll(FortuneMode mode, FortuneDrawType drawType, JLabel[] faceLabels, JTextArea resultArea, JButton rollButton) {
        List<FortuneFace> faces = imageRepository.getFaces(mode);
        Random source = drawType.isDaily() ? new Random(dailySeed(mode)) : null;
        int[] indexes = mode == FortuneMode.TAROT
                ? randomUniqueIndexes(mode, faces.size(), source)
                : randomIndexes(mode, faces.size(), source);
        boolean[] reversed = mode == FortuneMode.TAROT ? randomReversedFlags(indexes.length, source) : null;

        FortuneResult result = fortuneService.createResult(mode, faces, indexes, reversed);
        historyService.add(mode, drawType, result);

        final JTextArea finalResultArea = resultArea;
        final FortuneResult finalResult = result;

        if (mode == FortuneMode.TAROT) {
            animateReveal(faceLabels, faces, indexes, reversed, rollButton);

            Timer resultTimer = new Timer(2000, null);
            resultTimer.addActionListener(event -> {
                resultTimer.stop();
                finalResultArea.setText(finalResult.getText());
            });
            resultTimer.start();
        } else {
            for (int i = 0; i < faceLabels.length; i++) {
                FortuneFace face = faces.get(indexes[i]);
                faceLabels[i].setIcon(face.getIcon());
                String orientation = reversed == null ? "" : (reversed[i] ? "（逆位）" : "（正位）");
                faceLabels[i].setText(FortuneFrame.formatFaceLabel(face.getName() + orientation + "：" + face.getKeyword()));
                if (faceLabels[i] instanceof FortuneCardLabel) {
                    FortuneCardLabel cardLabel = (FortuneCardLabel) faceLabels[i];
                    cardLabel.setCardInfo(face.getName(), face.getKeyword());
                    cardLabel.setSelectedCard(true);
                    cardLabel.setScale(1.0f);
                    cardLabel.setRotation(0f);
                }
            }
            resultArea.setText(result.getText());
            rollButton.setEnabled(true);
        }
    }

    private void animateReveal(JLabel[] faceLabels, List<FortuneFace> faces, int[] indexes, boolean[] reversed, JButton rollButton) {
        final JLabel[] finalFaceLabels = faceLabels;
        final List<FortuneFace> finalFaces = faces;
        final int[] finalIndexes = indexes;
        final boolean[] finalReversed = reversed;
        final JButton finalRollButton = rollButton;

        final int[] step = {0};
        final int totalSteps = FLIP_STEPS * 2;

        Timer timer = new Timer(60, null);
        timer.addActionListener(event -> {
            step[0]++;
            float progress = (float) step[0] / totalSteps;

            for (int i = 0; i < finalFaceLabels.length; i++) {
                float cardProgress = Math.max(0, Math.min(1, (progress * finalFaceLabels.length) - i * 0.15f));

                FortuneCardLabel cardLabel = (FortuneCardLabel) finalFaceLabels[i];
                if (cardProgress >= 1) {
                    cardLabel.setShowBack(false);
                    FortuneFace face = finalFaces.get(finalIndexes[i]);
                    cardLabel.setCardInfo(face.getName(), face.getKeyword());
                    finalFaceLabels[i].setIcon(face.getIcon());
                    String orientation = finalReversed == null ? "" : (finalReversed[i] ? "（逆位）" : "（正位）");
                    finalFaceLabels[i].setText(FortuneFrame.formatFaceLabel(face.getName() + orientation + "：" + face.getKeyword()));
                    cardLabel.setSelectedCard(true);
                    cardLabel.setScale(1.1f);
                    cardLabel.setRotation(0f);
                    cardLabel.setAlpha(1f);
                } else if (cardProgress > 0) {
                    cardLabel.animateCardFlip(cardProgress);
                }
            }

            if (step[0] >= totalSteps + finalFaceLabels.length * 2) {
                timer.stop();
                for (JLabel label : finalFaceLabels) {
                    if (label instanceof FortuneCardLabel) {
                        ((FortuneCardLabel) label).setScale(1.0f);
                    }
                }
                finalRollButton.setEnabled(true);
            }
        });
        timer.start();
    }

    private int[] randomIndexes(FortuneMode mode, int faceCount, Random source) {
        if (source == null) {
            return fortuneService.randomIndexes(mode.getDrawCount(), faceCount);
        }
        return fortuneService.randomIndexes(mode.getDrawCount(), faceCount, source);
    }

    private int[] randomUniqueIndexes(FortuneMode mode, int faceCount, Random source) {
        if (source == null) {
            return fortuneService.randomUniqueIndexes(mode.getDrawCount(), faceCount);
        }
        return fortuneService.randomUniqueIndexes(mode.getDrawCount(), faceCount, source);
    }

    private boolean[] randomReversedFlags(int count, Random source) {
        if (source == null) {
            return fortuneService.randomReversedFlags(count);
        }
        return fortuneService.randomReversedFlags(count, source);
    }

    private long dailySeed(FortuneMode mode) {
        return Objects.hash(LocalDate.now(), mode.name());
    }

    private static void updateLabels(JLabel[] faceLabels, List<FortuneFace> faces, int[] indexes, boolean selected) {
        updateLabels(faceLabels, faces, indexes, null, selected);
    }

    private static void updateLabels(JLabel[] faceLabels, List<FortuneFace> faces, int[] indexes, boolean[] reversed, boolean selected) {
        for (int i = 0; i < faceLabels.length; i++) {
            FortuneFace face = faces.get(indexes[i]);
            faceLabels[i].setIcon(face.getIcon());
            String orientation = reversed == null ? "" : (reversed[i] ? "（逆位）" : "（正位）");
            faceLabels[i].setText(FortuneFrame.formatFaceLabel(face.getName() + orientation + "：" + face.getKeyword()));
            if (faceLabels[i] instanceof FortuneCardLabel) {
                ((FortuneCardLabel) faceLabels[i]).setSelectedCard(selected);
            }
        }
    }
}