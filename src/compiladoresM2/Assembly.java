package compiladoresM2;

import gals.Token;

import java.util.ArrayList;
import java.util.List;




public class Assembly {
    public List<String> dataArray = new ArrayList<>();
    public List<String> textArray = new ArrayList<>();
    private String tempAssembly[] = {"1000","1001","1002"};


    public void writeIfElIsVec(Assembly assembly, String lastToken,String currentToken ){
        Boolean flag = Character.isDigit(currentToken.charAt(0));
        if (flag) {
            assembly.textArray.add("        LDI       "+currentToken+"\n");
        }
        else{
            assembly.textArray.add("        LD        "+currentToken+"\n");
        }
        assembly.textArray.add("        STO       $indr"+"\n");
        assembly.textArray.add("        LDV       "+lastToken+"\n");
        assembly.textArray.add("        STO       $out_port"+"\n");

    }
    public void readIfElIsVec(Assembly assembly, String lastToken,String currentToken ){
        Boolean flag = Character.isDigit(currentToken.charAt(0));
        if (flag) {
            assembly.textArray.add("        LDI       "+currentToken+"\n");
        }
        else{
            assembly.textArray.add("        LD        "+currentToken+"\n");
        }
        assembly.textArray.add("        STO       $indr"+"\n");
        assembly.textArray.add("        LD        $in_port"+"\n");
        assembly.textArray.add("        STOV      "+lastToken+"\n");
    }


    public void readVarOrInt(Assembly assembly, String token){
        Boolean flag = Character.isDigit(token.charAt(0));
        if (flag) {
            assembly.textArray.add("        LDI   $in_port\n");
        }
        else{
            assembly.textArray.add("        LD   $in_port\n");
        }
        assembly.textArray.add("        STO  " +token+"\n");
    }
    public void writeVarOrInt(Assembly assembly, String  token){
        Boolean flag = Character.isDigit(token.charAt(0));
        if(flag){
            assembly.textArray.add("        LDI  " +token+"\n");
        }
        else{
            assembly.textArray.add("        LD  " +token+"\n");
        }
        assembly.textArray.add("        STO   $out_port\n");
    }

    public void simpleVectReceivesVarOrInt(String token,String indexLeft, Assembly assembly, String elementOnTheLeftSideOfAttr){
        System.out.println("simpleImmediateVecIntAttr");
        Boolean flag = Character.isDigit(indexLeft.charAt(0));
        if(flag){
            assembly.textArray.add("        LDI  " +indexLeft+"\n");
        }
        else{
            assembly.textArray.add("        LD   " +indexLeft+"\n");
        }

        assembly.textArray.add("        STO  " +tempAssembly[0]+"\n");
        Boolean tokenFlag = Character.isDigit(token.charAt(0));
        if(tokenFlag){
            assembly.textArray.add("        LDI  " +token+"\n");
        }
        else{
            assembly.textArray.add("        LD   " +token+"\n");
        }
        assembly.textArray.add("        STO  " +tempAssembly[1]+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[0]+"\n");
        assembly.textArray.add("        STO  " +"$indr"+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[1]+"\n");
        assembly.textArray.add("        STOV " +elementOnTheLeftSideOfAttr+"\n");
    }

    public void simpleVecReceivesVec(String token,String indexLeft, Assembly assembly, String elementOnTheLeftSideOfAttr, String indexRight){
        System.out.println("simpleVecintVecIntAttr");
        Boolean indexFlag = Character.isDigit(indexLeft.charAt(0));
        if(indexFlag){
            assembly.textArray.add("        LDI  " +indexLeft+"\n");
        }
        else{
            assembly.textArray.add("        LD   " +indexLeft+"\n");
        }
        assembly.textArray.add("        STO  " +tempAssembly[0]+"\n");
        Boolean indexFlagRight = Character.isDigit(indexRight.charAt(0));
        if(indexFlagRight){
            assembly.textArray.add("        LDI   " +indexRight+"\n");
        }
        else{
            assembly.textArray.add("        LD    " +indexRight+"\n");
        }
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +token+"\n");
        assembly.textArray.add("        STO   " +tempAssembly[1]+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[0]+"\n");
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[1]+"\n");
        assembly.textArray.add("        STOV   " +elementOnTheLeftSideOfAttr+"\n");
    }
    public void simpleVarReceivesVect(String token, Assembly assembly, String elementOnTheLeftSideOfAttr, String indexRight){
        System.out.println("simpleVectToVar");
        assembly.textArray.add("        LDI  " +indexRight+"\n");
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +token+"\n");
        assembly.textArray.add("        STO   " +elementOnTheLeftSideOfAttr+"\n");
    }

    public void simpleVarReceivesIntOrVar(String token, Assembly assembly, String elementOnTheLeftSideOfAttr){
        System.out.println("simpleImmediateAttr");
        Boolean flag = Character.isDigit(token.charAt(0));
        if (flag) {
            assembly.textArray.add("        LDI  " +token+"\n");
        }
        else{
            assembly.textArray.add("        LD   " +token+"\n");
        }
        assembly.textArray.add("        STO  " +elementOnTheLeftSideOfAttr+"\n");
    }

    public void addTo(String token2,String tokenOp, Assembly assembly){
        Boolean flag = Character.isDigit(token2.charAt(0));
        if(flag){
            if(tokenOp.equals("+")) {assembly.textArray.add("        ADDI   " + token2 + "\n");}
            else{assembly.textArray.add("        SUBI   " + token2 + "\n");}
        }
        else{
            if(tokenOp.equals("+")) {assembly.textArray.add("        ADD    " + token2 + "\n");}
            else{assembly.textArray.add("        SUB    " + token2 + "\n");}
        }
        if(tokenOp.equals(">>")){assembly.textArray.add("        SLL    " + token2 + "\n");}
        if(tokenOp.equals("<<")){assembly.textArray.add("        SLR    " + token2 + "\n");}
        if(tokenOp.equals("&")){assembly.textArray.add("        AND    " + token2 + "\n");}
        if(tokenOp.equals("|")){assembly.textArray.add("        OR    " + token2 + "\n");}
    }
    public  void  addVecTo(List<String> pile, Assembly assembly){
        assembly.textArray.add("        STO   " +tempAssembly[0]+"\n");
        Boolean flag = Character.isDigit(pile.get(2).charAt(0));
        if(flag){
            assembly.textArray.add("        LDI   " +pile.get(2)+"\n");
        }
        else {
            assembly.textArray.add("        LD    "+pile.get(2)+"\n");
        }
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +pile.get(1)+"\n");
        assembly.textArray.add("        STO   " +tempAssembly[1]+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[0]+"\n");
        assembly.textArray.add("        ADD   " +tempAssembly[1]+"\n");
    }

    public void firstOfSumIsVec(List<String> pile, Assembly assembly, String leftSideELem){
        Boolean flag = Character.isDigit(pile.get(1).charAt(0));
        if(flag){
            assembly.textArray.add("        LDI  " +pile.get(1)+"\n");
        }
        else {
            assembly.textArray.add("        LD   " +pile.get(1)+"\n");
        }
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +pile.get(0)+"\n");
        Boolean flag2 = Character.isDigit(pile.get(3).charAt(0));
        if(flag2){
            if(pile.get(2).equals("+")) {assembly.textArray.add("        ADDI   " + pile.get(3) + "\n");}
            if(pile.get(2).equals("-")){assembly.textArray.add("         SUBI   "+pile.get(3)+"\n");}
            if(pile.get(2).equals(">>")){assembly.textArray.add("        SLL    " + pile.get(3) + "\n");}
            if(pile.get(2).equals("<<")){assembly.textArray.add("        SLR    " + pile.get(3) + "\n");}
            if(pile.get(2).equals("&")){assembly.textArray.add("        AND    " + pile.get(3) + "\n");}
            if(pile.get(2).equals("|")){assembly.textArray.add("        OR    " + pile.get(3) + "\n");}
        }
        else{
            if(pile.get(2).equals("+")) {assembly.textArray.add("        ADD   " + pile.get(3) + "\n");}
            if(pile.get(2).equals("-")){assembly.textArray.add("         SUB   "+pile.get(3)+"\n");}
            if(pile.get(2).equals(">>")){assembly.textArray.add("        SLL    " + pile.get(3) + "\n");}
            if(pile.get(2).equals("<<")){assembly.textArray.add("        SLR    " + pile.get(3) + "\n");}
            if(pile.get(2).equals("&")){assembly.textArray.add("        AND    " + pile.get(3) + "\n");}
            if(pile.get(2).equals("|")){assembly.textArray.add("        OR    " + pile.get(3) + "\n");}
        }
    }
    public void secOfSumIsVec(List<String> pile, Assembly assembly, String leftSideELem) {

        Boolean flag = Character.isDigit(pile.get(3).charAt(0));
        if(flag){
            assembly.textArray.add("        LDI  " +pile.get(3)+"\n");
        }
        else {
            assembly.textArray.add("        LD   " +pile.get(3)+"\n");
        }
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +pile.get(2)+"\n");
        assembly.textArray.add("        STO   " +tempAssembly[0]+"\n");
        Boolean flag2 = Character.isDigit(pile.get(0).charAt(0));
        if(flag2){
            assembly.textArray.add("        LDI   " +pile.get(0)+"\n");
        }
        else{
            assembly.textArray.add("        LD    " +pile.get(0)+"\n");
        }
        if(pile.get(1).equals("+")) {assembly.textArray.add("        ADDI  " +tempAssembly[0] + "\n");}
        if(pile.get(1).equals("-")){assembly.textArray.add("         SUBI  "+tempAssembly[0]+"\n");}
        if(pile.get(1).equals(">>")){assembly.textArray.add("        SLL   " + tempAssembly[0] + "\n");}
        if(pile.get(1).equals("<<")){assembly.textArray.add("        SLR   " + tempAssembly[0] + "\n");}
        if(pile.get(1).equals("&")){assembly.textArray.add("        AND    " + tempAssembly[0] + "\n");}
        if(pile.get(1).equals("|")){assembly.textArray.add("        OR     " + tempAssembly[0] + "\n");}
    }
    public void bothVec(List<String> pile, Assembly assembly) {
        Boolean flag = Character.isDigit(pile.get(1).charAt(0));
        if(flag){
            assembly.textArray.add("        LDI  " +pile.get(1)+"\n");
        }
        else {
            assembly.textArray.add("        LD   " +pile.get(1)+"\n");
        }
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +pile.get(0)+"\n");
        assembly.textArray.add("        STO   " +tempAssembly[0]+"\n");
        Boolean flag2 = Character.isDigit(pile.get(4).charAt(0));
        if(flag2){
            assembly.textArray.add("        LDI  " +pile.get(4)+"\n");
        }
        else {
            assembly.textArray.add("        LD   " +pile.get(4)+"\n");
        }
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +pile.get(3)+"\n");
        assembly.textArray.add("        STO   " +tempAssembly[1]+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[0]+"\n");
        if(pile.get(2).equals("+")) {assembly.textArray.add("        ADDI  " +tempAssembly[1] + "\n");}
        if(pile.get(2).equals("-")){assembly.textArray.add("         SUBI  "+tempAssembly[1]+"\n");}
        if(pile.get(2).equals(">>")){assembly.textArray.add("        SLL   " + tempAssembly[1] + "\n");}
        if(pile.get(2).equals("<<")){assembly.textArray.add("        SLR   " + tempAssembly[1] + "\n");}
        if(pile.get(2).equals("&")){assembly.textArray.add("        AND    " + tempAssembly[1] + "\n");}
        if(pile.get(2).equals("|")){assembly.textArray.add("        OR     " + tempAssembly[1] + "\n");}
    }

    public void bothVecWithLeftVec(List<String> pile, Assembly assembly, String leftElem){
        Boolean leftFlag = Character.isDigit(leftElem.charAt(0));
        if(leftFlag){
            assembly.textArray.add("        LDI  " +pile.get(1)+"\n");
        }
        else {
            assembly.textArray.add("        LD   " +pile.get(1)+"\n");
        }
        assembly.textArray.add("        STO     "+ tempAssembly[0]+"\n");
        Boolean flag = Character.isDigit(pile.get(1).charAt(0));
        if(flag){
            assembly.textArray.add("        LDI  " +pile.get(1)+"\n");
        }
        else {
            assembly.textArray.add("        LD   " +pile.get(1)+"\n");
        }
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +pile.get(0)+"\n");
        assembly.textArray.add("        STO   " +tempAssembly[1]+"\n");
        Boolean flag2 = Character.isDigit(pile.get(4).charAt(0));
        if(flag2){
            assembly.textArray.add("        LDI  " +pile.get(4)+"\n");
        }
        else {
            assembly.textArray.add("        LD   " +pile.get(4)+"\n");
        }
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +pile.get(3)+"\n");
        assembly.textArray.add("        STO   " +tempAssembly[2]+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[1]+"\n");
        assembly.textArray.add("        ADD  " +tempAssembly[2]+"\n");
        assembly.textArray.add("        STO  " +tempAssembly[1]+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[0]+"\n");
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[1]+"\n");

    }
    public void firstSumAndLeftIsVec(List<String> pile, Assembly assembly,String indexOfAttr){
        Boolean leftFlag = Character.isDigit(indexOfAttr.charAt(0));
        if(leftFlag){
            assembly.textArray.add("        LDI  " +indexOfAttr+"\n");
        }
        else {
            assembly.textArray.add("        LD   " +indexOfAttr+"\n");
        }
        assembly.textArray.add("        STO   " +tempAssembly[0]+"\n");
        Boolean rightIndex = Character.isDigit(pile.get(1).charAt(0));
        if(rightIndex){
            assembly.textArray.add("        LDI  " +pile.get(1)+"\n");
        }else {
            assembly.textArray.add("        LD   " +pile.get(1)+"\n");
        }
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +pile.get(0)+"\n");
        Boolean leftEl = Character.isDigit(pile.get(3).charAt(0));
        if(leftEl){
            assembly.textArray.add("        ADDI   " +pile.get(3)+"\n");
        }
        else{
            assembly.textArray.add("        ADD   " +pile.get(3)+"\n");
        }
        assembly.textArray.add("        STO   " +tempAssembly[1]+"\n");
        assembly.textArray.add("        LD    " +tempAssembly[0]+"\n");
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LD    " +tempAssembly[1]+"\n");
    }
    public void secSumAndLeftIsVec(List<String> pile, Assembly assembly, String indexOfAttr){
        Boolean leftFlag = Character.isDigit(indexOfAttr.charAt(0));
        if(leftFlag){
            assembly.textArray.add("        LDI  " +indexOfAttr+"\n");
        }
        else {
            assembly.textArray.add("        LD   " +indexOfAttr+"\n");
        }
        assembly.textArray.add("        STO   " +tempAssembly[0]+"\n");
        Boolean rightIndex = Character.isDigit(pile.get(3).charAt(0));
        if(rightIndex){
            assembly.textArray.add("        LDI  " +pile.get(3)+"\n");
        }else {
            assembly.textArray.add("        LD   " +pile.get(3)+"\n");
        }
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LDV   " +pile.get(2)+"\n");
        assembly.textArray.add("        STO   " +tempAssembly[1]+"\n");
        Boolean varOrNum = Character.isDigit(pile.get(0).charAt(0));
        if(varOrNum){
            assembly.textArray.add("        LDI   " +pile.get(0)+"\n");
        }
        else {
            assembly.textArray.add("        LD   " +pile.get(0)+"\n");
        }
        if(pile.get(1).equals("+")) {assembly.textArray.add("        ADDI  " +tempAssembly[1] + "\n");}
        if(pile.get(1).equals("-")){assembly.textArray.add("         SUBI  "+tempAssembly[1]+"\n");}
        if(pile.get(1).equals(">>")){assembly.textArray.add("        SLL   " + tempAssembly[1] + "\n");}
        if(pile.get(1).equals("<<")){assembly.textArray.add("        SLR   " + tempAssembly[1] + "\n");}
        if(pile.get(1).equals("&")){assembly.textArray.add("        AND    " + tempAssembly[1] + "\n");}
        if(pile.get(1).equals("|")){assembly.textArray.add("        OR     " + tempAssembly[1] + "\n");}
        assembly.textArray.add("        STO   " +tempAssembly[1]+"\n");
        assembly.textArray.add("        LD   " +tempAssembly[0]+"\n");
        assembly.textArray.add("        STO   " +"$indr"+"\n");
        assembly.textArray.add("        LD   " + tempAssembly[1]+"\n");


    }
    public  void store(String elementOnTheLeft, Assembly assembly, boolean isVec){
        if(!isVec) {
            assembly.textArray.add("        STO    " + elementOnTheLeft + "\n");
        }
        else{
            assembly.textArray.add("        STOV   " + elementOnTheLeft + "\n");
        }
    }

    public void printAssemblyDataTable(Assembly assembly){
        System.out.print(assembly.dataArray.get(0));
        for(int i = 1;i<assembly.dataArray.size();i++){
            String init = "0";
            String name = assembly.dataArray.get(i).split(",")[0];
            int size = Integer.parseInt(assembly.dataArray.get(i).split(",")[2]);
            for(int j=1;j<size;j++){
                init = init + " , 0";
            }
            System.out.print("      "+name+" : " +init+"\n");
        }
    }

    public void printAssemblyTextTable(Assembly assembly){
        for(String x : assembly.textArray){
            System.out.print(x);
        }
    }

    public String getAssemblyString(Assembly assembly){
        String assemblyString = new String();
        assemblyString += assembly.dataArray.get(0).toString();
        for(int i = 1;i<assembly.dataArray.size();i++){
            String init = "0";
            String name = assembly.dataArray.get(i).split(",")[0];
            int size = Integer.parseInt(assembly.dataArray.get(i).split(",")[2]);
            for(int j=1;j<size;j++){
                init = init + " , 0";
            }
            assemblyString += "      "+name+" : " +init+"\n";
        }
        for(String x : assembly.textArray){
            assemblyString += x.toString();
        }
        return assemblyString;
    }



}