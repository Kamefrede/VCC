import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import me.gammadelta.Utils;
import me.gammadelta.common.program.CPURepr;
import me.gammadelta.common.program.MemoryType;
import me.gammadelta.common.program.MotherboardRepr;
import me.gammadelta.common.program.RegisterRepr;
import me.gammadelta.common.program.compilation.ASMCompiler;
import me.gammadelta.common.program.compilation.BytecodeWriter;
import me.gammadelta.common.program.compilation.Instruction;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ExecutionTest {
    @Test
    public void testDisan() throws Exception {
        // === READING AND ASSEMBLING ===

        URL path = ClassLoader.getSystemResource("DisanCount.vcc");
        String program = String.join("\n", Files.readAllLines(Paths.get(path.toURI())));

        List<Instruction> instructions = ASMCompiler.lexAndParse(program);
        System.out.println(ASMCompiler.prettyPrintInstructions(instructions));

        ByteList bytecode = new BytecodeWriter(instructions).writeProgramToBytecode();
        System.out.println(Utils.hexdump(bytecode));

        // === COMPUTER ===

        Random rand = new Random();

        EnumMap<MemoryType, ArrayList<IntList>> memLocations = new EnumMap<>(MemoryType.class);
        // xrams: [0]
        memLocations.put(MemoryType.XRAM, new ArrayList<>(Collections.singleton(IntLists.singleton(0))));
        // fill in everything else to avoid npes
        memLocations.put(MemoryType.EXRAM, new ArrayList<>());
        memLocations.put(MemoryType.ROM, new ArrayList<>());
        memLocations.put(MemoryType.RAM, new ArrayList<>());

        // Registers: [0, 1, 2]
        ArrayList<IntList> registers = new ArrayList<>(Arrays.asList(
                IntLists.singleton(0),
                IntLists.singleton(1),
                IntLists.singleton(2)
        ));

        // this cpurepr is strapped together with duct tape and hope...
        CPURepr cpu = new CPURepr(
                registers,
                memLocations
        );

        EnumMap<MemoryType, Integer> memoryCounts = new EnumMap<>(MemoryType.class);
        // we DO have to put everything in here
        memoryCounts.put(MemoryType.XRAM, 1);
        memoryCounts.put(MemoryType.EXRAM, 0);
        memoryCounts.put(MemoryType.ROM, 0);
        memoryCounts.put(MemoryType.RAM, 0);
        MotherboardRepr motherboard = new MotherboardRepr(memoryCounts, new ArrayList<>(
                Collections.singletonList(new ArrayList<>(Collections.singletonList(cpu)))
        ), new ArrayList<>(Arrays.asList(
                new RegisterRepr(new BlockPos[]{BlockPos.ZERO, BlockPos.ZERO}),
                new RegisterRepr(new BlockPos[]{BlockPos.ZERO, BlockPos.ZERO}),
                new RegisterRepr(new BlockPos[]{BlockPos.ZERO, BlockPos.ZERO})
        )), new ArrayList<>(), rand);

        // Write the program to XRAM
        for (int i = 0; i < bytecode.size(); i++) {
            byte code = bytecode.getByte(i);
            motherboard.memory[i] = code;
        }

        // and execute!
        for (int i = 0; i < 512; i++) {
            motherboard.executeStep(rand);
        }
    }

    @Test
    public void testIToA() throws Exception {
        // === READING AND ASSEMBLING ===

        URL path = ClassLoader.getSystemResource("IToA.vcc");
        String program = String.join("\n", Files.readAllLines(Paths.get(path.toURI())));

        List<Instruction> instructions = ASMCompiler.lexAndParse(program);
        System.out.println(ASMCompiler.prettyPrintInstructions(instructions));

        ByteList bytecode = new BytecodeWriter(instructions).writeProgramToBytecode();
        System.out.println(Utils.hexdump(bytecode));

        // === COMPUTER ===

        Random rand = new Random();

        EnumMap<MemoryType, ArrayList<IntList>> memLocations = new EnumMap<>(MemoryType.class);
        // xrams: [0]
        memLocations.put(MemoryType.XRAM, new ArrayList<>(Collections.singleton(IntLists.singleton(0))));
        // rams: [0]
        memLocations.put(MemoryType.RAM, new ArrayList<>(Collections.singleton(IntLists.singleton(0))));
        // fill in everything else to avoid npes
        memLocations.put(MemoryType.EXRAM, new ArrayList<>());
        memLocations.put(MemoryType.ROM, new ArrayList<>());


        // Registers: [0, 1, 2]
        ArrayList<IntList> registers = new ArrayList<>(Arrays.asList(
                IntLists.singleton(0),
                IntLists.singleton(1),
                IntLists.singleton(2)
        ));

        // this cpurepr is strapped together with duct tape and hope...
        CPURepr cpu = new CPURepr(
                null,
                new RegisterRepr(new BlockPos[]{BlockPos.ZERO}),
                BlockPos.ZERO,
                registers,
                new ArrayList<>(),
                memLocations
        );

        EnumMap<MemoryType, Integer> memoryCounts = new EnumMap<>(MemoryType.class);
        // we DO have to put everything in here
        memoryCounts.put(MemoryType.XRAM, 1);
        memoryCounts.put(MemoryType.EXRAM, 0);
        memoryCounts.put(MemoryType.ROM, 0);
        memoryCounts.put(MemoryType.RAM, 1);
        BlockPos[] eightBytes = new BlockPos[]{BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO, BlockPos.ZERO};
        MotherboardRepr motherboard = new MotherboardRepr(memoryCounts, new ArrayList<>(
                Collections.singletonList(new ArrayList<>(Collections.singletonList(cpu)))
        ), new ArrayList<>(Arrays.asList(
                new RegisterRepr(eightBytes),
                new RegisterRepr(eightBytes),
                new RegisterRepr(eightBytes)
        )), new ArrayList<>(), rand);

        // Write the program to XRAM
        for (int i = 0; i < bytecode.size(); i++) {
            byte code = bytecode.getByte(i);
            motherboard.memory[i] = code;
        }

        // and execute!
        for (int i = 0; i < 1024; i++) {
            motherboard.executeStep(rand);
        }
    }
}
