package gals;

import compiladoresM2.Assembly;
import compiladoresM2.Main;
import compiladoresM2.SemanticTable;
import compiladoresM2.SemanticTableHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Semantico implements Constants
{

    String type;
    String warningOutput = "Compilado com Warning\n";


    public class Simbolo{
        public String name;
        public String type;
        public boolean init = false;
        public boolean used = false;
        public int scope = 0;
        public boolean param = false;
        public int pos = 0;
        public boolean vect=false;
        public boolean matrix = false;
        public boolean ref=  false;
        public boolean func;
        public int size =1;
    }
    private boolean vecExp = false;
    public List<Simbolo> symbolTable = new ArrayList<Simbolo>();
    public List<Integer> pileOfScopes = new ArrayList<Integer>();
    private int scopeCounter=0;
    Simbolo lastSimbol = new Simbolo();
    private boolean isAttr = false;
    private boolean isCin = false;
    private boolean isCout = false;
    private boolean isVect = false;
    private String indexOfAttr;
    private List<String> attrPile = new ArrayList<>();
    private List<String> attrPileOfLex = new ArrayList<>();
    private List<String> indexExp = new ArrayList<>();

    SemanticTableHelper helper = new SemanticTableHelper();
    SemanticTable semanticTable = new SemanticTable();
    Simbolo lastAddSymbol;
    public  Assembly assembly = new Assembly();
    String elementOnTheLeftSideOfAttr;
    boolean leftElementIsVect = false;
    String listOfOps[] = {"+","-","<<",">>","|","&"};
    public void printTable(){
        System.out.println("Nome|Tipo|Func|Vet|Inic|Param|Pos|Ref|Escopo|Usado");
        for(Simbolo sim : this.symbolTable){
            System.out.println(sim.name +" " + sim.type + " " + sim.func + " " + sim.vect + " " + sim.init + " " + sim.param +" " + sim.pos + " " + sim.ref + " " + sim.scope + " " + sim.used );
        }
    }

    public Object[][] fillTable(){

        Object[][] data = new Object[this.symbolTable.size()][10];
        int i = 0;
        for(Simbolo sim : this.symbolTable){
            data[i][0] = sim.name;
            data[i][1] = sim.type;
            data[i][2] = sim.func;
            data[i][3] = sim.vect;
            data[i][4] = sim.init;
            data[i][5] = sim.param;
            data[i][6] = sim.pos;
            data[i][7] = sim.ref;
            data[i][8] = sim.scope;
            data[i][9] = sim.used;
            i++;
        }
        return data;
    }
    public String getWarningOutput(){
        return warningOutput;
    }

    private Simbolo findSymbolSameNameAndScope(Simbolo _lastSimbol){
        for(Simbolo sim : symbolTable){
            if(_lastSimbol.name.equals(sim.name) && _lastSimbol.scope == sim.scope){
                _lastSimbol.init = true;
                return sim;
            }
        }
        return null;
    }

    private boolean checkIfVarExistsCurrentContext(Simbolo sim){
        for (Simbolo simbols : symbolTable){
            if(sim.scope == simbols.scope && sim.name.equals(simbols.name)){
                return true;
            }
        }
        return false;
    }
    private boolean checkIfIsInit(Simbolo sim){
        for (Simbolo simbols : symbolTable){
            if(sim.scope == simbols.scope && sim.name.equals(simbols.name)){
                if(sim.init==false){
                    return false;
                }
                else{
                    return true;
                }
            }
        }
        return false;
    }
    private String returnObjType(String name){
        for(Simbolo x : symbolTable){
            if(x.name.equals(name)){
                return x.type;
            }
        }
        return null;
    }

    public void clearTable(){

        this.symbolTable.clear();
        attrPile.clear();
        attrPileOfLex.clear();
        warningOutput = "";
    }
    public void changedToUsed(Simbolo sim){
        for(Simbolo simbolo: symbolTable){
            if(sim.scope==simbolo.scope && sim.name.equals(simbolo.name)){
                simbolo.used = true;
            }
        }
    }
    public void changedToInit(String name ){
        for(int i=symbolTable.size()-1;i>=0;i--){
            if(name.equals(symbolTable.get(i).name)){
                symbolTable.get(i).init = true;

            }
        }
    }
    private void changeSize(int size, int scope,String  name){
        for(int i=symbolTable.size()-1;i>=0;i--){
            if(name.equals(symbolTable.get(i).name) && scope==symbolTable.get(i).scope){
                symbolTable.get(i).size = size;
            }
        }
    }
    private static boolean check(String[] arr, String toCheckValue)
    {
        // check if the specified element
        // is present in the array or not
        // using Linear Search method
        boolean test = false;
        for (String element : arr) {
            if (element.equals(toCheckValue)) {
                return true;
            }
        }
        return  false;
    }
    private static boolean checkIfElementIsVect(List<Simbolo> symbolTable, String toCheckValue)
    {
        for (Simbolo x : symbolTable) {
            if (x.name.equals(toCheckValue)) {
                if(x.vect==true){
                    return  true;
                }
            }
        }
        return  false;
    }
    private static boolean checkIfIndexExists(List<String> table, int index)
    {
        if(index>=table.size()){
            return false;
        }
        return  true;
    }

    private void checkAttr() throws SemanticError {
        if(indexExp.size()>1){
            //do something

            for(int i =1; i< indexExp.size()-1;i++){
                indexExp.remove(1);
            }
        }


        if(!isAttr && attrPile.size()==1) {
            //checking if is number or var to put on the .text
                //in case its not a vec
            if(attrPileOfLex.size()==1) {
                if (!leftElementIsVect) {
                    assembly.simpleVarReceivesIntOrVar(attrPileOfLex.get(0), assembly, elementOnTheLeftSideOfAttr);
                } else {
                    assembly.simpleVectReceivesVarOrInt(attrPileOfLex.get(0), indexOfAttr, assembly, elementOnTheLeftSideOfAttr);
                }
            }
            String value = attrPile.get(0);

            int valueInt = helper.changeTypeStringToInt(value);
            int valueLastAdded = helper.changeTypeStringToInt(lastAddSymbol.type);
            if(semanticTable.atribType(valueInt,valueLastAdded)==-1){
                throw new SemanticError("Resultado dessa operacao nao bate com o tipo da variavel que o recebe");
            }
            else if(semanticTable.atribType(valueInt,valueLastAdded)==1){
                System.out.println("O resultado desta operacao pode estar errado, devido ao tipo das variaveis serem diferentes");
                warningOutput += "O resultado desta operacao pode estar errado, devido ao tipo das variaveis serem diferentes\n";
            }
            attrPile.clear();
            attrPileOfLex.clear();
            changedToInit(elementOnTheLeftSideOfAttr);
            leftElementIsVect = false;
        }
        if(!isAttr && attrPile.size()==2) {//vec
            if(attrPileOfLex.size()==2) {
                if (leftElementIsVect) {
                    assembly.simpleVecReceivesVec(attrPileOfLex.get(0), indexOfAttr, assembly, elementOnTheLeftSideOfAttr, attrPileOfLex.get(1));
                } else {
                    assembly.simpleVarReceivesVect(attrPileOfLex.get(0), assembly, elementOnTheLeftSideOfAttr, attrPileOfLex.get(1));
                }
            }
            String value = attrPile.get(0);

            System.out.println(value);
            int valueInt = helper.changeTypeStringToInt(value);
            int valueLastAdded = helper.changeTypeStringToInt(lastAddSymbol.type);
            if(semanticTable.atribType(valueInt,valueLastAdded)==-1){
                throw new SemanticError("Resultado dessa operacao nao bate com o tipo da variavel que o recebe");
            }
            else if(semanticTable.atribType(valueInt,valueLastAdded)==1){
                System.out.println("O resultado desta operacao pode estar errado, devido ao tipo das variaveis serem diferentes");
                warningOutput += "O resultado desta operacao pode estar errado, devido ao tipo das variaveis serem diferentes\n";
            }
            attrPile.clear();
            attrPileOfLex.clear();
            changedToInit(elementOnTheLeftSideOfAttr);
            leftElementIsVect = false;
        }
        else if(attrPile.size()>2 && !isAttr) {
            int result = -1;

            //first time it should grab 3 elements but the other time it should get only 2(op and secEl)
            ///////////////////////////////////// NOT VECTOR BELOW
                String firstEl = attrPile.get(0);
                String op;
                String secEl;
                //int i[2];
                //int j = i[2] + 2;
            if(attrPileOfLex.size()>2) {
                if (checkIfElementIsVect(symbolTable, attrPileOfLex.get(0)) || checkIfElementIsVect(symbolTable, attrPileOfLex.get(2)) || checkIfIndexExists(attrPileOfLex, 3) && checkIfElementIsVect(symbolTable, attrPileOfLex.get(3))) {
                    op = attrPile.get(2);
                    secEl = attrPile.get(3);
                } else {
                    op = attrPile.get(1);
                    secEl = attrPile.get(2);
                }
            }else{
                op = attrPile.get(1);
                secEl = attrPile.get(2);
            }

                int firstElInt = helper.changeTypeStringToInt(firstEl);
                int ops = helper.changeOpsToInt(op);
                int secElInt = helper.changeTypeStringToInt(secEl);
                result = semanticTable.resultType(firstElInt, secElInt, ops);

                String firstStringEl = "";
                if(attrPileOfLex.size()>2){
                    firstStringEl=attrPileOfLex.get(0);
                }
                String stringOp;
                String secStringEl="";
                Boolean flagToken1=false;
                Boolean flagToken2=false;
                if(attrPileOfLex.size()>2) {
                    if (checkIfElementIsVect(symbolTable, attrPileOfLex.get(0)) || checkIfElementIsVect(symbolTable, attrPileOfLex.get(2)) || checkIfIndexExists(attrPileOfLex, 3) && checkIfElementIsVect(symbolTable, attrPileOfLex.get(3))) {
                        stringOp = attrPileOfLex.get(2);
                        secStringEl = attrPileOfLex.get(3);
                        //callfunc
                        if (checkIfElementIsVect(symbolTable, attrPileOfLex.get(0)) && !checkIfElementIsVect(symbolTable, attrPileOfLex.get(2)) && checkIfIndexExists(attrPileOfLex, 3) && !checkIfElementIsVect(symbolTable, attrPileOfLex.get(3))) {
                            if (leftElementIsVect) {
                                assembly.firstSumAndLeftIsVec(attrPileOfLex, assembly, indexOfAttr);
                            } else {
                                assembly.firstOfSumIsVec(attrPileOfLex, assembly, elementOnTheLeftSideOfAttr);
                            }
                        }
                        if (!checkIfElementIsVect(symbolTable, attrPileOfLex.get(0)) && checkIfElementIsVect(symbolTable, attrPileOfLex.get(2)) && checkIfIndexExists(attrPileOfLex, 3) && !checkIfElementIsVect(symbolTable, attrPileOfLex.get(3))) {
                            if (leftElementIsVect) {
                                assembly.secSumAndLeftIsVec(attrPileOfLex, assembly, indexOfAttr);
                            } else {
                                assembly.secOfSumIsVec(attrPileOfLex, assembly, elementOnTheLeftSideOfAttr);
                            }
                        }
                        if (checkIfElementIsVect(symbolTable, attrPileOfLex.get(0)) && !checkIfElementIsVect(symbolTable, attrPileOfLex.get(2)) && checkIfIndexExists(attrPileOfLex, 3) && checkIfElementIsVect(symbolTable, attrPileOfLex.get(3))) {
                            if (leftElementIsVect) {
                                assembly.bothVecWithLeftVec(attrPileOfLex, assembly, elementOnTheLeftSideOfAttr);
                            } else {
                                assembly.bothVec(attrPileOfLex, assembly);
                            }
                        }
                    } else {
                        stringOp = attrPileOfLex.get(1);
                        secStringEl = attrPileOfLex.get(2);
                        flagToken1 = Character.isDigit(attrPileOfLex.get(0).charAt(0));
                        flagToken2 = Character.isDigit(attrPileOfLex.get(2).charAt(0));

                        if (!flagToken1) {
                            assembly.textArray.add("        LD    " + firstStringEl + "\n");
                            assembly.addTo(secStringEl, stringOp, assembly);
                        }
                        if (flagToken1) {
                            assembly.textArray.add("        LDI    " + firstStringEl + "\n");
                            assembly.addTo(secStringEl, stringOp, assembly);
                        }

                    }
                }

                if (result == -1) {
                    throw new SemanticError("Resultado dessa operacao nao bate com o tipo da variavel que o recebe");
                } else if (result == 1) {
                    System.out.println("O resultado desta operacao pode estar errado, devido ao tipo das variaveis serem diferentes");
                    warningOutput += "O resultado desta operacao pode estar errado, devido ao tipo das variaveis serem diferentes\n";
                }
                if(attrPileOfLex.size()>2) {
                    if (checkIfElementIsVect(symbolTable, attrPileOfLex.get(0)) || checkIfElementIsVect(symbolTable, attrPileOfLex.get(2)) || checkIfIndexExists(attrPileOfLex, 3) && checkIfElementIsVect(symbolTable, attrPileOfLex.get(3))) {

                        ///In case there is two vectors
                        if (checkIfElementIsVect(symbolTable, attrPileOfLex.get(0)) && !checkIfElementIsVect(symbolTable, attrPileOfLex.get(2)) && checkIfIndexExists(attrPileOfLex, 3) && checkIfElementIsVect(symbolTable, attrPileOfLex.get(3))) {
                            attrPileOfLex.remove(0);
                            attrPile.remove(0);
                        }
                        attrPileOfLex.remove(0);
                        attrPile.remove(0);
                    }
                    attrPileOfLex.remove(0);
                    attrPileOfLex.remove(0);
                    attrPileOfLex.remove(0);
                }
                attrPile.remove(0);
                attrPile.remove(0);
                attrPile.remove(0);

                String previousEl = secStringEl;

                for (int i = 0; i < attrPile.size(); i = i + 2) {

                    op = attrPile.get(i);
                    secEl = attrPile.get(i + 1);
                    ops = helper.changeOpsToInt(op);
                    secElInt = helper.changeTypeStringToInt(secEl);
                    result = semanticTable.resultType(result, secElInt, ops);

                    if (attrPileOfLex.size() > 2) {
                        stringOp = attrPileOfLex.get(i);
                        secStringEl = attrPileOfLex.get(i + 1);
                        if (checkIfElementIsVect(symbolTable, attrPileOfLex.get(i + 1))) {
                            assembly.addVecTo(attrPileOfLex, assembly);
                        } else {
                            assembly.addTo(secStringEl, stringOp, assembly);

                        }
                    }
                    if (result == -1) {
                        throw new SemanticError("Resultado dessa operacao nao bate com o tipo da variavel que o recebe");
                    } else if (result == 1) {
                        System.out.println("O resultado desta operacao pode estar errado, devido ao tipo das variaveis serem diferentes");
                        warningOutput += "O resultado desta operacao pode estar errado, devido ao tipo das variaveis serem diferentes\n";
                    }
                    if (attrPileOfLex.size() > 2) {
                        if (checkIfElementIsVect(symbolTable, attrPileOfLex.get(1)) || checkIfIndexExists(attrPileOfLex, 2) && checkIfElementIsVect(symbolTable, attrPileOfLex.get(2))) {
                            i++;
                        }
                    }
                    //STORE
                    assembly.store(elementOnTheLeftSideOfAttr, assembly, leftElementIsVect);
                }

                int valueLastAdded = helper.changeTypeStringToInt(lastAddSymbol.type);
                int atribResult = semanticTable.atribType(result, valueLastAdded);
                if (atribResult == -1) {
                    throw new SemanticError("Resultado dessa operacao nao bate com o tipo da variavel que o recebe");
                } else if (atribResult == 1) {
                    System.out.println("O resultado desta operacao pode estar errado, devido ao tipo das variaveis serem diferentes");
                    warningOutput += "O resultado desta operacao pode estar errado, devido ao tipo das variaveis serem diferentes\n";
                }
                attrPile.clear();
                attrPileOfLex.clear();
                changedToInit(elementOnTheLeftSideOfAttr);
                leftElementIsVect = false;

        }

    }

    public void executeAction(int action, Token token) throws SemanticError, SyntaticError {
        System.out.println(action + " " + token);


        checkAttr();

        switch(action){
            case -2:
                pileOfScopes.add(0);
                break;
            case 1: {
                this.type = token.getLexeme();
                break;
            }
            case 3:
                Simbolo vet = new Simbolo();
                vet.name =token.getLexeme();
                vet.type = this.type;
                vet.vect = true;
                vet.scope = pileOfScopes.get(pileOfScopes.size()-1);
                if(!checkIfVarExistsCurrentContext(vet)){
                    symbolTable.add(vet);
                }else{
                    throw new SemanticError("Variavel ja declarada neste escopo");
                }

                break;
            //TYPE
            case 4:{
                Simbolo sim = new Simbolo();
                sim.name = token.getLexeme();
                sim.type = this.type;
                sim.scope = pileOfScopes.get(pileOfScopes.size()-1);
                if(!checkIfVarExistsCurrentContext(sim)){
                    if(isAttr){
                        attrPile.add(sim.type);
                        attrPileOfLex.add(token.getLexeme());
                    }
                    symbolTable.add(sim);

                }else{
                    throw new SemanticError("Variavel ja declarada neste escopo");
                }


                break;
            }
            case 5:
                Simbolo par = new Simbolo();
                par.name = token.getLexeme();
                par.type = this.type;
                par.param = true;
                par.scope = pileOfScopes.size()-1;
                symbolTable.add(par);
                break;
            case 6:
                Simbolo func = new Simbolo();
                func.name = token.getLexeme();
                func.scope = pileOfScopes.get(pileOfScopes.size()-1);
                if(!checkIfVarExistsCurrentContext(func)){
                    throw new SemanticError("Funcao inexistente");
                }
                break;
            case 7:
                Simbolo parArr = new Simbolo();
                parArr.name = token.getLexeme();
                parArr.type = this.type;
                parArr.param = true;
                parArr.scope = pileOfScopes.size();
                parArr.vect = true;
                symbolTable.add(parArr);
                break;
            case 8://adding var
                String name = token.getLexeme();
                String type = returnObjType(name);

                Simbolo x = new Simbolo();
                x.scope =pileOfScopes.get(pileOfScopes.size()-1);
                x.type = type;
                x.name = name;
                if(!checkIfVarExistsCurrentContext(x)){
                    throw new SemanticError("A variavel sendo usada nao foi declarada");
                }else{
                    changedToUsed(x);
                }
               if(!checkIfIsInit(x)){
                   System.out.println("Uso de variavel nao inicializada");
                   warningOutput += "Uso de variavel nao inicializada\n";
               }
               if(vecExp){
                  indexExp.add(token.getLexeme());
               }

                if(isAttr){
                    attrPile.add(type);
                    attrPileOfLex.add(token.getLexeme());
                }
                if(isCout){
                    if(!checkIfElementIsVect(symbolTable,token.getLexeme()) && !checkIfElementIsVect(symbolTable,lastSimbol.name)){
                        assembly.writeVarOrInt(assembly, token.getLexeme());
                        //if vec - all changes will be when index is already passed
                    }
                    if(checkIfElementIsVect(symbolTable,lastSimbol.name)){
                        assembly.writeIfElIsVec(assembly,lastSimbol.name,token.getLexeme());
                    }
                }

                if(isCin){
                    if(!checkIfElementIsVect(symbolTable,token.getLexeme()) && !checkIfElementIsVect(symbolTable,lastSimbol.name)) {
                        assembly.readVarOrInt(assembly, token.getLexeme());
                    }
                    if(checkIfElementIsVect(symbolTable,lastSimbol.name)){
                        assembly.readIfElIsVec(assembly,lastSimbol.name, token.getLexeme());
                    }
                }


                break;
            case 9://adding int
                if(isAttr){
                    attrPile.add("int");
                    attrPileOfLex.add(token.getLexeme());
                }
                if(isCout){
                    if(checkIfElementIsVect(symbolTable,lastSimbol.name)){
                        //index
                        assembly.writeIfElIsVec(assembly,lastSimbol.name, token.getLexeme());
                    }
                    else{
                        assembly.writeVarOrInt(assembly,token.getLexeme());
                    }
                }
                if(isCin){
                    if(checkIfElementIsVect(symbolTable,lastSimbol.name)) {
                        assembly.readIfElIsVec(assembly, lastSimbol.name, token.getLexeme());
                    }
                }
                if(vecExp){
                    indexExp.add(token.getLexeme());
                }

                break;


            case 10://float
                if(isAttr){
                    attrPile.add("float");
                }
                break;
            case 11://char
                if(isAttr){
                    attrPile.add("char");
                }
                break;
            case 12://ops
                if(isAttr){
                    attrPile.add(token.getLexeme());
                    attrPileOfLex.add(token.getLexeme());
                }
                if(vecExp){
                    indexExp.add(token.getLexeme());
                }

                break;

            case 13://string
                if(isAttr){
                    attrPile.add("string");
                }
                break;
            case 14://
                isVect = true;
                indexOfAttr=token.getLexeme();
            break;
            case 15://vet
                String vectType = returnObjType(token.getLexeme());
                if(isAttr){
                    attrPile.add(vectType);
                }
                leftElementIsVect = true;

            break;
            case 17://Function declare
                Simbolo functionSymb = new Simbolo();
                functionSymb.name = token.getLexeme();
                functionSymb.type = this.type;
                functionSymb.func = true;
                functionSymb.scope = pileOfScopes.get(pileOfScopes.size()-1);
                if(!checkIfVarExistsCurrentContext(functionSymb)){
                    symbolTable.add(functionSymb);
                }else{
                    throw new SemanticError("Funcao ja declarada neste escopo");
                }
                break;
            case 23://bool
                if(isAttr){
                    attrPile.add("boolean");
                }
                break;
            case 24://Initial phase of attrib
                lastAddSymbol = findSymbolSameNameAndScope(lastSimbol);
                elementOnTheLeftSideOfAttr = lastSimbol.name;
                // NEED TO CHANGE ----------------- CHECK FOR TYPE ISSUES
                isAttr = true;
                isVect = false;
                break;
            case 32://Scope open
                scopeCounter++;
                pileOfScopes.add(scopeCounter);
                break;
            case 33://Scope close
                scopeCounter--;
                pileOfScopes.remove(this.pileOfScopes.size()-1);
                break;
            case 34://Attrib close
                isAttr=false;
                isCout=false;
                isCin =false;
                break;
            case 35:
                isCout = true;
                break;
            case 36:
                isCin = true;
                break;
            case 37:
                changeSize(Integer.parseInt(token.getLexeme()), lastSimbol.scope, lastSimbol.name);
                break;
            case 38:
                vecExp = true;
                break;
            case 39:
                vecExp = false;
                break;
        }
        if(!isVect) {
            lastSimbol.name = token.getLexeme();
            lastSimbol.scope = pileOfScopes.get(pileOfScopes.size() - 1);
        }
    }
}
