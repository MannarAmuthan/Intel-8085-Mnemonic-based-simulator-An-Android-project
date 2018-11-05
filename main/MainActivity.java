package com.amuthan.bt.a8085;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    Button set, execute,delete,edit;
    EditText  op1, op2, label, line, lineOpe;
    AutoCompleteTextView inst;
    ListView list;
    Instructions insOb;
    HashMap<String,String> opHash1,opHash2, laHash,insList, lineList;
    ArrayList<String> AddList;
    ArrayList<Integer>addressesInAsc;
    String startingAddress="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        set = (Button) findViewById(R.id.set);
        execute = (Button) findViewById(R.id.execute);
        delete = (Button) findViewById(R.id.dltLine);
        edit=(Button) findViewById(R.id.edtLine);
        inst = (AutoCompleteTextView) findViewById(R.id.instr);
        op1 = (EditText) findViewById(R.id.op1);
        op2 = (EditText) findViewById(R.id.op2);
        label = (EditText) findViewById(R.id.la);
        lineOpe = (EditText) findViewById(R.id.line);
        line = (EditText) findViewById(R.id.lineno);
        list = (ListView) findViewById(R.id.programlist);
        op1.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        op2.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        label.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        final Ram ram = new Ram(getApplicationContext());
        Fetching f=new Fetching();

        //Default stack point setting, for avoiding errors
         String stackpointer=ram.get("SP");
         if(f.toInt(stackpointer)==00){
            ram.insert("SP","0000");
         }

        insOb=new Instructions();
        opHash1= new HashMap<>();
        opHash2= new HashMap<>();
        laHash = new HashMap<>();
        insList=new HashMap<>();
        AddList= new ArrayList<>();
        addressesInAsc= new ArrayList<>();
        lineList =new HashMap<>();
        ArrayAdapter<String>adapterIns=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,insOb.instructions);
        inst.setAdapter(adapterIns);



        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(line.getText().toString().length()!=0&inst.getText().toString().length()!=0){
                Trimming();
                //ChangeAllCaps();
                ParseValues(line.getText().toString());
                op1.setText("");
                op2.setText("");
                label.setText("");
                 }
                else{
                    Toast.makeText(getApplicationContext(),"please give an instruction or line num",Toast.LENGTH_SHORT).show();
                }

         delete.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 try {
                     String lineNo=lineOpe.getText().toString();
                     insList.remove(lineNo);
                     opHash1.remove(lineNo);
                     opHash2.remove(lineNo);
                     laHash.remove(lineNo);
                     lineList.remove(lineNo);
                     UpdateList();
                 }
                 catch (Exception e){}


             }
         });
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(lineOpe.getText().toString().length()!=0){
                        String lineNo=lineOpe.getText().toString();
                        insList.remove(lineNo);
                        opHash1.remove(lineNo);
                        opHash2.remove(lineNo);
                        laHash.remove(lineNo);
                        line.setText(lineNo);
                        lineList.remove(lineNo);
                        UpdateList();}
                } catch (Exception e){}

            }
        });
        execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(insList.size()!=0){
                Intent intent=new Intent(MainActivity.this,Fetching.class);
                intent.putExtra("addressmap", lineList);
                intent.putExtra("insmap",insList);
                intent.putExtra("opsmap1",opHash1);
                intent.putExtra("opsmap2",opHash2);
                intent.putExtra("opsmap3", laHash);
                intent.putExtra("pointer",startingAddress);
                startActivity(intent);}
                else{
                    Toast.makeText(getApplicationContext(),"ins list is empty",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void Trimming() {
        if(line.getText().toString().length()!=0){
            line.setText(line.getText().toString().trim());}
        if(inst.getText().toString().length()!=0){inst.setText(inst.getText().toString().trim());}
        if(op1.getText().toString().length()!=0){op1.setText(op1.getText().toString().trim());}
        if(op2.getText().toString().length()!=0){op2.setText(op2.getText().toString().trim());}
        if(label.getText().toString().length()!=0){
            label.setText(label.getText().toString().trim());}
    }


    private void ParseValues(String keyn) {
        int NumberOfOP=insOb.getProp(inst.getText().toString());
        switch (NumberOfOP) {
            case 1:
                if(hasVal(op1)){
                lineList.put(keyn, line.getText().toString());
                insList.put(keyn,inst.getText().toString());
                opHash1.put(keyn,op1.getText().toString());
                 if(hasVal(label)){
                        laHash.put(keyn,label.getText().toString());
                 }
                int pointerAdr = Integer.parseInt(line.getText().toString()) + 1;
                line.setText(String.valueOf(pointerAdr));
                inst.setText("");
                UpdateList();
                }
                else{Toast.makeText(getApplicationContext(),"Please fill the required operants",Toast.LENGTH_SHORT).show();}
                break;
            case 2:
                if(hasVal(op1)&hasVal(op2)){
                lineList.put(keyn, line.getText().toString());
                insList.put(keyn,inst.getText().toString());
                opHash1.put(keyn,op1.getText().toString());
                opHash2.put(keyn,op2.getText().toString());
                    if(hasVal(label)){
                        laHash.put(keyn,label.getText().toString());
                    }
                int pointerAdr = Integer.parseInt(line.getText().toString()) + 1;
                line.setText(String.valueOf(pointerAdr));
                inst.setText("");
                UpdateList(); }
                else{Toast.makeText(getApplicationContext(),"Please fill the required operants",Toast.LENGTH_SHORT).show();}
                break;
            case 0:
                lineList.put(keyn, line.getText().toString());
                insList.put(keyn,inst.getText().toString());
                if(hasVal(label)){
                    laHash.put(keyn,label.getText().toString());
                }
                int pointerAdr = Integer.parseInt(line.getText().toString()) + 1;
                line.setText(String.valueOf(pointerAdr));
                inst.setText("");
                UpdateList();
                break;
        }

    }
    boolean hasVal(EditText e){
        if(e.getText().toString().length()==0){
            return false;
        }
        else{return true;}
    }


    private void UpdateList() {
        addressesInAsc.clear();
        AddList.clear();

        for(String key: lineList.keySet()) {
             //AddList.add(index, lineList.get(key).toString() + "  " + insList.get(key).toString() + " " + opHash.get(key).toString());
            addressesInAsc.add(Integer.valueOf(key));
        }
        Collections.sort(addressesInAsc,Collections.reverseOrder());
        startingAddress= String.valueOf(addressesInAsc.get(addressesInAsc.size()-1));

        for(int i=addressesInAsc.size()-1;i>-1;i--){
            String address= lineList.get(String.valueOf(addressesInAsc.get(i)));
            String ins=insList.get(String.valueOf(addressesInAsc.get(i)));
            String ops1=opHash1.get(String.valueOf(addressesInAsc.get(i)));
            String ops2=opHash2.get(String.valueOf(addressesInAsc.get(i)));
            String Label = laHash.get(String.valueOf(addressesInAsc.get(i)));

            if(ops1==null&ops2==null& Label ==null){AddList.add(address+" "+ins);}
            else if(ins==null&ops1==null&ops2==null& Label ==null){AddList.add(address+"need to edit");}
            else if(ops1!=null&ops2==null& Label ==null){AddList.add(address+" "+ins+" "+ops1);}
            else if(ops1!=null&ops2!=null& Label ==null){AddList.add(address+" "+ins+" "+ops1+" "+ops2);}
            else if(ops1!=null&ops2==null& Label !=null){AddList.add(address+" "+ins+" "+ops1+" "+ Label);}
            else if(ops1!=null&ops2!=null& Label !=null){AddList.add(address+" "+ins+" "+ops1+" "+ops2+" "+ Label);}
            else{ }

        }
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, AddList);
        list.setAdapter(adapter);

    }


}

