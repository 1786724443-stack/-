package test;

import java.util.List;

public interface FortuneStrategy {
    FortuneResult buildResult(List<FortuneFace> faces, boolean[] reversed);
}