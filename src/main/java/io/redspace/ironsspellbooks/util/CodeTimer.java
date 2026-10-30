package io.redspace.ironsspellbooks.util;

import java.util.ArrayList;
import java.util.List;

public class CodeTimer {
    private record Entry(String name, long nanos) {
    }

    private final List<Entry> timing = new ArrayList<>();

    public CodeTimer() {
        add("START");
    }

    public void add(String name) {
        timing.add(new Entry(name, System.nanoTime()));
    }

    public String getOutput(String delimiter) {
        StringBuilder sb = new StringBuilder();

        long itemDelta = 0;
        long totalDelta = 0;

        for (int i = 0; i < timing.size(); i++) {
            var item = timing.get(i);

            if (i > 0) {
                var lastItem = timing.get(i - 1);
                itemDelta = item.nanos() - lastItem.nanos();
                totalDelta += itemDelta;
                sb.append(String.format("%s%s%s%s%f%s%f\n", lastItem.name(), delimiter, item.name(), delimiter, (itemDelta / 1000000d), delimiter, totalDelta / 1000000d));
            }
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return getOutput("\t");
    }
}
