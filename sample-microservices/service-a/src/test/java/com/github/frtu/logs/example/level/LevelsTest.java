package com.github.frtu.logs.example.level;

import com.github.frtu.logs.example.level.sub1.Sub1;
import com.github.frtu.logs.example.level.sub1.sub2.Sub2;
import com.github.frtu.logs.example.level.sub1.sub2.sub3.Sub3;
import org.junit.jupiter.api.Test;

class LevelsTest {
    @Test
    void levels() {
        new Sub1().levels();
        new Sub2().levels();
        new Sub3().levels();
    }
}