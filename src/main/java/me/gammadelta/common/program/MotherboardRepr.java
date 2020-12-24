package me.gammadelta.common.program;

import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import me.gammadelta.Utils;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

public class MotherboardRepr {

    public static int XRAM_SIZE = 256;
    public static int ROM_SIZE = 1_048_576;
    public static int RAM_SIZE = 65_536;
    public static Permissions XRAM_PERMS = Permissions.RWX;
    public static Permissions ROM_PERMS = Permissions.R;
    public static Permissions RAM_PERMS = Permissions.RW;

    // region These values are serialized back and forth with NBT.

    /**
     * The contents of all memory blocks.
     * See Memory Layout on the wiki.
     */
    public byte[] memory;
    private static final String MEMORY_KEY = "memory";


    private EnumMap<MemoryType, Integer> memoryCounts;
    private static final String MEMORY_COUNTS_KEY = "memory_counts";

    /**
     * CPUs, sorted by distance to the motherboard. Ties are in their own sub-arrays.
     *
     * `[ [0], [1, 1, 1], [2, 2], [3], [4] ]`
     */
    private ArrayList<ArrayList<CPURepr>> cpus;
    private static final String CPUS_KEY = "cpus";

    /**
     * Registers, in no particular order.
     * This does not include IP/SP extenders; their reprs are stored in the CPU.
     */
    private ArrayList<RegisterRepr> registers;
    private static final String REGISTERS_KEY = "registers";

    // endregion End serialized values.


    public MotherboardRepr(EnumMap<MemoryType, Integer> memoryCounts, ArrayList<ArrayList<CPURepr>> cpus, ArrayList<RegisterRepr> registers, Random rand) {
        this.memoryCounts = memoryCounts;
        this.cpus = cpus;
        this.registers = registers;

        int memorySize = 0;
        for (Map.Entry<MemoryType, Integer> entry : this.memoryCounts.entrySet()) {
            MemoryType mtype = entry.getKey();
            Integer count = entry.getValue();
            memorySize += mtype.storageAmount * count;
        }
        this.memory = new byte[memorySize];
        rand.nextBytes(this.memory);
    }

    /**
     * Execute one step of the computer!
     */
    private void executeStep(World world) {
        for (ArrayList<CPURepr> cpuGroup : this.cpus) {
            int[] indices = Utils.randomIndices(cpuGroup.size(), world.getRandom());
            for (int cpuIdx : indices) {
                cpuGroup.get(cpuIdx).executeStep(this, world);
            }
        }
    }

    // region Interacting with memory

    // java bad
    private static byte[] NEW_BYTE_ARRAY = new byte[0];

    /**
     * Access the region of memory associated with the type and that block index, and return it.
     * Effectively, this puts back the abstraction of bytes being stored across many blocks.
     */
    public ByteArrayList readRegion(MemoryType memType, int blockIdx) throws Emergency {
        int totalBlockCount = this.memoryCounts.get(memType);
        if (blockIdx >= totalBlockCount) {
            // uh-oh
            throw new Emergency();
        }
        // Calculate the byte index to start at
        int byteIdx = 0;
        for (MemoryType checkThis : MemoryType.values()) {
            if (checkThis == memType) {
                byteIdx += memType.storageAmount * blockIdx;
                break;
            }
            byteIdx += checkThis.storageAmount * totalBlockCount;
        }
        // and return
        ByteArrayList bal = new ByteArrayList(memType.storageAmount);
        bal.addElements(0, this.memory, byteIdx, memType.storageAmount);
        return bal;
    }

    // endregion
}
