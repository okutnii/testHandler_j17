package blockchain;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class MinerThreadTest {
    @Test
    public void testGenerateMagicHashWithSingleZero() {
        MinerThread minerThread = new MinerThread(new Miner(), new Blockchain(), new ArrayList<>());

        String hash = minerThread.generateMagicHash("text", 1);

        assertTrue(hash.startsWith("0"));
    }

    @Test
    public void testGenerateMagicHashWithMultipleZeros() {
        MinerThread minerThread = new MinerThread(new Miner(), new Blockchain(), new ArrayList<>());

        String hash = minerThread.generateMagicHash("text", 3);

        assertTrue(hash.startsWith("000"));
    }
}
