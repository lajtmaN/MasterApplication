package parsers;

import Model.CVar;
import Model.UPPAALEdge;
import Model.UPPAALTopology;
import parsers.Declaration.VariableParser;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CHandler {
    private static final String ConfigVariablePrefix = "CONFIG_";
    private static final String TopologyRegex = "CONFIG_connected\\[.+\\n((?:[^;])+)\\};";
    private static final String TopologyFormRegex = "((?:\\{(?:\\d,)*\\d\\},)*(?:\\{(?:\\d,)*\\d\\})+)";
    private static final String OutputVarsRegex = "int\\s+(OUTPUT_(?:\\w)+(?:\\s*\\[\\w+\\])*)";
    private static final String ConfigDeclarationRegex(String type) {
        return "(const\\s)?" + type + "(\\s)*((\\w)*(\\s)*=[\\d\\w*+\\-/%\\s,]*)+;"; }
    private static final String TypedefRegex(String typedefName) {
        return "typedef\\s+int\\s+\\[\\s*0,\\s*(CONFIG_\\w+)([*+-]\\d+)?\\]\\s+" + typedefName;
    }

    public static ArrayList<CVar> getConfigVariables(String decls, String scope) {
        ArrayList<CVar> returnvars = VariableParser.getInstantiations(decls);
        returnvars.removeIf(var -> !var.getName().startsWith(ConfigVariablePrefix) || var.isArrayType());
        returnvars.forEach(var -> var.setScope(scope));
        return returnvars;
    }

    public static ArrayList<CVar> getConfigVariables(HashMap<String, String> allDecls) {
        ArrayList<CVar> constantNames = new ArrayList<>();
        for (String scopeKey : allDecls.keySet()) {
            constantNames.addAll(getConfigVariables(allDecls.get(scopeKey), scopeKey));
        }
        return constantNames;
    }

    public static UPPAALTopology getTopology(String decls){
        UPPAALTopology result = new UPPAALTopology();
        String definitionString = RegexHelper.getFirstMatchedValueFromRegex(TopologyRegex, decls);
        if(definitionString != null) {
            definitionString = definitionString.replace(" ", "").replace("\n", "").replace("\t", "");
            if (RegexHelper.getFirstMatchedValueFromRegex(TopologyFormRegex, definitionString) != null) {
                int source_index = 0;
                for (String s : definitionString.split("}")) {
                    String temp = s.replace(",{", "").replace("}", "").replace("{", "");
                    int destination_index = 0;
                    for (String element : temp.split(",")) {
                        if (element.equals("1")) { // TODO: Only binary relations can be defined
                            result.add(new UPPAALEdge(source_index, destination_index));
                        }
                        destination_index++;
                    }
                    if (result.getNumberOfNodes() == 0) {
                        result.setNumberOfNodes(destination_index);
                    }
                    source_index++;
                }
            }
        }
        return result;
    }

    public static ArrayList<String> getOutputVars(String declarations) {
        Pattern pVar = Pattern.compile(OutputVarsRegex);
        ArrayList<String> vars = new ArrayList<>();

        Matcher mName = pVar.matcher(declarations);

        while(mName.find()) {
            vars.add(mName.group(1));
        }

        return vars;
    }


    public static int getSizeOfParam(String paramForProc, String globalDecls, List<CVar> constants) {
        String regex = TypedefRegex(paramForProc);
        String constantName = RegexHelper.getNthMatchedValueFromRegex(regex, globalDecls, 1);
        String optionalExpression = RegexHelper.getNthMatchedValueFromRegex(regex, globalDecls, 2);

        Optional<CVar> matchedConstant = constants.stream().filter(p -> p.getName().equals(constantName)).findFirst();
        if (matchedConstant.isPresent()) {
            int constValue = Integer.parseInt(matchedConstant.get().getValue());

            if (optionalExpression != null) {
                String operator = RegexHelper.getFirstMatchedValueFromRegex("([*+-])", optionalExpression);
                int num = Integer.parseInt(RegexHelper.getFirstMatchedValueFromRegex("(\\d+)", optionalExpression));
                switch (operator) {
                    case "*":
                        return constValue * num;
                    case "+":
                        return constValue + num;
                    case "-":
                        return constValue - num;
                }
            }
            return constValue;
        }
        return 0;
    }
}