
public class AnalizadorSintacticoDR {
    private Token token;
    private AnalizadorLexico al;
    private StringBuilder rules;
    private boolean show_rules;

    public AnalizadorSintacticoDR(AnalizadorLexico al) {
        this.al = al;
        show_rules = true;
        token = new Token();
        rules = new StringBuilder();
    }

    private void anyadir(int rule){
        rules.append(" ");
        rules.append(rule);
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

    public final void S(){
        token = al.siguienteToken();
        if (token.tipo == Token.PROGRAM) {
            anyadir(1);
            emparejar(Token.PROGRAM);
            emparejar(Token.ID);
            emparejar(Token.PYC);
            B();
        }
        else errorSintaxis(Token.PROGRAM);
    }
    public final void D(){
        if(token.tipo == Token.VAR){
            anyadir(2);
            emparejar(Token.VAR);
            L();
            emparejar(Token.ENDVAR);
        }
        else errorSintaxis(Token.VAR);
    }
    public final void L(){
        if(token.tipo==Token.ID){
            anyadir(3);
            V();
            Lp();
        }
        else errorSintaxis(Token.ID);
    }
    public final void Lp(){
        if(token.tipo==Token.ID){
            anyadir(4);
            V();
            Lp();
        }
        else if(token.tipo==Token.ENDVAR){
            anyadir(5);
        }
        else errorSintaxis(Token.ID,Token.ENDVAR);
    }
    public final void V(){
        if(token.tipo==Token.ID){
            anyadir(6);
            emparejar(Token.ID);
            emparejar(Token.DOSP);
            C();
            emparejar(Token.PYC);
        }
        else errorSintaxis(Token.ID);
    }
    public final void C(){
        if(token.tipo==Token.ARRAY){
            anyadir(7);
            A();
            C();
        }
        else if(token.tipo==Token.POINTER || token.tipo==Token.INTEGER || token.tipo == Token.REAL){
            anyadir(8);
            P();
        }
        else errorSintaxis(Token.ARRAY,Token.POINTER,Token.INTEGER,Token.REAL);
    }
    public final void A(){
        if(token.tipo==Token.ARRAY){
            anyadir(9);
            emparejar(Token.ARRAY);
            emparejar(Token.CORI);
            R();
            emparejar(Token.CORD);
            emparejar(Token.OF);
        }
        else errorSintaxis(Token.ARRAY);
    }
    public final void R(){
        if(token.tipo==Token.NUMENTERO){
            anyadir(10);
            G();
            Rp();
        }
        else errorSintaxis(Token.NUMENTERO);

    }
    public final void Rp(){
        if(token.tipo==Token.COMA){
            anyadir(11);
            emparejar(Token.COMA);
            G();
            Rp();
        }
        else if (token.tipo==Token.CORD){
            anyadir(12);
        }
        else errorSintaxis(Token.CORD,Token.COMA);
    }
    public final void G(){
        if(token.tipo==Token.NUMENTERO){
            anyadir(13);
            emparejar(Token.NUMENTERO);
            emparejar(Token.PTOPTO);
            emparejar(Token.NUMENTERO);
        }
        else errorSintaxis(Token.NUMENTERO);
    }
    public final void P(){
        if(token.tipo==Token.POINTER){
            anyadir(14);
            emparejar(Token.POINTER);
            emparejar(Token.OF);
            P();
        }
        else if(token.tipo==Token.INTEGER || token.tipo==Token.REAL){
            anyadir(15);
            Tipo();
        }
        else errorSintaxis(Token.POINTER,Token.INTEGER,Token.REAL);
    }
    public final void Tipo() {
        if (token.tipo == Token.INTEGER) {
            anyadir(16);
            emparejar(Token.INTEGER);
        }
        else if (token.tipo == Token.REAL) {
            anyadir(17);
            emparejar(Token.REAL);
        }
        else errorSintaxis(Token.INTEGER, Token.REAL);
    }
    public final void B(){
        if(token.tipo==Token.BEGIN){
            anyadir(18);
            emparejar(Token.BEGIN);
            D();
            SI();
            emparejar(Token.END);
        }
        else errorSintaxis(Token.BEGIN);
    }
    public final void SI(){
        if(token.tipo==Token.ID || token.tipo==Token.WRITE || token.tipo==Token.BEGIN){
            anyadir(19);
            I();
            M();
        }
        else errorSintaxis(Token.ID,Token.BEGIN,Token.WRITE);
    }
    public final void M(){
        if(token.tipo==Token.PYC){
            anyadir(20);
            emparejar(Token.PYC);
            I();
            M();
        }
        else if(token.tipo==Token.END){
            anyadir(21);
        }
        else errorSintaxis(Token.PYC,Token.END);
    }
    public final void I(){
        if(token.tipo==Token.ID){
            anyadir(22);
            emparejar(Token.ID);
            emparejar(Token.ASIG);
            E();
        }
        else if(token.tipo==Token.WRITE){
            anyadir(23);
            emparejar(Token.WRITE);
            emparejar(Token.PARI);
            E();
            emparejar(Token.PARD);
        }
        else if(token.tipo==Token.BEGIN){
            anyadir(24);
            B();
        }
        else errorSintaxis(Token.ID,Token.BEGIN,Token.WRITE);
    }
    public final void E(){
        if(token.tipo==Token.NUMENTERO || token.tipo==Token.NUMREAL || token.tipo==Token.ID){
            anyadir(25);
            T();
            Ep();
        }
        else errorSintaxis(Token.ID,Token.NUMENTERO,Token.NUMREAL);
    }
    public final void Ep(){
        if(token.tipo==Token.OPAS){
            anyadir(26);
            emparejar(Token.OPAS);
            T();
            Ep();
        }
        else if(token.tipo==Token.PYC || token.tipo == Token.PARD || token.tipo == Token.END){
            anyadir(27);
        }
        else errorSintaxis(Token.PYC, Token.END,Token.PARD,Token.OPAS);

    }
    public final void T(){
        if (token.tipo == Token.NUMENTERO || token.tipo == Token.NUMREAL || token.tipo == Token.ID){
            anyadir(28);
            F();
            Tp();

        }
        else errorSintaxis(Token.ID,Token.NUMENTERO,Token.NUMREAL);
    }
    public void Tp(){
        if (token.tipo == Token.OPMUL) {
            anyadir(29);
            emparejar(Token.OPMUL);
            F();
            Tp();
        }
        else if (token.tipo == Token.OPAS || token.tipo == Token.PYC || token.tipo == Token.END || token.tipo == Token.PARD)  {
            anyadir(30);
        }
        else errorSintaxis(Token.PYC,Token.END,Token.PARD,Token.OPAS,Token.OPMUL);
    }
    public final void F(){
        if (token.tipo == Token.NUMENTERO) {
            anyadir(31);
            emparejar(Token.NUMENTERO);
        }
        else if (token.tipo == Token.NUMREAL) {
            anyadir(32);
            emparejar(Token.NUMREAL);
        }
        else if(token.tipo ==Token.ID){
            anyadir(33);
            emparejar(Token.ID);
        }
        else errorSintaxis(Token.ID,Token.NUMENTERO,Token.NUMREAL);
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

}
