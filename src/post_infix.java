import javax.swing.*;
import java.util.Stack;
public class post_infix {
    static int  curpos;     //下标，表示在后缀表达式中从哪个位置取出item，每次取出一个item，curpos应当更新
    static String item;         //每次从后缀表达式中取出数字或运算符保存在item中
    static String postfix;       //后缀表达式
    static Stack<Double> stack =new Stack<>();     //栈，中缀计算时用
    public static int getItem(String fix) throws MyException{
        item = "";
        int i=0,k = curpos, flag;          //flag 标记取出的是操作符还是数字 ，数字标记成0，否则1
        if (fix.charAt(k) == '.')
            flag = -1;
        else if (fix.charAt(k) == 'D') {      //D开头 或默认 为10进制
            k++;
            while ((fix.charAt(k) >= '0' && fix.charAt(k) <= '9') || fix.charAt(k) == '.'||
                    (fix.charAt(k) >= 'a' && fix.charAt(k) <= 'f')) {
                if((fix.charAt(k) >= 'a' && fix.charAt(k) <= 'f'))
                    throw new MyException(" 十进制数输入错误！！");
                item += fix.charAt(k++);
                if (k >= fix.length())
                    break;
            }
            flag = 0;                            //如果是数字，不是操作符，返回0;
        } else if (fix.charAt(k) == 'H') {           //H开头为16进制
            k++;
            while ((fix.charAt(k) >= '0' && fix.charAt(k) <= '9') || (fix.charAt(k) >= 'a' && fix.charAt(k) <=
                    'f') || fix.charAt(k) == '.') {
                if(fix.charAt(k) =='.')
                    throw new MyException(" 十六进制只支持整数运算！");
                item += fix.charAt(k++);
                if (k >= fix.length())
                    break;
            }
            int a = (int)Long.parseLong(item, 16);          //转为16进制
            item = String.valueOf(a);
            if(item.charAt(0)=='-') item=item.substring(1,item.length())+" $";
            flag = 0;
        } else if (fix.charAt(k) >= '0' && fix.charAt(k) <= '9') {
            while ((fix.charAt(k) >= '0' && fix.charAt(k) <= '9') || fix.charAt(k) == '.') {
                item += fix.charAt(k++);
                if (k >= fix.length())
                    break;
            }
            flag = 0;
        } else {                  //操作符
            item += fix.charAt(k++);
            flag = 1;
        }
        while (k < fix.length() && fix.charAt(k) == ' ')      //取出空格
            k++;

        curpos = k;        //更新下标
        return flag;
    }

    static int ICP(char c){             //栈外优先级
        if(c == '#')    return 0;
        if(c == '(')    return 16;
        if(c == '*' || c== '/') return 12;
        if(c == '+' || c=='-')  return 10;
        if(c == '~' || c== '`' || c=='$') return 14;       //$为取负符号，与-减号应该区分，应为-为双目操作符，取负为单目，优先级最高
        if(c == '>' || c== '<') return 8;                   //~为位运算取反，`为取补，三个均为单目操作符，
        if(c == '&') return 6; //按位与
        if(c == '^') return 4; //按位异或
        if(c == '|')  return 2; //按位或
        if(c == ')')  return 1;
        else return -1;
    }
    static int ISP(char c){
        if(c == '#')    return 0;
        if(c == '(')    return 1;
        if(c == '*' || c== '/') return 13;
        if(c == '+' || c=='-')  return 11;
        if(c == '~' || c== '`' ||c=='$') return 15;
        if(c == '<' || c == '>')  return 9;
        if(c == '&' ) return 7;
        if(c == '^')  return 5;
        if(c == '|')  return 3;
        if(c == ')')  return 16;
        else return -1;

    }

    static void InfixToPostfix(String infix) throws MyException{
        curpos = 0;
        postfix = "";
        Stack<Character> S = new Stack<>();
        int flag = -1;
        int k = 0, i;
        char ch,curop = 0;
        S.push('#');
        while(curpos < infix.length()) {
            flag = getItem(infix);

            if (flag == 1)
            {
                curop = item.charAt(0);
                if(curop == ')')
                {
                    do {
                        ch = S.pop();
                        if(ch == '#')
                            throw new MyException(" 括号不对称！！");
                        if(ch!= '(')
                        {
                            postfix+= ch;
                            postfix +=" ";
                        }
                    }while(ch!='(');
                }
                else{
                    ch = S.peek();
                    while(ICP(curop) <= ISP(ch))
                    {
                        S.pop();
                        postfix+=ch;
                        postfix +=" ";
                        ch = S.peek();
                    }
                    S.push(curop);
                }
            }
            else
            {
                postfix+=item;
                postfix+=' ';
            }
        }
        while(!S.empty())
        {
            ch = S.pop();
            if(ch!='#')
            {
                postfix += ch;
                postfix += " ";
            }
        }

    }
    static void DoOperator(char oper) throws MyException{
        double oper1 , oper2;
        if(stack.empty())
        {
            throw new MyException(" 存在多余操作符！！");
        }
        oper1 = stack.pop();
        int int_oper1 = (int)oper1;
        if(oper == '~'){
            stack.push((double)(~(int)oper1));
            return;
        }
        if(oper == '`')
        {
            stack.push((double)(~(int)oper1 +1));
            return ;
        }
        if( oper == '$')
        {
            stack.push(-oper1);
            return;
        }

        if(stack.empty())
            throw new MyException(" 存在多余操作符!!");
        oper2 = stack.pop();
        int int_oper2 = (int)oper2;
        if(oper == '+') {
            long r = (long)int_oper2 +(long)int_oper1;         //两个大正数相加可能溢出   溢出判断
            if((int)r != r)
                throw new MyException(" overflow!!");
            stack.push(oper1 + oper2);
            return;
        }
        else if(oper == '-'){
            long r = (long)int_oper2 - (long)int_oper1;      //负数-正数 可能溢出，
            if((int)r != r)
                throw new MyException(" overflow!!");
            stack.push(oper2 - oper1);
            return;
        }
        else if(oper == '*') {
            long r = (long)int_oper1 * (long)int_oper2;
            if ((int)r != r)
                throw new MyException(" overflow");
            stack.push(oper1 * oper2);
            return;
        }
        else if(oper =='/')
            if(Math.abs(oper1) < 1e-6)
                throw new MyException(" 0不能作为除数！");
            else {
                stack.push(oper2 / oper1);
                return;
            }
        else if(oper == '&'){
            stack.push((double)(((int)oper1 & (int)oper2)));
            return ;
        }
        else if(oper == '|'){
            stack .push((double)(((int)oper1 | (int)oper2)));
            return ;
        }
        else if(oper == '^'){
            stack .push((double)(((int)oper1 ^ (int)oper2)));
            return ;
        }
        else if(oper == '<')
        {
            if(int_oper1>0) {
                int lmove = int_oper2 << int_oper1;
                if (lmove > 0 && int_oper2 < 0 || lmove < 0 && int_oper2 > 0)         //移位前后符号不同则溢出。    此处溢出判断条件可能有问题
                    throw new MyException(" overflow!!");
                stack.push((double) (lmove));
                return;
            }
            else{
                int rmove = -int_oper1;
                stack.push((double)(int_oper2 >>> rmove));
            }
        }

    }
    static double Caculating(String post) throws MyException
    {
        while(stack.size()!=0)
            stack.pop();
        double data;
        int flag;
        curpos = 0;
        while(curpos<post.length()&&post.charAt(curpos) == ' ')
            curpos++;
        while(curpos < post.length())
        {

            flag = getItem(post);
            if(flag == -1)
                throw new MyException(" 输入语法错误！！");
            else if(flag == 1)
                DoOperator(item.charAt(0));
            else
            {
                data = Double.parseDouble(item);
                stack.push(data);
            }
        }
        if(stack.size() == 1)              //栈中只剩一个数时，即为答案，弹出返回
            return stack.pop();
        else{
            throw new MyException(" 存在多余操作数！");}
    }

    public static boolean isIntegerForDouble(double obj) {       //判断是否为整数
        double eps = 1e-10;  // 精度范围
        return obj-Math.floor(obj) < eps;
    }
    public static String final_compute(String s){
        try{

            InfixToPostfix(s);

            double b = Caculating(postfix);      //b 为十进制答案
            return "" + b;
        }catch(MyException e) {

            return e.getMessage();
        }
    }
    public static void main(String []args){
        JFrame jf1 = new JFrame("testcase");
        jf1.setLayout(null);
        jf1.setSize(500,500);
        Compute_panel haha = new Compute_panel();
        haha.setSize(jf1.getWidth(),jf1.getHeight() - 30);
        haha.setLocation(0,0);
        jf1.setResizable(false);//窗口大小不可更改
        jf1.setLocationRelativeTo(null);//将此窗口显示在屏幕中央
        jf1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//可关
        jf1.setVisible(true);//可见
        jf1.add(haha);//窗口添加实例化对象

    }
}
