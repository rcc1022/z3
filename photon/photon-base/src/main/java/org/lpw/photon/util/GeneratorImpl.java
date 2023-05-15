package org.lpw.photon.util;

import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.UUID;

@Component("photon.util.generator")
public class GeneratorImpl implements Generator {
    private final Random random = new Random();

    @Override
    public String random(int length) {
        StringBuilder sb = new StringBuilder();
        char a = 'a' - 10;
        while (sb.length() < length) {
            int n = Math.abs(random.nextInt()) % 36;
            if (n < 10)
                n += '0';
            else
                n += a;
            sb.append((char) n);
        }

        return sb.toString();
    }

    @Override
    public String number(int length) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length)
            sb.append(Math.abs(random.nextInt()) % 10);

        return sb.toString();
    }

    @Override
    public String chars(int length) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            int n = Math.abs(random.nextInt()) % 26 + 'a';
            sb.append((char) n);
        }

        return sb.toString();
    }

    @Override
    public int random(int min, int max) {
        if (min == max)
            return min;

        if (min > max)
            return Math.abs(random.nextInt()) % (min - max + 1) + max;

        return Math.abs(random.nextInt()) % (max - min + 1) + min;
    }

    @Override
    public long random(long min, long max) {
        if (min == max)
            return min;

        if (min > max)
            return Math.abs(random.nextLong()) % (min - max + 1) + max;

        return Math.abs(random.nextLong()) % (max - min + 1) + min;
    }

    @Override
    public double random(double min, double max) {
        if (min == max)
            return min;

        if (min > max)
            return random.nextDouble() * (min - max) + max;

        return random.nextDouble() * (max - min) + min;
    }

    @Override
    public String uuid() {
        return UUID.randomUUID().toString();
    }
}
