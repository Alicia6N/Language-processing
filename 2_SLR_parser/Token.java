import java.util.ArrayList;

public class Token {

	public int fila;
	public int columna;

	public String lexema;
	public static final ArrayList<String> nombreToken = new ArrayList<String>();

	static{
		nombreToken.add("(");
		nombreToken.add(")");
		nombreToken.add(":");
		nombreToken.add(":=");
		nombreToken.add(";");
		nombreToken.add("+ -");
		nombreToken.add("'program'");
		nombreToken.add("'var'");
		nombreToken.add("'endvar'");
		nombreToken.add("'integer'");
		nombreToken.add("'real'");
		nombreToken.add("'begin'");
		nombreToken.add("'end'");
		nombreToken.add("'write'");
		nombreToken.add("identificador");
		nombreToken.add("numero entero");
		nombreToken.add("numero real");
		nombreToken.add("fin de fichero");
	}

	public int tipo;		// tipo es: ID, ENTERO, REAL ...

	public static final int
		PARI 		= 0,
		PARD		= 1,
		DOSP            = 2,
		ASIG		= 3,
		PYC		= 4,
		OPAS		= 5,
		PROGRAM		= 6,
		VAR		= 7,
		ENDVAR		= 8,
		INTEGER		= 9,
		REAL		= 10,
		BEGIN		= 11,
		END		= 12,
		WRITE		= 13,
		ID		= 14,
		NUMENTERO	= 15,
		NUMREAL		= 16,
		EOF		= 17;

	public String toString(){
	        return nombreToken.get(tipo);
	}
}

