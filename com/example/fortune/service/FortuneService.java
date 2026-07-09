package com.example.fortune.service;

import com.example.fortune.model.FortuneFace;
import com.example.fortune.model.FortuneMode;
import com.example.fortune.model.FortuneResult;
import com.example.fortune.strategy.FortuneStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FortuneService {
    private final Random random = new Random();
    private final Map<FortuneMode, FortuneStrategy> strategies;

    public FortuneService(Map<FortuneMode, FortuneStrategy> strategies) {
        this.strategies = strategies;
    }

    public int[] randomIndexes(int count, int faceCount) {
        return randomIndexes(count, faceCount, random);
    }

    public int[] randomIndexes(int count, int faceCount, Random source) {
        int[] indexes = new int[count];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = source.nextInt(faceCount);
        }
        return indexes;
    }

    public int[] randomUniqueIndexes(int count, int faceCount) {
        return randomUniqueIndexes(count, faceCount, random);
    }

    public int[] randomUniqueIndexes(int count, int faceCount, Random source) {
        if (count > faceCount) {
            return randomIndexes(count, faceCount, source);
        }

        List<Integer> pool = new ArrayList<>();
        for (int i = 0; i < faceCount; i++) {
            pool.add(i);
        }
        Collections.shuffle(pool, source);

        int[] indexes = new int[count];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = pool.get(i);
        }
        return indexes;
    }

    public FortuneResult createResult(FortuneMode mode, List<FortuneFace> allFaces, int[] indexes) {
        return createResult(mode, allFaces, indexes, null);
    }

    public FortuneResult createResult(FortuneMode mode, List<FortuneFace> allFaces, int[] indexes, boolean[] reversed) {
        List<FortuneFace> selectedFaces = new ArrayList<>();
        for (int index : indexes) {
            selectedFaces.add(allFaces.get(index));
        }

        FortuneStrategy strategy = strategies.get(mode);
        if (strategy != null) {
            return strategy.buildResult(selectedFaces, reversed);
        }
        return buildDefaultFortune(selectedFaces);
    }

    private FortuneResult buildDefaultFortune(List<FortuneFace> faces) {
        StringBuilder builder = new StringBuilder();
        builder.append("抽取结果：\n");
        for (FortuneFace face : faces) {
            builder.append(face.getName()).append(" - ").append(face.getKeyword()).append("\n");
            builder.append(face.getMessage()).append("\n");
        }
        return new FortuneResult(faces, null, builder.toString(), "");
    }

    public boolean[] randomReversedFlags(int count) {
        return randomReversedFlags(count, random);
    }

    public boolean[] randomReversedFlags(int count, Random source) {
        boolean[] reversed = new boolean[count];
        for (int i = 0; i < reversed.length; i++) {
            reversed[i] = source.nextBoolean();
        }
        return reversed;
    }
}