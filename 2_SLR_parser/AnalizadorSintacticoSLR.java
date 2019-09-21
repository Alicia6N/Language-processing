import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class AnalizadorSintacticoSLR {
    private AnalizadorLexico al;
    private Stack<Integer> stack;
    private String[][] acciones;
    private int[][] go;
    private ArrayList<Integer> parte_derecha;
    private ArrayList<Integer> parte_izq;
    Token nextToken;
    public AnalizadorSintacticoSLR(AnalizadorLexico al) {
        this.al = al;
        parte_derecha = new ArrayList<>(Arrays.asList(1,4,3,2,1,4,1,1,4,3,1,3,4,1,3,1,1,1,1));
        parte_izq = new ArrayList<>(Arrays.asList(0,1,2,3,3,4,5,5,6,7,7,8,8,8,9,9,10,10,10));
        stack = new Stack<>();
        createTable();
    }
    public void analizar(){
        Stack<Integer> rules = new Stack<>();
        stack.push(0);
        nextToken = al.siguienteToken();
        while(true){
            int s = stack.peek();
            if(acciones[s][nextToken.tipo]==null){
                errorSintaxis(s);
            }
            String nextAction = acciones[s][nextToken.tipo];
            if(nextAction == null || nextAction.isEmpty()) { errorSintaxis(s);}
            String[] words=nextAction.split(",");
            String type = words[0];
            int number = Integer.parseInt(words[1]);
            if (type.equals("D")) {
                stack.push(number);
                nextToken = al.siguienteToken();
            }
            else if (type.equals("R")){
                for(int i=1;i<=parte_derecha.get(number);i++){
                    stack.pop();
                }

                int p = stack.peek();
                int A = parte_izq.get(number);
                rules.push(number); //checkpoint
                stack.push(go[p][A]);
            }
            else if(type.equals("A")){
                break; //fin
            }
            else {
                errorSintaxis(s);
            }
        }
        printRules(rules);
    }
    public void errorSintaxis(int state){
        if(nextToken.tipo == Token.EOF){
            System.err.print("Error sintactico: encontrado fin de fichero, esperaba ");
        }
        else {
            System.err.print("Error sintactico ("+ nextToken.fila+","+ nextToken.columna
                    + "): encontrado  '"+nextToken.lexema+"', esperaba ");
        }
        printToken(state);
        System.err.println();
        System.exit(-1);
    }
    public void printToken(int state){
        Token tipo = new Token();
        for(int i=0;i<acciones[0].length;i++){
            if(acciones[state][i]!=null){
                tipo.tipo = i;
                System.err.print(tipo.toString());
                System.err.print(" ");
            }
        }
    }
    public void printRules(Stack<Integer> solution){
        while(!solution.empty()){
            System.out.print(solution.pop());
            System.out.print(" ");
        }
    }
    public void createTable(){
        acciones = new String[37+1][17+1];
        go = new int[37+1][10+1];
        go[0][1] = 1;
        go[4][6] = 5;
        go[6][2] = 18;
        go[7][3] = 9;
        go[7][4] = 8;
        go[9][4] = 11;
        go[13][5] = 16;
        go[18][6] = 20;
        go[18][7] = 21;
        go[18][8] = 19;
        go[23][6] = 20;
        go[23][8] = 24;
        go[26][9] = 27;
        go[26][10] = 33;
        go[28][10] = 29;
        go[35][9] = 36;
        go[35][10] = 33;
        acciones[1][17] = "A,0";
        acciones[5][17] = "R,1";
        acciones[22][17] = "R,8";
        acciones[34][0] = "D,35";

        acciones[29][1] = "R,14";
        acciones[30][1] = "R,16";
        acciones[31][1] = "R,17";
        acciones[32][1] = "R,18";
        acciones[33][1] = "R,15";
        acciones[36][1] = "D,37";

        acciones[12][2] = "D,13";
        acciones[25][3] = "D,26";

        acciones[3][4] = "D,4";

        acciones[14][4] = "R,6";
        acciones[15][4] = "R,7";
        acciones[16][4] = "D,17";
        acciones[19][4] = "R,10";
        acciones[20][4] = "R,13";
        acciones[21][4] = "D,23";
        acciones[22][4] = "R,8";
        acciones[24][4] = "R,9";

        acciones[27][4] = "R,11";
        acciones[29][4] = "R,14";
        acciones[30][4] = "R,16";
        acciones[31][4] = "R,17";
        acciones[32][4] = "R,18";
        acciones[33][4] = "R,15";

        acciones[27][5] = "D,28";
        acciones[29][5] = "R,14";
        acciones[30][5] = "R,16";
        acciones[31][5] = "R,17";
        acciones[32][5] = "R,18";
        acciones[33][5] = "R,15";
        acciones[36][5] = "D,28";
        acciones[0][6] = "D,2";
        acciones[6][7] = "D,7";
        acciones[8][8] = "R,4";
        acciones[9][8] = "D,10";
        acciones[11][8] = "R,3";
        acciones[17][8] = "R,5";
        acciones[13][9] = "D,14";
        acciones[13][10] = "D,15";
        acciones[4][11] = "D,6";
        acciones[10][11] = "R,2";
        acciones[18][11] = "D,6";
        acciones[23][11] = "D,6";
        acciones[19][12] = "R,10";
        acciones[20][12] = "R,13";
        acciones[21][12] = "D,22";
        acciones[22][12] = "R,8";
        acciones[24][12] = "R,9";
        acciones[27][12] = "R,11";
        acciones[29][12] = "R,14";
        acciones[30][12] = "R,16";
        acciones[31][12] = "R,17";
        acciones[32][12] = "R,18";
        acciones[33][12] = "R,15";
        acciones[37][12] = "R,12";
        acciones[10][13] = "R,2";
        acciones[18][13] = "D,34";
        acciones[23][13] = "D,34";
        acciones[2][14] = "D,3";
        acciones[7][14] = "D,12";
        acciones[8][14] = "R,4";
        acciones[9][14] = "D,12";
        acciones[10][14] = "R,2";
        acciones[11][14] = "R,3";
        acciones[17][14] = "R,5";
        acciones[18][14] = "D,25";
        acciones[23][14] = "D,25";
        acciones[26][14] = "D,32";
        acciones[28][14] = "D,32";
        acciones[35][14] = "D,32";
        acciones[37][4] = "R,12";
        acciones[26][15] = "D,30";
        acciones[28][15] = "D,30";
        acciones[35][15] = "D,30";
        acciones[26][16] = "D,31";
        acciones[28][16] = "D,31";
        acciones[35][16] = "D,31";


    }
}
