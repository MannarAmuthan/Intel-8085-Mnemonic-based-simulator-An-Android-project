package com.amuthan.bt.a8085;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by AMUTHAN on 9/9/2018.
 * An activity for fetching and execution
 */

public class Fetching extends AppCompatActivity {
    String pointer;
    Ram ram1;
    boolean stop = false;
    HashMap<String, String> adrList, insList, opHash1, opHash2, laHash;
    String pointerAddress, pointerInstruction, pointerOp;
    EditText address, inputAdr, inputData;
    Button getData, setData, exec,next;
    String RetFromRoutine;
    TextView result;
    ListView list;
    ArrayList<String> RegList;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fetching);
        Intent i = getIntent();

        //getting the instructions,operand values from previous activity via intent

        adrList = (HashMap<String, String>) i.getSerializableExtra("addressmap");
        insList = (HashMap<String, String>) i.getSerializableExtra("insmap");
        opHash1 = (HashMap<String, String>) getIntent().getSerializableExtra("opsmap1");
        opHash2 = (HashMap<String, String>) getIntent().getSerializableExtra("opsmap2");
        laHash = (HashMap<String, String>) getIntent().getSerializableExtra("opsmap3");
        final int initialadd = Integer.parseInt(i.getStringExtra("pointer"));
        RegList= new ArrayList<>();

        //initializing ram
        ram1 = new Ram(getApplicationContext());

        //defining widgets

        address = (EditText) findViewById(R.id.addresscheck);
        inputAdr = (EditText) findViewById(R.id.inputadr);
        inputData = (EditText) findViewById(R.id.inputdata);
        setData = (Button) findViewById(R.id.setData);
        getData = (Button) findViewById(R.id.check);
        next = (Button) findViewById(R.id.next);
        exec = (Button) findViewById(R.id.exe);
        result=(TextView)findViewById(R.id.result);
        list=(ListView)findViewById(R.id.listReg);
        address.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        inputAdr.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        inputData.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        result.setTextColor(Color.RED);
        RetFromRoutine="";

        //fetching operation,automatically invoked in the starting of activity launching
        try{
            Fetch(adrList, insList, opHash1, opHash2, laHash, initialadd);}
        catch (Exception e){}

        //the list for showing register's statuses
        UpdateList();

        //check ram's values

        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(address.getText().toString().length()!=0){
               // Toast.makeText(getApplicationContext(), ram1.get(address.getText().toString()), Toast.LENGTH_SHORT).show();
                    result.setText(address.getText().toString()+":    "+ram1.get(address.getText().toString()));
                    }
                UpdateList();
            }
        });

        //for control flow, check next ram values
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(address.getText().toString().length()!=0){
                 int nextAdr=toInt(address.getText().toString())+1;
                 address.setText(toHex(nextAdr));
                    if(address.getText().toString().length()!=0){
                        result.setText(address.getText().toString()+":    "+ram1.get(address.getText().toString()));
                    }
                }
                UpdateList();
            }
        });
        //for input giving, if needed
        setData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputAdr.getText().toString().length()!=0){
                    ram1.insert(inputAdr.getText().toString(), inputData.getText().toString());

            } UpdateList();
            }
        });


        //execution button
        exec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop = false;
                adrList = (HashMap<String, String>) getIntent().getSerializableExtra("addressmap");
                insList = (HashMap<String, String>) getIntent().getSerializableExtra("insmap");
                opHash1 = (HashMap<String, String>) getIntent().getSerializableExtra("opsmap1");
                opHash2 = (HashMap<String, String>) getIntent().getSerializableExtra("opsmap2");
                laHash = (HashMap<String, String>) getIntent().getSerializableExtra("opsmap3");
                final int initialadd = Integer.parseInt(getIntent().getStringExtra("pointer"));
                Fetch(adrList, insList, opHash1, opHash2, laHash, initialadd);
                UpdateList();
            }
        });


    }
    //List for easy check the register's values
    private void UpdateList() {
        RegList.clear();
        RegList.add("A"+"   "+ram1.get("A"));
        RegList.add("B"+"   "+ram1.get("B"));
        RegList.add("C"+"   "+ram1.get("C"));
        RegList.add("D"+"   "+ram1.get("D"));
        RegList.add("E"+"   "+ram1.get("E"));
        RegList.add("H"+"   "+ram1.get("H"));
        RegList.add("L"+"   "+ram1.get("L"));
        RegList.add("M"+"   "+ram1.get("M"));
        RegList.add("SP"+"   "+ram1.get("SP"));
        RegList.add("zero flag"+"   "+ram1.get("zero"));
        RegList.add("carry flag"+"   "+ram1.get("carry"));
        RegList.add("sign flag"+"   "+ram1.get("sign"));
        RegList.add("parity flag"+"   "+ram1.get("parity"));
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, RegList);
        list.setAdapter(adapter);
    }

    void Fetch(HashMap<String, String> adrList, HashMap<String, String> insList, HashMap<String, String> opHash1, HashMap<String, String> opHash2, HashMap<String, String> opHash3, int startingAddress) {
       try {
            AddressModes mo = new AddressModes();
            while (!stop) {
                pointer = String.valueOf(startingAddress);
                pointerInstruction = insList.get(pointer);
                pointerAddress = adrList.get(pointer);

                //Data transfer instructions

                if (pointerInstruction.equals("LDA")) {
                    String source = hexOrReg(opHash1.get(pointer));
                    String value=ram1.get(source);
                    ram1.insert("A",value);


                }
                if (pointerInstruction.equals("MOV")) {
                    String source = hexOrReg(opHash2.get(pointer));
                    String dest = hexOrReg(opHash1.get(pointer));
                    String data = ram1.get(source);
                   if(ram1.insert(dest.trim(),data.trim())){

                   }
                   else {EightBitErr(pointer);}

                }
                if (pointerInstruction.equals("MVI")) {
                    String source = opHash2.get(pointer).replace("#", "").replace("H", "");
                    String dest = hexOrReg(opHash1.get(pointer));
                    if(ram1.insert(dest.trim(),source.trim())){

                    }
                    else {EightBitErr(pointer);}
                }
                if (pointerInstruction.equals("HLT")) {
                    stop = true;
                }


                if (pointerInstruction.equals("STA")) {
                    String source = hexOrReg(opHash1.get(pointer));
                    String data = ram1.get("A");
                    ram1.insert(source.trim(), data.trim());
                }
                if (pointerInstruction.equals("LDAX")) {
                    String pair = opHash1.get(pointer).replace("#", "");
                    String source = "";
                    if (pair.equals("B")) {
                        source = ram1.get("B") + ram1.get("C");
                    } if (pair.equals("D")) {
                        source = ram1.get("D") + ram1.get("E");
                    } if (pair.equals("H")) {
                        source = ram1.get("H") + ram1.get("L");
                    }


                    ram1.insert("A", ram1.get(source));
                }
                if (pointerInstruction.equals("STAX")) {
                    String pair = opHash1.get(pointer).replace("#", "");
                    String dest= "";

                    if (pair.equals("B")) {
                        dest = ram1.get("B") + ram1.get("C");
                    } if (pair.equals("D")) {
                        dest = ram1.get("D") + ram1.get("E");
                    } if (pair.equals("H")) {
                        dest = ram1.get("H") + ram1.get("L");
                    }

                    ram1.insert(dest, ram1.get("A"));
                }
                if(pointerInstruction.equals("XCHG")){
                    String h=ram1.get("H");String l=ram1.get("L");
                    String d=ram1.get("D");String e=ram1.get("E");
                    ram1.insert("H",d);ram1.insert("L",e);
                    ram1.insert("D",h);ram1.insert("E",l);


                }
                if(pointerInstruction.equals("LXI")){
                String dest = hexOrReg(opHash1.get(pointer));
                String source= opHash2.get(pointer).replace("H","");
                if(source.length()!=4){SixteenbitErr(pointer);break;}
                if(dest.equals("B")){
                    ram1.insert("B", String.valueOf(source.charAt(0))+String.valueOf(source.charAt(1)));
                    ram1.insert("C", String.valueOf(source.charAt(2))+String.valueOf(source.charAt(3)));
                }
                if(dest.equals("D")){
                        ram1.insert("D", String.valueOf(source.charAt(0))+String.valueOf(source.charAt(1)));
                        ram1.insert("E", String.valueOf(source.charAt(2))+String.valueOf(source.charAt(3)));
                    }
                if(dest.equals("H")){
                        ram1.insert("H", String.valueOf(source.charAt(0))+String.valueOf(source.charAt(1)));
                        ram1.insert("L", String.valueOf(source.charAt(2))+String.valueOf(source.charAt(3)));
                    }
                if(dest.equals("SP")){
                        ram1.insert("SP",source);

                    }
                }
                if(pointerInstruction.equals("LHLD")){
                    String data=opHash1.get(pointer).replace("H","");
                    int nextData=toInt(data)+1;
                    ram1.insert("H",data);ram1.insert("L",toHex(nextData));


                }
                if(pointerInstruction.equals("SHLD")){
                    String data=opHash1.get(pointer).replace("H","");
                    int nextData=toInt(data)+1;
                    ram1.insert(data,ram1.get("L"));ram1.insert(toHex(nextData),ram1.get("H"));
                }

                //Incrementation and decrementation

                if(pointerInstruction.equals("INR")){
                    String value=opHash1.get(pointer).replace("#","");
                    String source= "";
                    if(value.length()==1){source=value;}
                    else {source=value.replace("H","");}
                    int data=toInt(ram1.get(source));
                    String modified=toHex(data+1);
                    ram1.insert(value,modified);


                }
                if(pointerInstruction.equals("DCR")){
                    String value=opHash1.get(pointer).replace("#","");
                    String source="";
                    if(value.length()==1){source=value;}
                    else {source=value.replace("H","");}
                    int data=toInt(ram1.get(source));
                    String modified=toHex(data-1);
                    ram1.insert(value,modified);



                }

                //Arithmetical and logical instructions

                if(pointerInstruction.equals("ADD")){
                    String source = hexOrReg(opHash1.get(pointer));
                    String  op1=ram1.get(source);
                    int value1=toInt(op1);
                    int value2=toInt(ram1.get("A"));
                    int total=value1+value2;
                    String val=toHex(total);
                    if(total>255){
                        ram1.insert("carry","1");
                        val= String.valueOf(toHex(total).charAt(1))+String.valueOf(toHex(total).charAt(2));
                    }
                    ram1.insert("A",val);
                }
                if(pointerInstruction.equals("ADC")){
                    String source = hexOrReg(opHash1.get(pointer));
                    String  op1=ram1.get(source);
                    int value1=toInt(op1);
                    int value2=toInt(ram1.get("A"));
                    int total=value1+value2+Integer.parseInt(ram1.get("carry"));
                    String val=toHex(total);
                    if(total>255){
                        ram1.insert("carry","1");
                        val= String.valueOf(toHex(total).charAt(1))+String.valueOf(toHex(total).charAt(2));
                    }
                    ram1.insert("A",val);
                }
                if(pointerInstruction.equals("ACI")){
                    String source = hexOrReg(opHash1.get(pointer));
                    String  op1=source;
                    int value1=toInt(op1);
                    int value2=toInt(ram1.get("A"));
                    int total=value1+value2+Integer.parseInt(ram1.get("carry"));
                    String val=toHex(total);
                    if(total>255){
                        ram1.insert("carry","1");
                        val= String.valueOf(toHex(total).charAt(1))+String.valueOf(toHex(total).charAt(2));
                    }
                    ram1.insert("A",val);
                }
                if(pointerInstruction.equals("ADI")){
                    String source = hexOrReg(opHash1.get(pointer));
                    int value1=toInt(source);
                    int value2=toInt(ram1.get("A"));
                    int total=value1+value2;
                    String val=toHex(total);
                    if(total>255){
                        ram1.insert("carry","1");
                        val= String.valueOf(toHex(total).charAt(1))+String.valueOf(toHex(total).charAt(2));
                    }
                    ram1.insert("A",val);
                }
                if(pointerInstruction.equals("SUB")){
                    String source = hexOrReg(opHash1.get(pointer));
                    String  op1=ram1.get(source);
                    int value1=toInt(op1);
                    int value2=toInt(ram1.get("A"));
                    if ((value2 - value1)<0) {
                        ram1.insert("carry","1");
                        ram1.insert("sign","1");
                    }

                    ram1.insert("A",toHex(value2-value1));
                }
                if(pointerInstruction.equals("SBB")){
                    String source = hexOrReg(opHash1.get(pointer));
                    String  op1=ram1.get(source);
                    int value1=toInt(op1);
                    int value2=toInt(ram1.get("A"));
                    int ope=value2-value1-toInt(ram1.get("carry"));
                    if (ope<0) {
                        ram1.insert("carry","1");
                        ram1.insert("sign","1");
                    }
                    ram1.insert("A",toHex(ope));
                }

                if(pointerInstruction.equals("SUI")){
                    String source = hexOrReg(opHash1.get(pointer));
                    int value1=toInt(source);
                    int value2=toInt(ram1.get("A"));
                    int ope=value2-value1;
                    if (ope<0) {
                        ram1.insert("carry","1");
                        ram1.insert("sign","1");
                    }
                    ram1.insert("A",toHex(value2-value1));
                }
                if(pointerInstruction.equals("SBI")){
                    String source = hexOrReg(opHash1.get(pointer));
                    int value1=toInt(source);
                    int value2=toInt(ram1.get("A"));
                    int ope=value2-value1-toInt(ram1.get("carry"));
                    if (ope<0) {
                        ram1.insert("carry","1");
                        ram1.insert("sign","1");
                    }
                    ram1.insert("A",toHex(ope));
                }
                if(pointerInstruction.equals("ANA")){
                    String source = hexOrReg(opHash1.get(pointer));
                    String op1=ram1.get(source);
                    String op2=ram1.get("A");
                    int val=toInt(op1);int val2=toInt(op2);
                    int and=val&val2;
                    ram1.insert("A",toHex(and));

                }
                if(pointerInstruction.equals("XRA")){
                    String source = hexOrReg(opHash1.get(pointer));
                    String op1=ram1.get(source);
                    String op2=ram1.get("A");
                    int val=toInt(op1);int val2=toInt(op2);
                    int and=(val^val2);
                    ram1.insert("A",toHex(and));

                }
                if(pointerInstruction.equals("ORA")){
                    String source = hexOrReg(opHash1.get(pointer));
                    String op1=ram1.get(source);
                    String op2=ram1.get("A");
                    int val=toInt(op1);int val2=toInt(op2);
                    int and=val|val2;
                    ram1.insert("A",toHex(and));

                }
                if(pointerInstruction.equals("ANI")){
                    String op1 = hexOrReg(opHash1.get(pointer));
                    String op2=ram1.get("A");
                    int val=toInt(op1);int val2=toInt(op2);
                    int and=val&val2;
                    ram1.insert("A",toHex(and));

                }
                if(pointerInstruction.equals("XRI")){
                    String op1=opHash1.get(pointer);
                    String op2=ram1.get("A");
                    int val=toInt(op1);int val2=toInt(op2);
                    int and=(val^val2);
                    ram1.insert("A",toHex(and));

                }


                if(pointerInstruction.equals("ORI")){
                    String op1 = hexOrReg(opHash1.get(pointer));
                    String op2=ram1.get("A");
                    int val=toInt(op1);int val2=toInt(op2);
                    int and=val|val2;
                    ram1.insert("A",toHex(and));

                }

                //Some register pair cliches

                if(pointerInstruction.equals("DAD")){
                    String op1 = hexOrReg(opHash1.get(pointer));
                    if(op1.equals("B")){
                        int val=toInt(ram1.get("H"))+toInt(ram1.get("B"));
                        ram1.insert("H",toHex(val));
                        int val1=toInt(ram1.get("L"))+toInt(ram1.get("C"));
                        ram1.insert("L",toHex(val1));
                    }
                    if(op1.equals("D")){
                        int val=toInt(ram1.get("H"))+toInt(ram1.get("D"));
                        ram1.insert("H",toHex(val));
                        int val1=toInt(ram1.get("L"))+toInt(ram1.get("E"));
                        ram1.insert("L",toHex(val1));
                    }

                }
                if(pointerInstruction.equals("INX")){
                    String op1 = hexOrReg(opHash1.get(pointer));
                    if(op1.equals("B")){
                        String current=ram1.get("B")+ram1.get("C");
                        int incremented=toInt(current)+1;
                        ram1.insert("B",String.valueOf(toHex(incremented).charAt(0))+String.valueOf(toHex(incremented).charAt(1)));
                        ram1.insert("C",String.valueOf(toHex(incremented).charAt(2))+String.valueOf(toHex(incremented).charAt(3)));

                    }
                    if(op1.equals("D")){
                        String current=ram1.get("D")+ram1.get("E");
                        int incremented=toInt(current)+1;
                        ram1.insert("D",String.valueOf(toHex(incremented).charAt(0))+String.valueOf(toHex(incremented).charAt(1)));
                        ram1.insert("E",String.valueOf(toHex(incremented).charAt(2))+String.valueOf(toHex(incremented).charAt(3)));
                    }
                    if(op1.equals("H")){
                        String current=ram1.get("H")+ram1.get("L");
                        int incremented=toInt(current)+1;
                        ram1.insert("H",String.valueOf(toHex(incremented).charAt(0))+String.valueOf(toHex(incremented).charAt(1)));
                        ram1.insert("L",String.valueOf(toHex(incremented).charAt(2))+String.valueOf(toHex(incremented).charAt(3)));
                    }

                }
                if(pointerInstruction.equals("DCX")){
                    String op1 = hexOrReg(opHash1.get(pointer));
                    if(op1.equals("B")){
                        String current=ram1.get("B")+ram1.get("C");
                        int incremented=toInt(current)-1;
                        ram1.insert("B",String.valueOf(toHex(incremented).charAt(0))+String.valueOf(toHex(incremented).charAt(1)));
                        ram1.insert("C",String.valueOf(toHex(incremented).charAt(2))+String.valueOf(toHex(incremented).charAt(3)));

                    }
                    if(op1.equals("D")){
                        String current=ram1.get("D")+ram1.get("E");
                        int incremented=toInt(current)-1;
                        ram1.insert("D",String.valueOf(toHex(incremented).charAt(0))+String.valueOf(toHex(incremented).charAt(1)));
                        ram1.insert("E",String.valueOf(toHex(incremented).charAt(2))+String.valueOf(toHex(incremented).charAt(3)));
                    }
                    if(op1.equals("H")){
                        String current=ram1.get("H")+ram1.get("L");
                        int incremented=toInt(current)-1;
                        ram1.insert("H",String.valueOf(toHex(incremented).charAt(0))+String.valueOf(toHex(incremented).charAt(1)));
                        ram1.insert("L",String.valueOf(toHex(incremented).charAt(2))+String.valueOf(toHex(incremented).charAt(3)));
                    }

                }

                //Jump instructions


                if(pointerInstruction.equals("JMP")){

                    String loop=opHash1.get(pointer);

                   String jumpIns= getKey(loop,laHash);
                    if(jumpIns.equals("")){
                      noSuchAloop(pointer);
                        break;
                    }
                    startingAddress= Integer.parseInt(jumpIns);
                    continue;


                }
                if(pointerInstruction.equals("JZ")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(ram1.get("zero").equals("1")){
                        startingAddress= Integer.parseInt(jumpIns);
                        continue;
                    }


                }
                if(pointerInstruction.equals("JNZ")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(!ram1.get("zero").equals("1")){
                        startingAddress= Integer.parseInt(jumpIns);
                        continue;
                    }


                }
                if(pointerInstruction.equals("JC")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(ram1.get("carry").equals("1")){
                        startingAddress= Integer.parseInt(jumpIns);
                        continue;
                    }


                }
                if(pointerInstruction.equals("JNC")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(!ram1.get("carry").equals("1")){
                        startingAddress= Integer.parseInt(jumpIns);
                        continue;
                    }


                }
                if(pointerInstruction.equals("JM")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(ram1.get("sign").equals("1")){
                        startingAddress= Integer.parseInt(jumpIns);
                        continue;
                    }


                }
                if(pointerInstruction.equals("JP")){

                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(!ram1.get("sign").equals("1")){
                        startingAddress= Integer.parseInt(jumpIns);
                        continue;
                    }


                }
                if(pointerInstruction.equals("JPE")){

                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(ram1.get("parity").equals("1")){
                        startingAddress= Integer.parseInt(jumpIns);
                        continue;
                    }


                }
                if(pointerInstruction.equals("JPO")){

                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(!ram1.get("parity").equals("1")){
                        startingAddress= Integer.parseInt(jumpIns);
                        continue;
                    }


                }
                //Comparison instructions

                if(pointerInstruction.equals("CMP")){
                    String op1 = hexOrReg(opHash1.get(pointer));
                    op1=ram1.get(op1);
                    String op2=ram1.get("A");
                    int val=toInt(op1);int val2=toInt(op2);
                    if(val>val2){ram1.insert("carry","1");ram1.insert("zero","0");}
                    if(val<val2){ram1.insert("carry","0");ram1.insert("zero","0");}
                    if(val==val2){ram1.insert("carry","0");ram1.insert("zero","1");}


                }
                if(pointerInstruction.equals("CPI")){
                    String op1=opHash1.get(pointer).replace("H","");
                    String op2=ram1.get("A");
                    int val=toInt(op1);int val2=toInt(op2);
                    if(val>val2){ram1.insert("carry","1");ram1.insert("zero","0");}
                    if(val<val2){ram1.insert("carry","0");ram1.insert("zero","0");}
                    if(val==val2){ram1.insert("carry","0");ram1.insert("zero","1");}


                }
                if(pointerInstruction.equals("STC")){
                    ram1.insert("carry","1");

                }
                if(pointerInstruction.equals("CMA")){
                   String  comp= onescomple(ram1.get("A"));

                    ram1.insert("A",comp);
                }
                if(pointerInstruction.equals("CMC")){
                    int carry= Integer.parseInt(ram1.get("A"));
                    if(carry==0){ram1.insert("A","1");}
                    else{ram1.insert("A","0");}

                }

                //CALL instructions

                if(pointerInstruction.equals("CALL")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    RetFromRoutine=String.valueOf(Integer.valueOf(pointer)+1);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }

                        startingAddress= Integer.parseInt(jumpIns);
                        continue;


                }
                if(pointerInstruction.equals("CC")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    RetFromRoutine=String.valueOf(Integer.valueOf(pointer)+1);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(ram1.get("carry").equals("1")) {
                    startingAddress= Integer.parseInt(jumpIns);
                    continue; }


                }
                if(pointerInstruction.equals("CNC")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    RetFromRoutine=String.valueOf(Integer.valueOf(pointer)+1);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(!ram1.get("carry").equals("1")) {
                        startingAddress= Integer.parseInt(jumpIns);
                        continue; }


                }
                if(pointerInstruction.equals("CZ")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    RetFromRoutine=String.valueOf(Integer.valueOf(pointer)+1);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(ram1.get("zero").equals("1")) {
                        startingAddress= Integer.parseInt(jumpIns);
                        continue; }


                }
                if(pointerInstruction.equals("CNZ")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    RetFromRoutine=String.valueOf(Integer.valueOf(pointer)+1);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(!ram1.get("zero").equals("1")) {
                        startingAddress= Integer.parseInt(jumpIns);
                        continue; }


                }
                if(pointerInstruction.equals("CM")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    RetFromRoutine=String.valueOf(Integer.valueOf(pointer)+1);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(ram1.get("sign").equals("1")) {
                        startingAddress= Integer.parseInt(jumpIns);
                        continue; }


                }
                if(pointerInstruction.equals("CP")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    RetFromRoutine=String.valueOf(Integer.valueOf(pointer)+1);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(!ram1.get("sign").equals("1")) {
                        startingAddress= Integer.parseInt(jumpIns);
                        continue; }


                }
                if(pointerInstruction.equals("CPE")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    RetFromRoutine=String.valueOf(Integer.valueOf(pointer)+1);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(ram1.get("parity").equals("1")) {
                        startingAddress= Integer.parseInt(jumpIns);
                        continue; }


                }
                if(pointerInstruction.equals("CPO")){
                    String loop=opHash1.get(pointer);
                    String jumpIns= getKey(loop,laHash);
                    RetFromRoutine=String.valueOf(Integer.valueOf(pointer)+1);
                    if(jumpIns.equals("")){
                        noSuchAloop(pointer);
                        break;
                    }
                    if(!ram1.get("parity").equals("1")) {
                        startingAddress= Integer.parseInt(jumpIns);
                        continue; }


                }

                //RETURN instructions

                if(pointerInstruction.equals("RET")){
                    startingAddress=Integer.valueOf(RetFromRoutine);
                    continue;
                }
                if(pointerInstruction.equals("RC")){
                    if(ram1.get("carry").equals("1")){
                    startingAddress=Integer.valueOf(RetFromRoutine);
                    continue; }
                }
                if(pointerInstruction.equals("RNC")){
                    if(!ram1.get("carry").equals("1")){
                        startingAddress=Integer.valueOf(RetFromRoutine);
                        continue; }
                }
                if(pointerInstruction.equals("RM")){
                    if(ram1.get("sign").equals("1")){
                        startingAddress=Integer.valueOf(RetFromRoutine);
                        continue; }
                }
                if(pointerInstruction.equals("RP")){
                    if(!ram1.get("sign").equals("1")){
                        startingAddress=Integer.valueOf(RetFromRoutine);
                        continue; }
                }
                if(pointerInstruction.equals("RZ")){
                    if(ram1.get("zero").equals("1")){
                        startingAddress=Integer.valueOf(RetFromRoutine);
                        continue; }
                }
                if(pointerInstruction.equals("RNZ")){
                    if(!ram1.get("zero").equals("1")){
                        startingAddress=Integer.valueOf(RetFromRoutine);
                        continue; }
                }
                if(pointerInstruction.equals("RPE")){
                    if(ram1.get("parity").equals("1")){
                        startingAddress=Integer.valueOf(RetFromRoutine);
                        continue; }
                }
                if(pointerInstruction.equals("RPO")){
                    if(!ram1.get("parity").equals("1")){
                        startingAddress=Integer.valueOf(RetFromRoutine);
                        continue; }
                }


                //Updating  Conditional Cliches

                if(ram1.get("A").equals("00")|ram1.get("A").equals("0")|ram1.get("A").equals("000")|ram1.get("A").equals("0000")){ram1.insert("zero","1");}else{ram1.insert("zero","0");}
                if(checkParity(ram1.get("A"))){ram1.insert("parity","1");}else{ram1.insert("parity","0");}

                //Updating memory content values

                if(true){ram1.insert("M",ram1.get(ram1.get("H")+ram1.get("L")));}


                //stack operations
                if(pointerInstruction.equals("PUSH")){
                    String op1 = hexOrReg(opHash1.get(pointer));
                    String newPos=toHex(toInt(ram1.get("SP"))-2);
                    if(op1.equals("B")){
                       String loc=toHex(toInt(ram1.get("SP"))-1);
                       ram1.insert(loc,ram1.get("B"));
                       String loc2=toHex(toInt(ram1.get("SP"))-2);
                       ram1.insert(loc2,ram1.get("C"));
                       ram1.insert("SP",newPos);

                    }
                    if(op1.equals("D")){
                        String loc=toHex(toInt(ram1.get("SP"))-1);
                        ram1.insert(loc,ram1.get("D"));
                        String loc2=toHex(toInt(ram1.get("SP"))-2);
                        ram1.insert(loc2,ram1.get("E"));
                        ram1.insert("SP",newPos);
                        }
                    if(op1.equals("H")){
                        String loc=toHex(toInt(ram1.get("SP"))-1);
                        ram1.insert(loc,ram1.get("H"));
                        String loc2=toHex(toInt(ram1.get("SP"))-2);
                        ram1.insert(loc2,ram1.get("L"));
                        ram1.insert("SP",newPos);
                    }

                }
                if(pointerInstruction.equals("POP")){
                    String op1 = hexOrReg(opHash1.get(pointer));
                    String newPos=toHex(toInt(ram1.get("SP"))+2);
                    if(op1.equals("B")){
                     String bot=toHex(toInt(ram1.get("SP")));
                     String top=toHex(toInt(ram1.get("SP"))+1);
                     ram1.insert("C",ram1.get(bot));
                     ram1.insert("B",ram1.get(top));
                    }
                    if(op1.equals("D")){
                        String bot=toHex(toInt(ram1.get("SP")));
                        String top=toHex(toInt(ram1.get("SP"))+1);
                        ram1.insert("E",ram1.get(bot));
                        ram1.insert("D",ram1.get(top));
                    }
                    if(op1.equals("H")){
                        String bot=toHex(toInt(ram1.get("SP")));
                        String top=toHex(toInt(ram1.get("SP"))+1);
                        ram1.insert("L",ram1.get(bot));
                        ram1.insert("H",ram1.get(top));
                    }

                }


                if(pointerInstruction.equals("XTHL")){
                  String h=ram1.get("H");String l=ram1.get("L");
                  String top=ram1.get("SP");String bot=toHex(toInt(ram1.get("SP"))-1);
                  ram1.insert("H",ram1.get(top)); ram1.insert("L",ram1.get(bot));
                  ram1.insert(top,h);ram1.insert(bot,l);


                }
                if(pointerInstruction.equals("SPHL")){
                   String hl=ram1.get("H")+ram1.get("L");
                   ram1.insert("SP",hl);

                }


                startingAddress++;

            }



       } catch (Exception e) {
          // Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    //Some additional tools

    public String getKey(String val,HashMap<String, String> label){
        String key="";
       for(Map.Entry<String,String> entry:label.entrySet()){
           if(entry.getValue().equals(val)){
               key=entry.getKey();
           }
       }
       return key;
    }


    //checking the parity

    private boolean checkParity(String a) {
        boolean parity=false;
        int acc=toInt(a);
        while(acc!=0){
            parity=!parity;
             acc=acc&(acc-1);
        }
        return parity;
    }

    //Display toast message when it wants 16 bit

    private void SixteenbitErr(String point) {
        Toast.makeText(getApplicationContext(),"Error occured in line "+point+" please put 4bit hex value",Toast.LENGTH_LONG).show();

    }

    // Display toast message when can not find the mneumonic label name

    private void noSuchAloop(String point) {
        Toast.makeText(getApplicationContext(),"Error occured in line "+point+" cannot find the loop name",Toast.LENGTH_LONG).show();

    }

    // to find whether the operand is register or memory

    String hexOrReg(String s){
        String newStr = null;
        if(s.length()==1){
            newStr=s;

        }
        else{
            newStr=s.replace("H","");
        }
        return newStr.replace("#","");
    }

    // to find one's complement

    String onescomple(String s){
           int val=toInt(s);
           //int bits=(int)(Math.floor(Math.log(val))/Math.log(2))+1;
           int bits=8;
           int res=((1<<bits)-1)^val;
           return toHex(res);

    }

    //Display toast message when tries to put more than 8 bits in register or memory

    void EightBitErr(String point){

             Toast.makeText(getApplicationContext(),"Error occured in line "+point+" attempt to push more than 8bit",Toast.LENGTH_LONG).show();
         }


    //Convertion tools

    //hexadecimal to integer

    int toInt(String hex){
             String digits="0123456789ABCDEF";
             int val=0;
             for(int i=0;i<hex.length();i++){
                 char a=hex.charAt(i);
                 int di=digits.indexOf(a);
                 val=16*val+di;
             }
             return val;
         }

    //Integer to hexadecimal

    String toHex(int val){
              String value= Integer.toHexString(val).toUpperCase();
             String fourBitHex="";
             if(val>255){
             if(value.length()==1){fourBitHex= new String(new char[]{'0', '0', '0', value.charAt(0)});}
             if(value.length()==2){fourBitHex= new String(new char[]{'0', '0', value.charAt(0), value.charAt(1)});}
             if(value.length()==3){fourBitHex= new String(new char[]{'0', value.charAt(0), value.charAt(1), value.charAt(2)});}
             else{fourBitHex=value;}
             return fourBitHex; }
             else if(val<256){
                 if(value.length()==1){fourBitHex= new String(new char[]{'0', value.charAt(0)});}
                 if(value.length()==2){fourBitHex= new String(new char[]{ value.charAt(0), value.charAt(1)});}
                 else{fourBitHex=value;}
                 return fourBitHex; }
             else{return  value;}
         }


   //Abandoned tools while developement

    /*private boolean isLetters(String s){
        if(Pattern.matches("[a-zA-Z]",s)){
            return true;
        }
        else{return false;}

    }
    boolean isEightBit(String hex){
        int val=toInt(hex);
        if(val>256){return false;}
        else{return true;}
    }
    String getOpe(String op){
        if(op.charAt(0)=='#'){
            op=op.replace("#","").replace("H","");
            return ram1.get(op);
        }

        else{
            return op.replace("H","");
        }
    }
    int add(String hex,int op){
        int val=toInt(hex);
        return val+op;
    } */
    }

