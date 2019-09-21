import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class AnalizadorLexico {
    private static final int EOF = '!';
    private static final String EOF_error = "Error lexico: fin de fichero inesperado";
    private boolean eof = false;
    private static final ArrayList<Integer> final_states = new ArrayList<Integer>(
            Arrays.asList(2,3,7,8,5,6,9,11,12,32,19,21,24,25,27,29,32));
    private static final ArrayList<String> reserved_words = new ArrayList<String>(
            Arrays.asList("program","var","endvar","integer","real","array","of","pointer","begin","end","write"));
    private static final ArrayList<String> opmul_words = new ArrayList<String>(
            Arrays.asList("div","mod"));

    private static  RandomAccessFile file;
    private int state=1,row=1,col=1;
    private String lexema = "";

    public AnalizadorLexico(RandomAccessFile entrada) {
        file  = entrada;
    }

    public Token siguienteToken() {
        lexema = "";
        state = 1; //Where we start
        Token token = new Token();
        while (true) {
            if(state==1){ //Starting col/row of the token
                token.columna = col;
                token.fila = row;
            }
            char c = readChar(); //Read next character.
            lexema+=c;
            state = delta(state, c); //Next node

            if(checkFinal(state)){
                if(state==19){
                    token.tipo = checkReserved(lexema);
                }
                else {
                    token.tipo = setType();
                }
                token.lexema = lexema;
                if(c=='\n') row--; //dont like this
                break;
            }
        if(eof) {
            token.lexema = "";
            token.tipo = Token.EOF;
            return token;
        }
    }
        return token;
    }
    public char readChar(){
        if(!eof) {
            char c;
            try {
                c = (char) file.readByte();
                this.col++;
                if (c == '\n') {
                    row++;
                    if(state!=22){
                        col = 1;
                    }
                }
                return c;
            } catch (EOFException e) {
                eof = true;
                return EOF; //static class
            } catch (IOException ex) {
                System.exit(-1);
            }
        }
        return ' ';
    }

    public int delta (int state, int c) {
        final boolean b = c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9';
        
        switch (state) {
            case 1: //START
                if(c==' ' || c=='\t' || c=='\n'){
                    lexema = lexema.substring(0,lexema.length()-1);
                    return 1;
                }
                else if (c==')') return 2; //PARD
                else if(c==',') return 3; //COMA
                else if(c==':') return 4; //STATE 1 OF DOSP
                else if(c=='[') return 5; //CORI
                else if(c==']') return 6; //CORD
                else if(c=='=') return 8; //ASIG
                else if(c==';') { return 9;} //PYC
                else if(c=='.') {
                    try {
                        col++;
                        if(file.readByte()=='.'){
                            goBack(1,false);
                            return 10;
                        }
                        else{
                            goBack(2,false);
                            errorChar(c);
                        }
                    } catch (IOException e) {
                        //e.printStackTrace();
                    }
                }
                else if(c=='-' || c=='+') return 12; //OPAS
                else if(c=='*' || c=='/') return 32; //OPMUL
                else if(c=='D'|| c=='d') return 14; //STATE 1 OF OPMUL(DIV)
                else if(c=='M' || c=='m') return 16; //STATE 1 OF OPMUL(MUL)
                else if(c>='a' && c<='z' || c>='A' && c<='Z') return 18; //STATE 1 OF ID
                else if(c>='0' && c<='9') return 20; //STATE 1 OF NUMENTERO/NUMREAL
                else if(c=='(') return 28;
                else if(c==EOF){
                    eof = true;
                    return -1;
                }
                else{
                    col--;
                    errorChar(c);
            }
            case 4:
                if(c=='=') return 8;
                else return 7;
            case 10:
                if(c=='.') return 11;
                else{
                    errorChar(c);
                }
            case 13:
                if(b) return 18; //Its an id
                else {
                    System.out.println("hey");
                    goBack(1,true);
                    return 32; //Opmul
                }

            case 14:
                if(c=='I' || c=='i') return 15;
            case 15:
                if(c=='V' || c=='v') return 13; //OPMUL
            case 16:
                if(c=='O' || c == 'o') return 17;
            case 17:
                if(c=='D' || c=='d') return 13; //OPMUL
            case 18:
                if(b) return 18;
                else return 19; //ID
            case 20:
                if(c>='0' && c<='9') return 20;
                else if(c=='.') return 22;
                else return 21; //NUMENTERO
            case 22:
                if(c>='0' && c<='9') return 23;
                else return 25; //NUMENTERO
            case 23:
                if(c>='0' && c<='9') return 23;
                else return 24; //NUMREAL
            case 28:
                if(c=='*') return 30;//DO WE HAVE A COMMENT?
                else return 29; //PARI
            case 30://DO WE HAVE A COMMENT?
                if(c=='*') return 31;
                else if (c==EOF) {
                    errorEOF();
                }
                else return 30; //LOOP
            case 31://DO WE HAVE A COMMENT?
                if(c=='*') return 31;
                if(c==')'){
                    lexema=""; //Clean 
                    return 1;
                }
                else if(c==EOF){
                    errorEOF();
                }
                else return 30;
            default:
                return -1;
        }

    }
    public int setType(){
        switch(state){
            case 2:
                return Token.PARD;
            case 3:
                return Token.COMA;
            case 5:
                return Token.CORI;
            case 6:
                return Token.CORD;
            case 7:
                return Token.DOSP;
            case 8:
                return Token.ASIG;
            case 9:
                return Token.PYC;
            case 11:
                return Token.PTOPTO;
            case 12:
                return Token.OPAS;
            case 32:
                return Token.OPMUL;
            case 19:
                return Token.ID;
            case 21:
                return Token.NUMENTERO;
            case 24:
                return Token.NUMREAL;
            case 25:
                return Token.NUMENTERO;
            case 29:
                return Token.PARI;
            default:
                break;
        }
        return -1;
    }
    public boolean checkFinal(int state){
        switch(state){
            case 7:  case 19: case 21: case 24: case 27: case 29: //FINAL STATES WITH *
                return goBack(1,true);

            case 25: //FINAL STATE OF **NUMENTERO
                return goBack(2,true);

            default:
                return final_states.contains(state);
        }
    }
    public int checkReserved(String lexema){
        if(reserved_words.contains(lexema.toLowerCase())){
            return (reserved_words.indexOf(lexema.toLowerCase())+1)+10;
        }
        else if(opmul_words.contains(lexema.toLowerCase())){
            return Token.OPMUL;
        }
        else return Token.ID;
    }
    public boolean goBack(int n, boolean delete_char){
        try {
            file.seek(file.getFilePointer()-n);
            if(col>1) col = col - n; //Check
            if(delete_char) lexema = lexema.substring(0,lexema.length()-n); //delete useless chars
            return true;
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return false;
    }
    public void errorChar(int c){
        System.err.println("Error lexico("+row+","+col+"): caracter '"+(char)c+"' incorrecto");
        System.exit(-1);
    }
    public void errorEOF(){
        System.err.println(EOF_error);
        System.exit(-1);
    }
}