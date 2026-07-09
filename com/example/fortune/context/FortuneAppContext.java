package com.example.fortune.context;

import com.example.fortune.controller.FortuneController;
import com.example.fortune.model.FortuneMode;
import com.example.fortune.repository.FortuneImageRepository;
import com.example.fortune.service.FortuneHistoryService;
import com.example.fortune.service.FortuneService;
import com.example.fortune.strategy.FortuneStrategy;
import com.example.fortune.strategy.ImageFortuneStrategy;
import com.example.fortune.strategy.LiuYaoFortuneStrategy;
import com.example.fortune.strategy.TarotFortuneStrategy;
import com.example.fortune.view.FortuneFrame;

import java.util.EnumMap;
import java.util.Map;

public class FortuneAppContext {
    private final FortuneImageRepository imageRepository;
    private final FortuneService fortuneService;
    private final FortuneHistoryService historyService;
    private final FortuneController controller;
    private final FortuneFrame frame;

    public FortuneAppContext() {
        this.imageRepository = new FortuneImageRepository();
        
        Map<FortuneMode, FortuneStrategy> strategies = new EnumMap<>(FortuneMode.class);
        strategies.put(FortuneMode.IMAGE_ORACLE, new ImageFortuneStrategy());
        strategies.put(FortuneMode.TAROT, new TarotFortuneStrategy());
        strategies.put(FortuneMode.LIU_YAO, new LiuYaoFortuneStrategy());
        
        this.fortuneService = new FortuneService(strategies);
        this.historyService = new FortuneHistoryService();
        this.controller = new FortuneController(imageRepository, fortuneService, historyService);
        this.frame = new FortuneFrame(controller);
    }

    public FortuneImageRepository getImageRepository() {
        return imageRepository;
    }

    public FortuneService getFortuneService() {
        return fortuneService;
    }

    public FortuneHistoryService getHistoryService() {
        return historyService;
    }

    public FortuneController getController() {
        return controller;
    }

    public FortuneFrame getFrame() {
        return frame;
    }

    public void run() {
        frame.show();
    }

    public static void main(String[] args) {
        FortuneAppContext context = new FortuneAppContext();
        context.run();
    }
}