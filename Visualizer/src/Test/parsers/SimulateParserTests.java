package parsers;

import Model.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

/**
 * Created by lajtman on 07-02-2017.
 */
public class SimulateParserTests {

    @Test
    public void parseSimpleVariable() {
        String variableDef = "P.s1:";
        String variableName = RegexHelper.getFirstMatchedValueFromRegex(SimulateParser.variableRegex, variableDef);
        assertEquals("P.s1", variableName);
    }


    @Test
    public void parseAdvancedVariable() {
        String variableDef = "Paber.data_is_scheduled[30][2]:";
        String variableName = RegexHelper.getFirstMatchedValueFromRegex(SimulateParser.variableRegex, variableDef);
        assertEquals("Paber.data_is_scheduled[30][2]", variableName);
    }

    @Test
    public void parseSimulateResult() {
        String sampleOutput =
                "Options for the verification:\n" +
                "  Generating no trace\n" +
                "  Search order is breadth first\n" +
                "  Using conservative space optimisation\n" +
                "  Seed is 1486472788\n" +
                "  State space representation uses minimal constraint systems\n" +
                "[2K\n" +
                "Verifying formula 1 at line 1\n" +
                "[2K -- Formula is satisfied.\n" +
                "P.s1:\n" +
                "[0]: (0,1) (2,1) (2,0) (6,0) (6,1) (8,1) (8,0) (12,0) (12,1) (14,1) (14,0) (18,0) (18,1) (20,1) " +
                        "(20,0) (24,0) (24,1) (26,1) (26,0) (30,0) (30,1) (32,1) (32,0) (36,0) (36,1) (38,1) (38,0) (42,0)\n" +
                "P.s2:\n" +
                "[0]: (0,0) (2,0) (2,1) (4,1) (4,0) (8,0) (8,1) (10,1) (10,0) (14,0) (14,1) (16,1) (16,0) " +
                        "(20,0) (20,1) (22,1) (22,0) (26,0) (26,1) (28,1) (28,0) (32,0) (32,1) (34,1) (34,0) (38,0) " +
                        "(38,1) (40,1) (40,0) (42,0)\n";

        SimulateOutput output = SimulateParser.parse(Arrays.asList(sampleOutput.split("\n")), 1);

        assertEquals(2, output.getNumVariables());
        assertEquals(14, output.getSimulationForVariable("P.s1", 0).size());
        containsData("P.s1", 12, 1.0, 0, output);
        containsData("P.s1", 36, 1.0, 0, output);

        containsData("P.s2", 32, 1.0, 0, output);
        containsData("P.s2", 40, 0.0, 1, output);
    }

    @Test
    public void removeUnnecessaryDataPoints() {
        String sampleOutput =
                        "[2K -- Formula is satisfied.\n" +
                        "P.s1:\n" +
                        "[0]: (0,0) (1,0) (1,1) (2,1) (2,0) (3,0)";

        SimulateOutput output = SimulateParser.parse(Arrays.asList(sampleOutput.split("\n")), 1);

        assertEquals(2, output.getSimulationForVariable("P.s1", 0). size());
        containsData("P.s1", 1, 1.0, 0, output);
        containsData("P.s1", 2, 0.0, 1, output);
    }

    @Test
    public void parseSimulateErrorOutput() {
        String sampleErrorOutput =
                "Options for the verification:\n" +
                    "Generating no trace\n" +
                    "Search order is breadth first\n" +
                    "Using conservative space optimisation\n" +
                    "Seed is 1486472853\n" +
                    "State space representation uses minimal constraint systems\n" +
                "uppaalquery.q:1: [error] Unknown identifier: as.\n" +
                "uppaalquery.q:1: [error] syntax error: unexpected end, expecting ',' or '}'. ";

        String expectedOutput = "Unknown identifier: as.\n" +
                "syntax error: unexpected end, expecting ',' or '}'. ";

        SimulateOutput output = SimulateParser.parse(Arrays.asList(sampleErrorOutput.split("\n")), 1);
        assertTrue(output.errorState());
        assertEquals(expectedOutput, output.getErrorDescription());
    }

    private void containsData(String name, double time, double value, double previousValue, SimulateOutput simulateOutput) {
        DataPoint d = new DataPoint(time, value, previousValue);
        assertTrue("name: " + name + ", time: " + time + " value: " + value,
                simulateOutput.getSimulationForVariable(name, 0).contains(d));
    }

    @Test
    public void zipOutput(){
        SimulateOutput simOut = new SimulateOutput(1);
        String from1to2 = "data[1][2]";
        String from2to1 = "data[2][1]";
        ArrayList<DataPoint> datas = new ArrayList<>();
        datas.add(new DataPoint(1,5));
        datas.add(new DataPoint(2,4));
        datas.add(new DataPoint(3,3));
        datas.add(new DataPoint(4,2));
        datas.add(new DataPoint(5,1));

        simOut.addDatapoint(from1to2, 0, datas.get(0));
        simOut.addDatapoint(from1to2, 0, datas.get(3));
        simOut.addDatapoint(from1to2, 0, datas.get(4));
        simOut.addDatapoint(from2to1, 0, datas.get(1));
        simOut.addDatapoint(from2to1, 0, datas.get(2));

        ArrayList<SimulationEdgePoint> expected = new ArrayList<>();
        expected.add(new SimulationEdgePoint(1,"1","2",5));
        expected.add(new SimulationEdgePoint(2,"2","1",4));
        expected.add(new SimulationEdgePoint(3,"2","1",3));
        expected.add(new SimulationEdgePoint(4,"1","2",2));
        expected.add(new SimulationEdgePoint(5,"1","2",1));
        ArrayList<SimulationEdgePoint> actual = simOut.getZippedEdgePoints(0);

        AssertArrayList(expected, actual);
    }

    @Test
    public void zipOutputFromNonArrayVariable(){
        String uppaalOut1 = "data[1][2]";
        String uppaalOut2 = "data[2][1]";

        ArrayList<OutputVariable> outputVars = new ArrayList<>();
        OutputVariable out1 = new OutputVariable("data");
        out1.setEdgeData(true); //true because it is 2d array
        outputVars.add(out1);
        OutputVariable out2 = new OutputVariable("test");
        outputVars.add(out2);

        ArrayList<SimulationPoint> expected = new ArrayList<>();
        expected.add(new SimulationPoint(out2.getName(), 0, 2));
        expected.add(new SimulationEdgePoint(1,"1","2",5));
        expected.add(new SimulationEdgePoint(2,"2","1",4));
        expected.add(new SimulationEdgePoint(3,"2","1",3));
        expected.add(new SimulationEdgePoint(4,"1","2",2));
        expected.add(new SimulationEdgePoint(5,"1","2",1));
        expected.add(new SimulationPoint(out2.getName(), 6, 20));
        expected.add(new SimulationPoint(out2.getName(), 100, 15));

        SimulateOutput simOut = new SimulateOutput(1);
        simOut.addDatapoint(uppaalOut1, 0, new DataPoint(1,5));
        simOut.addDatapoint(uppaalOut1, 0, new DataPoint(4,2));
        simOut.addDatapoint(uppaalOut1, 0, new DataPoint(5,1));
        simOut.addDatapoint(uppaalOut2, 0, new DataPoint(2,4));
        simOut.addDatapoint(uppaalOut2, 0, new DataPoint(3,3));
        simOut.addDatapoint(out2.getName(), 0, new DataPoint(6, 20));
        simOut.addDatapoint(out2.getName(), 0, new DataPoint(100, 15));
        simOut.addDatapoint(out2.getName(), 0, new DataPoint(0, 2));

        List<SimulationPoint> actual = simOut.zipAsList(outputVars, 0);

        AssertArrayList(expected, actual);
    }

    @Test
    public void zipOutputFromLocalArrayVariable(){
        String uppaalOut1 = "dymo[1].OUTPUT_queues[2]";
        String uppaalOut2 = "dymo[2].OUTPUT_queues[1]";

        ArrayList<OutputVariable> outputVars = new ArrayList<>();
        OutputVariable out1 = new OutputVariable("OUTPUT_queues");
        out1.setEdgeData(true); //true because it is 2d array
        outputVars.add(out1);

        ArrayList<SimulationPoint> expected = new ArrayList<>();
        expected.add(new SimulationEdgePoint(1,"1","2",5));
        expected.add(new SimulationEdgePoint(2,"2","1",4));
        expected.add(new SimulationEdgePoint(3,"2","1",3));
        expected.add(new SimulationEdgePoint(4,"1","2",2));
        expected.add(new SimulationEdgePoint(5,"1","2",1));

        SimulateOutput simOut = new SimulateOutput(1);
        simOut.addDatapoint(uppaalOut1, 0, new DataPoint(1,5));
        simOut.addDatapoint(uppaalOut1, 0, new DataPoint(4,2));
        simOut.addDatapoint(uppaalOut1, 0, new DataPoint(5,1));
        simOut.addDatapoint(uppaalOut2, 0, new DataPoint(2,4));
        simOut.addDatapoint(uppaalOut2, 0, new DataPoint(3,3));

        List<SimulationPoint> actual = simOut.zipAsList(outputVars, 0);

        AssertArrayList(expected, actual);
    }

    @Test
    public void zipOutputMultipleSims(){
        SimulateOutput simOut = new SimulateOutput(2);
        String from1to2 = "data[1][2]";
        String from2to1 = "data[2][1]";
        ArrayList<DataPoint> datas = new ArrayList<>();
        datas.add(new DataPoint(1,10));
        datas.add(new DataPoint(2,9));
        datas.add(new DataPoint(3,8));
        datas.add(new DataPoint(4,7));
        datas.add(new DataPoint(5,6));
        datas.add(new DataPoint(6,5));
        datas.add(new DataPoint(7,4));
        datas.add(new DataPoint(8,3));
        datas.add(new DataPoint(9,2));
        datas.add(new DataPoint(10,1));

        simOut.addDatapoint(from1to2, 0, datas.get(5));
        simOut.addDatapoint(from1to2, 0, datas.get(6));
        simOut.addDatapoint(from1to2, 0, datas.get(7));
        simOut.addDatapoint(from2to1, 0, datas.get(8));
        simOut.addDatapoint(from2to1, 0, datas.get(9));
        simOut.addDatapoint(from1to2, 1, datas.get(0));
        simOut.addDatapoint(from1to2, 1, datas.get(3));
        simOut.addDatapoint(from1to2, 1, datas.get(4));
        simOut.addDatapoint(from2to1, 1, datas.get(1));
        simOut.addDatapoint(from2to1, 1, datas.get(2));

        ArrayList<SimulationEdgePoint> expected = new ArrayList<>();
        expected.add(new SimulationEdgePoint(1,"1","2",10));
        expected.add(new SimulationEdgePoint(2,"2","1",9));
        expected.add(new SimulationEdgePoint(3,"2","1",8));
        expected.add(new SimulationEdgePoint(4,"1","2",7));
        expected.add(new SimulationEdgePoint(5,"1","2",6));


        ArrayList<SimulationEdgePoint> actual = simOut.getZippedEdgePoints(1);

        AssertArrayList(expected, actual);
    }

    private void AssertArrayList(List<? extends SimulationPoint> expected, List<? extends SimulationPoint> actual){
        assertEquals(expected.size(), actual.size());
        for (int i = 0;i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }
    @Test
    public void testRunMayNeverHaveMultipleValuesForSameClock() {
        String sampleOutput =
                "[2K -- Formula is satisfied.\n" +
                        "P.s1:\n" +
                        "[0]: (0,0) (0,1) (0,0) (0,1)";

        SimulateOutput output = SimulateParser.parse(Arrays.asList(sampleOutput.split("\n")), 1);

        assertEquals(1, output.getSimulationForVariable("P.s1", 0).size());
        containsData("P.s1", 0, 1.0, 0, output);
    }

    @Test
    public void testNeverTwoFollowingDataPointsWithSameValue(){
        String sampleOutput =
                "[2K -- Formula is satisfied.\n" +
                        "P.s1:\n" +
                        "[0]: (0,0) (6150.92,0) (6150.92,0) (6150.92,1) (6150.92,1) (16951.9,1) (16951.9,1) (16952.9,0) (16952.9,1) (20071.9,1)";

        SimulateOutput output = SimulateParser.parse(Arrays.asList(sampleOutput.split("\n")),1);

        assertEquals(1,output.getSimulationForVariable("P.s1", 0).size());
        containsData("P.s1", 6150.92, 1.0, 0, output);
    }

    @Test
    public void testLMACRemoveCorrectData() {
        String uppaalOutput = "Options for the verification:\n" +
                "  Generating no trace\n" +
                "  Search order is breadth first\n" +
                "  Using conservative space optimisation\n" +
                "  Seed is 1492685741\n" +
                "  State space representation uses minimal constraint systems\n" +
                "\u001B[2K\n" +
                "Verifying formula 1 at line 1\n" +
                "\u001B[2K -- Formula is satisfied.\n" +
                "OUTPUT_slot_no[0]:\n" +
                "[0]: (0,0) (0,0) (12,0)\n" +
                "[1]: (0,0) (0,0) (12,0)\n" +
                "[2]: (0,0) (0,0) (12,0)\n" +
                "OUTPUT_slot_no[1]:\n" +
                "[0]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "[1]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "[2]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "OUTPUT_slot_no[2]:\n" +
                "[0]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "[1]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "[2]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "OUTPUT_slot_no[3]:\n" +
                "[0]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "[1]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "[2]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "OUTPUT_slot_no[4]:\n" +
                "[0]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "[1]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "[2]: (0,0) (0,0) (0,-1) (12,-1)\n" +
                "node[1].OUTPUT_collision:\n" +
                "[0]: (0,-1) (0,-1) (12,-1)\n" +
                "[1]: (0,-1) (0,-1) (12,-1)\n" +
                "[2]: (0,-1) (0,-1) (12,-1)\n" +
                "node[2].OUTPUT_collision:\n" +
                "[0]: (0,-1) (0,-1) (12,-1)\n" +
                "[1]: (0,-1) (0,-1) (12,-1)\n" +
                "[2]: (0,-1) (0,-1) (12,-1)\n" +
                "node[3].OUTPUT_collision:\n" +
                "[0]: (0,-1) (0,-1) (12,-1)\n" +
                "[1]: (0,-1) (0,-1) (12,-1)\n" +
                "[2]: (0,-1) (0,-1) (12,-1)\n" +
                "node[4].OUTPUT_collision:\n" +
                "[0]: (0,-1) (0,-1) (12,-1)\n" +
                "[1]: (0,-1) (0,-1) (12,-1)\n" +
                "[2]: (0,-1) (0,-1) (12,-1)\n" +
                "node[1].OUTPUT_mode:\n" +
                "[0]: (0,0) (0,0) (2,0) (2,0) (2,2) (2,2) (12,2)\n" +
                "[1]: (0,0) (0,0) (2,0) (2,0) (2,2) (12,2)\n" +
                "[2]: (0,0) (0,0) (2,0) (2,0) (2,2) (2,2) (12,2)\n" +
                "node[2].OUTPUT_mode:\n" +
                "[0]: (0,0) (0,0) (12,0)\n" +
                "[1]: (0,0) (0,0) (12,0)\n" +
                "[2]: (0,0) (0,0) (12,0)\n" +
                "node[3].OUTPUT_mode:\n" +
                "[0]: (0,0) (0,0) (12,0)\n" +
                "[1]: (0,0) (0,0) (12,0)\n" +
                "[2]: (0,0) (0,0) (12,0)\n" +
                "node[4].OUTPUT_mode:\n" +
                "[0]: (0,0) (0,0) (12,0)\n" +
                "[1]: (0,0) (0,0) (12,0)\n" +
                "[2]: (0,0) (0,0) (12,0)\n" +
                "gateway[0].OUTPUT_collision:\n" +
                "[0]: (0,-1) (0,-1) (12,-1)\n" +
                "[1]: (0,-1) (0,-1) (12,-1)\n" +
                "[2]: (0,-1) (0,-1) (12,-1)\n" +
                "gateway[0].OUTPUT_mode:\n" +
                "[0]: (0,3) (0,3) (12,3)\n" +
                "[1]: (0,3) (0,3) (12,3)\n" +
                "[2]: (0,3) (0,3) (12,3)";


        SimulateOutput out = SimulateParser.parse(Arrays.asList(uppaalOutput.split("\\n")), 3);
        assertEquals(out.getNumVariables(), 15);
    }
}
