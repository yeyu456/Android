package com.simplecalculator;

import android.app.Activity;
import android.os.Bundle;
import java.util.Stack;
import java.util.HashMap;
import java.util.ArrayList;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;



public class MainActivity extends Activity {
    HashMap<String, Integer> operator = new HashMap<String, Integer>();
    TextView outputView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        operator.put("+", 1);
        operator.put("-", 1);
        operator.put("*", 2);
        operator.put("%", 2);
        operator.put("/", 2);
        
        setContentView(R.layout.main_activity);
        outputView = (TextView) findViewById(R.id.output_view);
        
        //restore the status if exist
        if(savedInstanceState!=null){
            outputView.setText(savedInstanceState.getString("output"));
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        TextView outputView = (TextView) findViewById(R.id.output_view);
        outState.putString("output", outputView.getText().toString());
        //saving the screen view
    }
    
    public void onTableItemClicked(View v){
        Button b = (Button)v;
        String input = b.getText().toString();
        String textString = outputView.getText().toString();
        if(textString.equals("ERROR"))
            outputView.setText(null);
        switch(input){
            case "=" : {
                outputView.append(input);
                calculate(outputView);
                return;
            }
            case "Çå¿Õ" : {
                outputView.setText(null);
                return;
            }
            case "C" : {
                outputView.setText(textString
                                    .substring(0, textString.length()-1));
                return;
            }
            default: {
                outputView.append(input);
            }
        }
    }
        
    
    //use Shunting Yard Algorithm to get a expression
    private void calculate(TextView txv){
        CharSequence tmp = txv.getText();
        ArrayList<String> s = new ArrayList<String>();
        Stack<Character> o = new Stack<Character>();
        String tmpNum = "";
        String text = "";
        char tmpChar;
        for(int n=0;n<tmp.length();n++){
            tmpChar = tmp.charAt(n);
            if(Character.isDigit(tmpChar)||tmpChar=='.'){
                tmpNum += tmpChar;
            }
            else{
                if(!tmpNum.equals("")){
                    s.add(tmpNum);
                    tmpNum = "";
                    if(tmpChar=='=')
                        break;
                }
                if(o.isEmpty()||tmpChar=='(')
                    o.add(tmpChar);
                else{
                    if(tmpChar==')'){
                        if(!rightParenthese(tmpChar, s, o)){
                            txv.setText("ERROR");
                            return;
                        }
                    }
                    else{
                        while(operatorCompare(o.pop(), tmpChar, s, o)
                              &&(!o.isEmpty())){}
                        o.add(tmpChar);
                    }
                }
            }
        }
        while(!o.isEmpty()){
            s.add(o.pop().toString());
        }
        try{
            text = RPNCalculate(s);
        }catch(Exception e){
            text = "ERROR";
        }
        txv.setText(text);
    }
    
    //use Reverse Polish notation to get the result of expression
    private String RPNCalculate(ArrayList<String> f1) throws Exception{
        Stack<String> s = new Stack<String>();
        String tmp = "";
        String o1 = "";
        String o2 = "";
        while(!f1.isEmpty()){
            tmp = f1.remove(0);
            if(!operator.containsKey(tmp))
                s.add(tmp);
            else{
                o1 = s.pop();
                o2 = s.pop();
                s.add((compute(Double.parseDouble(o1), 
                               Double.parseDouble(o2), 
                               tmp))
                      .toString());
            }
        }
        return String.valueOf(
                (Math.round(Double.parseDouble(s.pop()) * 100) 
                 / 100));
        //let the result accurate to the second decimal place
    }

    private Double compute(double a, 
                           double b, 
                           String operator){
        switch(operator){
            case "+" :
                return b+a;
            case "-" :
                return b-a;
            case "*" :
                return b*a;
            case "/" :
                return b/a;
            case "%" :
                return b%a;
            default :
                return 0.0;
        }
    }
    
    private boolean operatorCompare(Character a, 
                                    Character b, 
                                    ArrayList<String> f1, 
                                    Stack<Character> f2){
        if(a.equals('(')
           ||b.equals('(')
           ||operator.get(b.toString())>
             operator.get(a.toString())){
            f2.add(a);
            return false;
        }
        else{
            f1.add(String.valueOf(a));
            return true;
        }
    }
    
    private boolean rightParenthese(char a, 
                                    ArrayList<String> f1, 
                                    Stack<Character> f2){
        String tmp;
        if(!f2.isEmpty())
            while(!f2.isEmpty()){
                tmp = f2.pop().toString();
                if(tmp.equals("("))
                    return true;
                else
                    f1.add(tmp);
            }
        return false;
    }
}
