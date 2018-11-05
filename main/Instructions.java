package com.amuthan.bt.a8085;

  /*Creted by Amuthan
    Contains the list and properties
    of instruction set of 8085 which is available for the app
    Currently compatible with 68 instructions
   */

public class Instructions {
    String[] instructions={"MOV","MVI","LDA","LDAX","LXI","LHLD","STA","STAX","SHLD","XCHG","SPHL","XTHL",
    "PUSH","POP","ADD","ADC","ADI","ACI","DAD","SUB","SBB","SUI","SBI","INR","INX","DCR","DCX","CMP","CPI","ANA","ANI",
    "ORA","ORI","XRA","XRI","CMA","CMC","STC","JMP","JZ","JC","JNC","JP","JM","JZ","JNZ","JPE","JPO",
    "CALL","CC","CNC","CP","CM","CZ","CNZ","CPE","CPO","RET","RC","RNC","RP","RM","RZ","RNZ","RPE","RPO","NOP","HLT"};
      Instructions(){}
    int getProp(String ins){
        switch(ins){
            case "MOV":case"LXI":case"MVI":
                return 2;
            case "ORI":case "ORA":case "ACI":case "DAD":case "INX":case "DCX":case "LHLD":case "SHLD":case "ANI":
            case "ANA":case "ADC":case "JMP":case "SUI":case "ADI":case "SUB":case "SBI":case "ADD":case "LDA":case "STA":
            case "DCR":case "INC":case "LDAX":case "STAX":case "JZ":case "JNZ":case "JC":case "JNC":case "JPO":case "JPE":
            case "XRI":case "XRA":case "CMP": case "CPI":case "JP":case "JM":case "CMC":case "CALL":case "INR":
            case "CZ":case "CNZ":case "CC":case "CNC":case "CP":case "CPO":case "CPE":case "CM":case "PUSH":case "POP":

                return 1;
            default:
                return 0;


        }


    }


}
