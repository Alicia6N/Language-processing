import javafx.util.Pair;

import java.util.ArrayList;

public class TraductorDR {
    private class Obj {
        public int tipo=-1;
        public String trad;
        Obj(int tipo,String trad){
            this.tipo = tipo;
            this.trad = trad;
        }
    }
    private Token token;
    private AnalizadorLexico al;
    private StringBuilder rules;
    private boolean show_rules = false;
    private TablaSimbolos ts;
    private static int SCOPE = -1;
    private final int ERRYADECL=1,ERRNOSIMPLE=2,ERRNODECL=3,ERRTIPOS=4,ERRNOENTEROIZQ=5,ERRNOENTERODER=6,ERRRANGO=7;
    TraductorDR(AnalizadorLexico al){
        this.al = al;
        rules = new StringBuilder();
        show_rules = false;

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///             BASIC METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void anyadir(int rule){
        rules.append(" ");
        rules.append(rule);
    }
    public void errorSintaxis(int... tokens){
        if(token.tipo == Token.EOF){
            System.err.print("Error sintactico: encontrado fin de fichero, esperaba ");
        }
        else {
            System.err.print("Error sintactico ("+ token.fila+","+ token.columna
                    + "): encontrado  '"+token.lexema+"', esperaba ");
        }
        printTokens(tokens);
        System.err.println();
        System.exit(-1);
    }
    public void printTokens(int... tokens){
        Token tok = new Token();
        for(int t: tokens){
            tok.tipo = t;
            System.err.print(" "+tok.toString());
        }
    }
    public final void emparejar(int tokEsperado) {
        if (token.tipo == tokEsperado) { 
            token = al.siguienteToken();
        }
        else{
            errorSintaxis(tokEsperado);
        }
    }
    public void comprobarFinFichero(){
        if(token.tipo!=Token.EOF){
            errorSintaxis(Token.EOF);
        }
        else{
            if(show_rules)
                System.out.println(rules);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    ///             TREE METHODS
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public final String S(){
        token = al.siguienteToken();
        String trad = "";
        if (token.tipo == Token.PROGRAM) {
            anyadir(1);
            emparejar(Token.PROGRAM);
            emparejar(Token.ID);
            emparejar(Token.PYC);
            trad+="int main()\n";
            trad+=B(null,-1);
        }
        else errorSintaxis(Token.PROGRAM);
        return trad;
    }
    public final String  D(TablaSimbolos root,int scope){
        String trad = "";
        if(token.tipo == Token.VAR){
            anyadir(2);
            emparejar(Token.VAR);
            trad+= L(root,scope);
            emparejar(Token.ENDVAR);

        }
        else errorSintaxis(Token.VAR);
        return trad;
    }
    public final String L(TablaSimbolos root,int scope){
        String trad = "";
        if(token.tipo==Token.ID){
            anyadir(3);
            trad+=V(root,scope);
            trad+=Lp(root,scope);
        }
        else errorSintaxis(Token.ID);
        return trad;

    }
    public final String Lp(TablaSimbolos root,int scope){
        String trad = "";
        if(token.tipo==Token.ID){
            anyadir(4);
            trad+=V(root,scope);
            trad+=Lp(root,scope);
        }
        else if(token.tipo==Token.ENDVAR){
            anyadir(5);
        }
        else errorSintaxis(Token.ID,Token.ENDVAR);
        return trad;
    }
    public final String V(TablaSimbolos root,int scope){
        String trad = "";
        if(token.tipo==Token.ID){
            anyadir(6);
            String id = token.lexema;
            if(root.buscarAmbito(id)) errorSemantico(ERRYADECL,token);
            emparejar(Token.ID);
            emparejar(Token.DOSP);
            String array = "";
            trad += C(id,array,root,scope);
            emparejar(Token.PYC);
        }
        else errorSintaxis(Token.ID);
        return trad;
    }
    public final String C(String id, String array, TablaSimbolos root,int scope){
        String trad = "";
        StringBuilder barras = new StringBuilder();
        if(token.tipo==Token.ARRAY){ //C −→  A C
            anyadir(7);
            String dim=A(); //[7][2]
            trad+=C(id,array+dim,root,scope);
        }
        else if(token.tipo==Token.POINTER || token.tipo==Token.INTEGER || token.tipo == Token.REAL){  //C −→  P
            anyadir(8);
            trad+= P(id,root,scope);
            for(int i=0;i<scope;i++) barras.append("_");
            if(array!="") {
                Simbolo s = new Simbolo(id,Simbolo.ARRAY,barras+id);
                root.anyadir(s);
            }
            else {
                switch (trad) {
                    case "int":
                        root.anyadir(new Simbolo(id, Simbolo.ENTERO, barras + id));
                        break;
                    case "float":
                        root.anyadir(new Simbolo(id, Simbolo.REAL, barras + id));
                        break;
                    default:
                        root.anyadir(new Simbolo(id, Simbolo.PUNTERO, barras + id));
                }
            }

            trad+= " " + barras + id + array + ";"+"\n";

        }
        else errorSintaxis(Token.ARRAY,Token.POINTER,Token.INTEGER,Token.REAL);
        return trad;

    }
    public final String A(){
        String trad = "";
        if(token.tipo==Token.ARRAY){
            anyadir(9);
            emparejar(Token.ARRAY);
            emparejar(Token.CORI);
            trad+=R();
            emparejar(Token.CORD);
            emparejar(Token.OF);
        }
        else errorSintaxis(Token.ARRAY);
        return trad;
    }
    public final String R(){
        String trad = "";
        if(token.tipo==Token.NUMENTERO){
            anyadir(10);
            trad += G();
            trad += Rp();
        }
        else errorSintaxis(Token.NUMENTERO);
        return trad;

    }
    public final String Rp(){
        String trad = "";
        if(token.tipo==Token.COMA){
            anyadir(11);
            emparejar(Token.COMA);
            trad += G();
            trad += Rp();
        }
        else if (token.tipo==Token.CORD){
            anyadir(12);
        }
        else errorSintaxis(Token.CORD,Token.COMA);
        return trad;
    }
    public final String G(){
        String trad = "";
        if(token.tipo==Token.NUMENTERO){
            anyadir(13);
            trad += "[";
            int num1 = Integer.parseInt(token.lexema);
            emparejar(Token.NUMENTERO);
            emparejar(Token.PTOPTO);
            int num2 = Integer.parseInt(token.lexema);
            if(num2<num1) errorSemantico(ERRRANGO,token);
            emparejar(Token.NUMENTERO);
            trad += num2-num1+1;
            trad += "]";
        }
        else errorSintaxis(Token.NUMENTERO);
        return trad;
    }
    public final String P(String id,TablaSimbolos root,int scope){
        String trad = "";
        if(token.tipo==Token.POINTER){
            anyadir(14);
            emparejar(Token.POINTER);
            emparejar(Token.OF);
            trad += P(id,root,scope);
            trad += "*";
        }
        else if(token.tipo==Token.INTEGER || token.tipo==Token.REAL){
            anyadir(15);
            String tipo = Tipo();
            trad += tipo;
        }
        else errorSintaxis(Token.POINTER,Token.INTEGER,Token.REAL);
        return trad;
    }
    public final String Tipo() {
        String trad = "";
        if (token.tipo == Token.INTEGER) {
            anyadir(16);
            emparejar(Token.INTEGER);
            trad+="int";
        }
        else if (token.tipo == Token.REAL) {
            anyadir(17);
            emparejar(Token.REAL);
            trad+="float";
        }
        else errorSintaxis(Token.INTEGER, Token.REAL);
        return trad;
    }
    public final String B(TablaSimbolos root,int scope){
        String trad = "";
        if(token.tipo==Token.BEGIN){
            TablaSimbolos child = new TablaSimbolos(root);
            scope++;
            anyadir(18);
            emparejar(Token.BEGIN);
            trad+="{\n";
            trad+= D(child,scope);
            trad += SI(child,scope);
            trad += "}\n";
            scope--;
            emparejar(Token.END);
        }
        else errorSintaxis(Token.BEGIN);
        return trad;
    }
    public final String SI(TablaSimbolos root,int scope){
        String trad = "";
        if(token.tipo==Token.ID || token.tipo==Token.WRITE || token.tipo==Token.BEGIN){
            anyadir(19);
            trad += I(root,scope);
            trad += M(root,scope);
        }
        else errorSintaxis(Token.ID,Token.BEGIN,Token.WRITE);
        return trad;
    }
    public final String M(TablaSimbolos root,int scope){
        String trad = "";
        if(token.tipo==Token.PYC){
            anyadir(20);
            emparejar(Token.PYC);
            trad += I(root,scope);
            trad += M(root,scope);
        }
        else if(token.tipo==Token.END){
            anyadir(21);
        }
        else errorSintaxis(Token.PYC,Token.END);
        return trad;
    }
    public final String I(TablaSimbolos root,int scope){
        String trad = "";
        String id = token.lexema;
        if(token.tipo==Token.ID){
            anyadir(22);
            Simbolo i_token = root.buscar(token.lexema);
            if(i_token==null) errorSemantico(ERRNODECL,token);
            int var_tipo = i_token.tipo;
            String id_token = i_token.nomtrad;
            emparejar(Token.ID);
            Token asig = token;
            emparejar(Token.ASIG);
            trad+= id_token + "=";
            Obj e_obj=E(root,false);
            if(e_obj.tipo==1 && var_tipo==2) {
                trad+="itor("+e_obj.trad+")";
            }
            else if((e_obj.tipo!=1 && e_obj.tipo!=2) || (var_tipo!=1 && var_tipo!=2) || (e_obj.tipo==2 && var_tipo==1)){
                errorSemantico(ERRTIPOS,asig);
            }
            else trad+=e_obj.trad;
            trad+=";\n";
        }
        else if(token.tipo==Token.WRITE){
            anyadir(23);
            emparejar(Token.WRITE);
            emparejar(Token.PARI);
            Token tok = token;
            trad+="printf(\"%";
            Obj e_obj=E(root,true);
             if(e_obj.tipo!=1 && e_obj.tipo!=2){
                errorSemantico(ERRNOSIMPLE,tok);
            }
            if(e_obj.tipo==1) trad+="d";
            else trad+="f";
            trad+="\","+e_obj.trad+");\n";
            emparejar(Token.PARD);
        }
        else if(token.tipo==Token.BEGIN){
            anyadir(24);
            trad+=B(root,scope);
        }
        else errorSintaxis(Token.ID,Token.BEGIN,Token.WRITE);
        return trad;
    }
    public final Obj E(TablaSimbolos root,boolean flag){
        String trad = "";
        int tipo = 0;
        Obj ep_obj = null;
        if(token.tipo==Token.NUMENTERO || token.tipo==Token.NUMREAL || token.tipo==Token.ID){
            anyadir(25);
             Obj t_obj = T(root,new Obj(0,""));
             ep_obj= Ep(root,t_obj);

        }
        else errorSintaxis(Token.ID,Token.NUMENTERO,Token.NUMREAL);
        return ep_obj;
    }
    public final Obj Ep(TablaSimbolos root, Obj e_obj){
        String trad = "";
        String ep_th= "";
        String ep1_th = "";
        Obj ep_obj = null;
        if(token.tipo==Token.OPAS){
            anyadir(26);
            String opas = token.lexema;
            emparejar(Token.OPAS);
            Obj t_obj=T(root,e_obj);
            if(e_obj.tipo==3){
                e_obj.tipo = root.buscar(e_obj.trad).tipo;
                e_obj.trad = root.buscar(e_obj.trad).nomtrad;

            }
            if(e_obj.tipo==1 && t_obj.tipo==2){
                e_obj.tipo=2;
                ep_th = "itor("+e_obj.trad+")";
            }
            else ep_th = e_obj.trad;
            switch(opas){
                case "+":
                    if(e_obj.tipo==1 && t_obj.tipo==1){
                        ep1_th = ep_th + " +i "+t_obj.trad;
                    }
                    else if(e_obj.tipo==2 && t_obj.tipo==1){
                        ep1_th =  ep_th + "+r itor("+t_obj.trad+")";
                        t_obj.tipo = 2;
                    }
                    else if(e_obj.tipo==2 && t_obj.tipo==2){
                        ep1_th =  ep_th  +" +r "+t_obj.trad;
                    }
                    break;
                case "-":
                    if(e_obj.tipo==1 && t_obj.tipo==1){
                        ep1_th = ep_th + " -i "+t_obj.trad;
                    }
                    else if(e_obj.tipo==2 && t_obj.tipo==1){
                        ep1_th =  ep_th + "-r itor("+t_obj.trad+")";
                        t_obj.tipo = 2;
                    }
                    else if(e_obj.tipo==2 && t_obj.tipo==2){
                        ep1_th =  ep_th  +" -r "+t_obj.trad;
                    }
                    break;
            }
            ep_obj=Ep(root,new Obj(t_obj.tipo,ep1_th));
        }
        else if(token.tipo==Token.PYC || token.tipo == Token.PARD || token.tipo == Token.END){
            anyadir(27);
            return e_obj;
        }
        else errorSintaxis(Token.PYC, Token.END,Token.PARD,Token.OPAS);
        return ep_obj;

    }
    public final Obj T(TablaSimbolos root,Obj t_obj){
        String trad = "";
        int f_tipo = 0;
        Obj tp_obj = null;
        if (token.tipo == Token.NUMENTERO || token.tipo == Token.NUMREAL || token.tipo == Token.ID){
            anyadir(28);
            String f_number = token.lexema;
            Token t_tok = token;
            f_tipo = F(root);
           if (t_obj.tipo==0) t_obj.tipo = f_tipo;
           if(f_tipo==3){
               f_tipo = root.buscar(f_number).tipo;
               f_number =root.buscar(f_number).nomtrad;
               trad = f_number;

           }
            else trad=f_number;
            tp_obj = Tp(root,new Obj(f_tipo,trad),t_tok);
            trad=tp_obj.trad;
        }
        else errorSintaxis(Token.ID,Token.NUMENTERO,Token.NUMREAL);
        return new Obj(tp_obj.tipo,trad);
    }
    public final Obj Tp(TablaSimbolos root, Obj t_obj,Token t_tok){
        String tp_th = "";
        String tp1_th ="";
        Obj tp_obj = null;
        String tp_lexema = "";
        int f_tipo = 0;
        if (token.tipo == Token.OPMUL) {
            anyadir(29);
            String opmul = token.lexema;
            Token op_mul = token;
            emparejar(Token.OPMUL);
            Token tp_tok = token;
            String lexema = token.lexema;
            f_tipo=F(root);
            if(f_tipo==3) {
                f_tipo = root.buscar(lexema).tipo; // COMPROBAR SI ES DISTINTO DE 1 O 2
                lexema = root.buscar(lexema).nomtrad; //b

            }
            tp_th = t_obj.trad;
            switch(opmul.toLowerCase()){
                case "mod":
                    if(f_tipo==1 && t_obj.tipo==1){
                        tp1_th = tp_th + " % " + lexema;
                    }
                    else if(t_obj.tipo!=1) errorSemantico(ERRNOENTEROIZQ,op_mul);
                    else if(f_tipo!=1) errorSemantico(ERRNOENTERODER,op_mul);
                    break;
                case "div":
                    if(f_tipo==1 && t_obj.tipo==1){
                        tp1_th = tp_th + " /i " + lexema;
                    }
                    else if(t_obj.tipo!=1) errorSemantico(ERRNOENTEROIZQ,op_mul);
                    else if(f_tipo!=1) errorSemantico(ERRNOENTERODER,op_mul);
                    break;
                case "/":
                    if(f_tipo==1 && t_obj.tipo==1){
                        tp1_th = "itor("+tp_th  + ") /r itor(" + lexema+")";
                        t_obj.tipo =2;
                    }
                    else if(f_tipo==1 && t_obj.tipo==2){
                        tp1_th = tp_th + " /r " + "itor("+lexema+")";
                        t_obj.tipo = 2;
                    }
                    else if(f_tipo==2&& t_obj.tipo==2){
                        tp1_th = tp_th + " /r " + lexema;
                    }
                    else if(f_tipo==2&& t_obj.tipo==1){
                        tp1_th = "itor("+tp_th + ") /r " + lexema;
                        t_obj.tipo = 2;
                    }
                    else errorSemantico(ERRTIPOS,token);
                    break;
                case "*":
                    if(f_tipo==1 && t_obj.tipo==1){
                        tp1_th = tp_th + " *i " +lexema;
                    }
                    else if(f_tipo==1 && t_obj.tipo==2){
                        tp1_th = tp_th + " *r " + "itor("+lexema+")";

                    }
                    else if(f_tipo==2&& t_obj.tipo==2){
                        tp1_th = tp_th + " *r " + lexema;

                    }
                    else if(f_tipo==2&& t_obj.tipo==1){
                        tp1_th = "itor("+tp_th + ") *r " + lexema;
                        t_obj.tipo = 2;
                    }
                    break;
            }
            tp_obj = new Obj(t_obj.tipo,tp1_th);
            tp_obj=Tp(root,tp_obj,tp_tok);
        }
        else if (token.tipo == Token.OPAS || token.tipo == Token.PYC || token.tipo == Token.END || token.tipo == Token.PARD)  {
            anyadir(30);
            return t_obj;
        }
        else errorSintaxis(Token.PYC,Token.END,Token.PARD,Token.OPAS,Token.OPMUL);
        return tp_obj;
    }
    public final int F(TablaSimbolos root){
        int trad=0;
        if (token.tipo == Token.NUMENTERO) {
            anyadir(31);
            trad=1;
            emparejar(Token.NUMENTERO);
        }
        else if (token.tipo == Token.NUMREAL) {
            anyadir(32);
            trad=2;
            emparejar(Token.NUMREAL);

        }
        else if(token.tipo ==Token.ID){
            anyadir(33);
            String id = token.lexema;
            if(root.buscar(id)==null) errorSemantico(ERRNODECL,token);
            else trad = 3;
            emparejar(Token.ID);

        }
        else errorSintaxis(Token.ID,Token.NUMENTERO,Token.NUMREAL);
        return trad;
    }


    private void errorSemantico(int nerror,Token tok) {
        System.err.print("Error semantico ("+tok.fila+","+tok.columna+"): en '"+tok.lexema+"', ");
        switch (nerror) {
            case ERRYADECL: System.err.println("ya existe en este ambito");
                break;
            case ERRNOSIMPLE: System.err.println("debe ser de tipo entero o real");
                break;
            case ERRNODECL: System.err.println("no ha sido declarado");
                break;
            case ERRTIPOS: System.err.println("tipos incompatibles entero/real");
                break;
            case ERRNOENTEROIZQ: System.err.println("el operando izquierdo debe ser entero");
                break;
            case ERRNOENTERODER: System.err.println("el operando derecho debe ser entero");
                break;
            case ERRRANGO: System.err.println("rango incorrecto");
                break;
        }
        System.exit(-1);
    }


}
