package dev.nyxane.mods.scalmyth.client;

import net.minecraft.core.Position;
import oshi.util.tuples.Pair;

import java.util.List;

public class VisualDebug {
    static public List<Pair<Position, Position>> lines = List.of();
    public static void drawLine(Position start, Position end) {
        lines.add(new Pair<>(start,end));
    }
}
