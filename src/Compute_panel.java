import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Compute_panel extends JPanel {
    String client_display = "", sever_display = "";
    Font f1 = new Font("微软雅黑",Font.PLAIN,15);//格式一
    Font f2 = new Font("微软雅黑",Font.BOLD,15);//格式二
    private JTextField jtf1 = new JTextField();
    private JTextField jtf2 = new JTextField("DEC");
    private JButton[][] buttons = new JButton[8][5];
    GridBagLayout gbaglayout=new GridBagLayout();    //创建GridBagLayout布局管理器
    GridBagConstraints constraints=new GridBagConstraints();
    Map<String, String> map = new HashMap<String, String>(){
        {put("DEC","D");
            put("HEX","H");
        }
    };
    String[][] names = {
            {"DEC","HEX","OCT","OFF","ON/C"},
            {"STO","RCT","SUM","(",")"},
            {"SHF","D","E","F","K"},
            {"1'sC","A","B","C","÷"},
            {"OR","7","8","9","×"},
            {"AND","4","5","6","-"},
            {"XOR","1","2","3","+"},
            {"CE","0",".","＋/-","="}
    };
    String[][] maps_1 = {//显示映射
            {"","","","OFF","ON/C"},
            {"","","","(",")"},
            {"[SHF]","D","E","F",""},
            {"[1'sc]","A","B","C","÷"},
            {"[OR]","7","8","9","×"},
            {"[AND]","4","5","6","-"},
            {"[XOR]","1","2","3","+"},
            {"CE","0",".","[+/-]","="}
    };
    String[][] maps_2 = {//后台处理映射
            {"","","","OFF","ON/C"},
            {"","","","(",")"},
            {"<","d","e","f",""},
            {"~","a","b","c","/"},
            {"|","7","8","9","*"},
            {"&","4","5","6","-"},
            {"^","1","2","3","+"},
            {"CE","0",".","`","="}
    };
    String[][] maps_3 = {//进制状态映射
            {"DEC","HEX","OCT","OFF","ON/C"},
            {"","","","",""},
            {"","","","",""},
            {"","","","",""},
            {"","","","",""},
            {"","","","",""},
            {"","","","",""},
            {"CE","","","",""}
    };
    public void Showc(){
        jtf1.setFont(f2);
        if(client_display != null)
            jtf1.setText(client_display.toUpperCase(Locale.ROOT));
    }
    public void settle_client(int finalI,int finalJ){
        if(maps_1[finalI][finalJ] == "CE"||maps_1[finalI][finalJ] == "OFF" || maps_1[finalI][finalJ] == "ON/C"){
            client_display = "";
            Showc();
        }else if(maps_1[finalI][finalJ] == "="){
            client_display = sever_display.substring(1);
            if(client_display.charAt(client_display.length() - 1) == '$')
                client_display = '-' + client_display.substring(0,client_display.length() - 1);
            Showc();
        }else{
            if(client_display== "") client_display = maps_1[finalI][finalJ];
            else client_display += maps_1[finalI][finalJ];
            Showc();
        }
    }
    public void settle_sever(int finalI,int finalJ){
        if(maps_2[finalI][finalJ] == "CE" || maps_2[finalI][finalJ] == "OFF" || maps_2[finalI][finalJ] == "ON/C"){
            sever_display = "";
        }else if(maps_2[finalI][finalJ] == "="){
            sever_display = post_infix.final_compute(sever_display);//十进制的结果
            double ans = 0;
            try{
                ans = Double.parseDouble(sever_display);
            }catch (NumberFormatException exception){
                return;
            }
            if(post_infix.isIntegerForDouble(ans)){
                int res =(int)ans;
                sever_display = ""+ res;
                if(jtf2.getText().charAt(0)=='H')
                    sever_display = Integer.toHexString(res);
            }
            if(sever_display.charAt(0) == '-') {
                sever_display = sever_display.substring(1) + "$";
            }
            sever_display = jtf2.getText().charAt(0) + sever_display;
        }else{
            if(maps_2[finalI][finalJ] != ""){
                char t = maps_2[finalI][finalJ].charAt(0);
                if((t >= '0' && t <= '9')||(t >= 'a' && t <= 'f')){
                    String temp_1 = sever_display;
                    String temp_2 = jtf2.getText();
                    String op = map.get(temp_2);
                    if(op == null) op = "";
                    if(temp_1 == "") sever_display += op;//如果是空，同样也算第一个字符，加上进制信息
                    else{//不空的话需要判断前一个字符是除"."外的非数字字符，加上进制信息
                        char idx = temp_1.charAt(temp_1.length() - 1);
                        if( !(idx >= '0' && idx <= '9') && !(idx >= 'a' && idx <= 'f')) {
                            if(idx != '.') sever_display += op;
                        }
                    }
                }
                sever_display += t;
            }
        }
    }
    public void settle_lable(int finalI,int finalJ){
        jtf2.setFont(f1);
        String temp = jtf2.getText();
        String t = maps_3[finalI][finalJ];
        if(t != ""){
            if((t == "CE" || t == "ON/C" || t == "OFF") && t != temp){
                jtf2.setText("DEC");
            }else {
                jtf2.setText(t);
            }
        }
    }
    public Compute_panel() {

        this.setLayout(gbaglayout);    //使用GridBagLayout布局管理器
        constraints.fill=GridBagConstraints.BOTH;    //组件填充显示区域
        constraints.weightx=1.0;    //恢复默认值
        constraints.weighty=1.0;
        constraints.gridwidth = GridBagConstraints.REMAINDER;    //结束行
        gbaglayout.setConstraints(jtf1, constraints);
        gbaglayout.setConstraints(jtf2, constraints);
        this.add(jtf1);
        this.add(jtf2);
        for(int i = 0; i < 8; i ++){
            constraints.gridwidth=1;
            for(int j = 0; j < 5; j ++){
                buttons[i][j] = new JButton();
                buttons[i][j].setText(names[i][j]);
                if(j == 4)  constraints.gridwidth=GridBagConstraints.REMAINDER;
                gbaglayout.setConstraints(buttons[i][j], constraints);
                this.add(buttons[i][j]);
                int finalI = i;
                int finalJ = j;
                buttons[i][j].addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        settle_sever(finalI,finalJ);
                        settle_client(finalI,finalJ);
                        settle_lable(finalI,finalJ);
                    }
                });
            }
        }
    }
}
