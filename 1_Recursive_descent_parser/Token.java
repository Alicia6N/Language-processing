import java.util.ArrayList;

public class Token {

	public int fila;
	public int columna;

	public String lexema;
	public static final ArrayList<String> nombreToken = new ArrayList<String>();

	static{
		nombreToken.add("(");
		nombreToken.add(")");
		nombreToken.add(",");
		nombreToken.add(":");
		nombreToken.add("[");
		nombreToken.add("]");
		nombreToken.add(":=");
		nombreToken.add(";");
		nombreToken.add("..");
		nombreToken.add("+ -");
		nombreToken.add("* / 'div' 'mod'");
		nombreToken.add("'program'");
		nombreToken.add("'var'");
		nombreToken.add("'endvar'");
		nombreToken.add("'integer'");
		nombreToken.add("'real'");
		nombreToken.add("'array'");
		nombreToken.add("'of'");
		nombreToken.add("'pointer'");
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
		COMA		= 2,
		DOSP            = 3,
		CORI            = 4,
		CORD            = 5,
		ASIG		= 6,
		PYC		= 7,
		PTOPTO		= 8,
		OPAS		= 9,
		OPMUL		= 10,
		PROGRAM		= 11,
		VAR		= 12,
		ENDVAR		= 13,
		INTEGER		= 14,
		REAL		= 15,
		ARRAY		= 16,
		OF		= 17,
		POINTER		= 18,
		BEGIN		= 19,
		END		= 20,
		WRITE		= 21,
		ID		= 22,
		NUMENTERO	= 23,
		NUMREAL		= 24,
		EOF		= 25;

	public String toString(){
	        return nombreToken.get(tipo);
	}
}

