package com.example.fortune.strategy;

import com.example.fortune.model.FortuneFace;
import com.example.fortune.model.FortuneResult;

import java.util.List;

public interface FortuneStrategy {
    FortuneResult buildResult(List<FortuneFace> faces, boolean[] reversed);
}