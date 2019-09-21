/*------------------------------ plp4.y -------------------------------*/
%token program id
%token var endvar
%token array cori cord of 
%token coma dosp pyc
%token integer real
%token twrite tbegin tend pointer
%token asig ptopto
%token pari pard
%token opas opmul
%token numentero numreal

%{
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include <iostream>
#include <sstream>
using namespace std;
#include "comun.h"
extern int ncol,nlin,findefichero;
extern int yylex();
extern char *yytext;
extern FILE *yyin;

int yyerror(char *s);
const int ENTERO=1;
const int REAL=2;
const int ARRAY=3;
const int PUNTERO=4;
string  s1, s2; 
int n1,n2,n3;
int col,col1,row;
TablaSimbolos *ts = new TablaSimbolos(NULL);
%}

%%
S : program id pyc {$$.scope="";} B                                         {
                                                                                int tk = yylex();
                                                                                if (tk != 0) yyerror("");
                                                                                $$.trad = "int main()\n"+$5.trad;
                                                                                cout << $$.trad;

                                                                            };

D : var {$$.scope = $0.scope;} L endvar                                     {$$.trad = $3.trad;};

L : L {$$.scope = $0.scope;} V                                              {$$.trad = $1.trad + $3.trad;}
    | {$$.scope = $0.scope;} V                                              {$$.trad = $2.trad;};

V : id                                                                 {
                                                                                $$.scope = $0.scope; 
                                                                                if(buscarAmbito(ts,$1.lexema)) msgError(ERRYADECL,$1.nlin, $1.ncol,$1.lexema);
                                                                            } 
    dosp {$$.lexema = $1.lexema;$$.scope = $0.scope; } C pyc                                                                   {                                                                               
                                                                                s1 = $1.lexema;
                                                                                $$.trad =  $5.trad;
                                                                            };

C : A {$$.scope = $0.scope;$$.pre = $0.pre+$1.trad;$$.lexema = $0.lexema;} C   {$$.trad = $3.trad;}
  | P                                                                       {
                                                                                s2 = $1.trad;
                                                                                
                                                                                if($$.pre!="") { //ARRAY
                                                                                    Simbolo s;
                                                                                    s.nombre = $0.lexema;
                                                                                    s.tipo = 4;
                                                                                    s.nomtrad = $0.scope+$0.lexema;
                                                                                    anyadir(ts,s);
                                                                                }
                                                                                else if(s2=="int"){
                                                                                    Simbolo s;
                                                                                    s.nombre = $0.lexema;
                                                                                    s.tipo = 1;
                                                                                    s.nomtrad = $0.scope+$0.lexema;
                                                                                    anyadir(ts,s);
                                                                                }
                                                                                else if(s2=="float"){
                                                                                    Simbolo s;
                                                                                    s.nombre = $0.lexema;
                                                                                    s.tipo = 2;
                                                                                    s.nomtrad = $0.scope+$0.lexema;
                                                                                    anyadir(ts,s);
                                                                                }
                                                                                else {
                                                                                    Simbolo s;
                                                                                    s.nombre = $0.lexema;
                                                                                    s.tipo = 3;
                                                                                    s.nomtrad = $0.scope+$0.lexema;  
                                                                                    anyadir(ts,s); 
                                                                                }
                                                                                $$.trad =$1.trad+" "+$0.scope+$0.lexema+$0.pre+";\n";
                                                                            };

A : array cori R cord of                                                    {$$.trad = $3.trad;};

R : R coma G                                                                {$$.trad = $1.trad + $3.trad;}
  | G                                                                       {$$.trad = $1.trad;};

G : numentero ptopto numentero                                              {                                   
                                                                                n1 = atoi($1.lexema);
                                                                                n2 =  atoi($3.lexema);
                                                                                if(n2>=n1){
                                                                                    $$.trad="[";
                                                                                    n3 =  n2-n1+1;
                                                                                    $$.trad+=std::to_string(n3);
                                                                                    $$.trad+="]";
                                                                                }
                                                                                else msgError(ERRRANGO,$3.nlin, $3.ncol,$3.lexema);

                                                                            };

P : pointer of P                                                            {$$.trad = $3.trad + "*";}
  | Tipo                                                                    {$$.trad = $1.trad;};

Tipo : integer                                                              {$$.trad = "int";}
     | real                                                                 {$$.trad = "float";};

B : tbegin {$$.scope=$0.scope;ts = createScope(ts);} D {$$.scope=$0.scope;} SI tend {                                                                           
                                                                                    $$.trad="{\n";
                                                                                    $$.trad+=$3.trad+$5.trad+"}\n";
                                                                                    ts = ts->root;
                                                                            };


SI : SI pyc {$$.scope=$0.scope;} I                                           {$$.trad = $1.trad + $4.trad;}
   | {$$.scope=$0.scope;} I                                                  {$$.trad=$2.trad;};

I : id asig E                                                               {
                                                                                s1 = $1.lexema;
                                                                                std::transform(s1.begin(), s1.end(), s1.begin(), ::tolower);
                                                                                s2 = buscar(ts,s1).nomtrad;
                                                                                n1 = buscar(ts,s1).tipo;

                                                                                if($3.tipo == 1 && n1 == 2){
                                                                                    $$.trad=s2+"="+"itor("+$3.trad+");\n";
                                                                                }
                                                                                else if($3.tipo == 2 && n1 == 1){
                                                                                    msgError(ERRTIPOS,$2.nlin,$2.ncol,$2.lexema);
                                                                                }
                                                                                else $$.trad=s2+"="+$3.trad+";\n";
                                                                            }

  | twrite pari E pard                                                      {
                                                                                if($3.tipo==1) s1="d";
                                                                                else s1 = "f";
                                                                                $$.trad="printf(\"%"+s1+"\","+$3.trad+");\n";
                                                                            }
| {$$.scope=$0.scope+"_";} B                                                {$$.trad=$2.trad;};

E : E opas T                                                                {
                                                                                if(!strcmp($2.lexema,"+")){
                                                                                    
                                                                                    if($1.tipo==2 && $3.tipo==1){
                                                                                        $$.trad = $1.trad +" "+ "+r" + " itor("+ $3.trad+")";
                                                                                        $$.tipo=2;
                                                                                    }
                                                                                    else if($1.tipo==1 && $3.tipo==2){
                                                                                        $$.trad = "itor("+$1.trad +") "+ "+r" + " "+ $3.trad;
                                                                                        $$.tipo=2;
                                                                                    }
                                                                                    else if($1.tipo==1 && $3.tipo==1){
                                                                                        $$.trad = $1.trad +" "+ "+i" +" "+ $3.trad;
                                                                                    } 
                                                                                    else $$.trad = $1.trad +" "+ "+r" +" "+ $3.trad;
                                                                                }
                                                                                else if(!strcmp($2.lexema,"-")){
                                                                                    if($1.tipo==2 && $3.tipo==1){
                                                                                        $$.trad = $1.trad +" "+ "-r" + " itor("+ $3.trad+")";
                                                                                        $$.tipo=2;
                                                                                    }
                                                                                    else if($1.tipo==1 && $3.tipo==2){
                                                                                        $$.trad = "itor("+$1.trad +") "+ "-r" + " "+ $3.trad;
                                                                                        $$.tipo=2;
                                                                                    }
                                                                                    else if($1.tipo==1 && $3.tipo==1){
                                                                                        $$.trad = $1.trad +" "+ "-i" +" "+ $3.trad;
                                                                                    }
                                                                                    else $$.trad = $1.trad +" "+ "-r" +" "+ $3.trad;
                                                                                }

                                                                                
                                                                            }
  | T                           
                                                                            {
                                                                                $$.trad = $1.trad;
                                                                                $$.tipo = $1.tipo;
                                                                            };

T : T opmul  F                                                               {
                                                                                if($3.tipo == 3){                                                                               
                                                                                    s1 = buscar(ts,$3.lexema).nomtrad;
                                                                                    n1 = buscar(ts,$3.lexema).tipo;    
                                                                                                                                                        
                                                                                }
                                                                                else {
                                                                                    s1 = $3.lexema;
                                                                                    $$.tipo = $1.tipo;
                                                                                    n1 = $3.tipo;
                                                                                }
                                                                                s2 = $2.lexema;
                                                                                std::transform(s2.begin(), s2.end(), s2.begin(), ::tolower);
                                                                                if(s2=="mod"){

                                                                                    if($1.tipo==1 && n1==1){
                                                                                        $$.trad = $1.trad +" "+ "%" + " "+ s1;
                                                                                    }
                                                                                    else if ($1.tipo!=1) msgError(ERRNOENTEROIZQ,$2.nlin,$2.ncol,$2.lexema);
                                                                                    else if (n1!=1) msgError(ERRNOENTERODER,$2.nlin,$2.ncol,$2.lexema);
                                                                                }
                                                                                else if(s2=="div"){
                                                                                    if($1.tipo==1 && n1==1){
                                                                                        $$.trad = $1.trad +" "+ "/i" + " "+ s1;
                                                                                    }
                                                                                    else if ($1.tipo!=1) msgError(ERRNOENTEROIZQ,$2.nlin,$2.ncol,$2.lexema);
                                                                                    else if (n1!=1) msgError(ERRNOENTERODER,$2.nlin,$2.ncol,$2.lexema);
                                                                                }
                                                                                else if(s2=="/"){
                                                                                    if($1.tipo==1 && n1==1){
                                                                                        $$.trad = "itor("+$1.trad +") "+ "/r "+ " itor("+s1+")";
                                                                                        $$.tipo = 2;
                                                                                    }
                                                                                    else if($1.tipo==2 && n1==1){
                                                                                        $$.trad = $1.trad +" "+ "/r"+ " itor("+s1+")";
                                                                                        $$.tipo = 2;
                                                                                    }
                                                                                    else if($1.tipo==1 && n1==2){
                                                                                        $$.trad = "itor("+$1.trad +") "+ "/r" + " "+ s1;
                                                                                        $$.tipo = 2;
                                                                                    }
                                                                                    else {
                                                                                        $$.trad = $1.trad +" "+ "/r "+s1;
                                                                                        $$.tipo = 2;
                                                                                    }
                                                                                }
                                                                                else if(s2=="*"){
                                                                                    if($1.tipo==2 && n1==1){
                                                                                        $$.trad = $1.trad +" "+ "*r"+ " itor("+ s1+")";
                                                                                        $$.tipo = 2;
                                                                                    }
                                                                                    else if($1.tipo==1 && n1==2){
                                                                                        $$.trad = "itor("+$1.trad +") "+ "*r" + " "+ s1;
                                                                                        $$.tipo = 2;
                                                                                    }
                                                                                    else if($1.tipo==1 && n1==1){
                                                                                        $$.trad = $1.trad +" "+ "*i" + " "+ s1;
                                                                                    }
                                                                                    else $$.trad = $1.trad +" "+ "*r" + " "+ s1;
                                                                                }
                                                                            }
                                                                               
  | F                                                                       {
                                                                                if($1.tipo == 3){
                                                                                s2 = $1.lexema;
                                                                                std::transform(s2.begin(), s2.end(), s2.begin(), ::tolower);
                                                                                if(buscar(ts,s2).nombre==""){ msgError(ERRNODECL,$1.nlin,$1.ncol,$1.lexema);}
                                                                                    $$.trad = buscar(ts,s2).nomtrad;
                                                                                    $$.tipo = buscar(ts,s2).tipo;
                                                                                    if($$.tipo!=1 && $$.tipo!=2) msgError(ERRNOSIMPLE,$1.nlin,$1.ncol,$1.lexema);
                                                                                    
                                                                                }
                                                                                else {
                                                                                    $$.trad = $1.lexema;
                                                                                    $$.tipo = $1.tipo;
                                                                                }
                                                                            
                                                                            };

F : numentero                                                               {$$.tipo=1;}
  | numreal                                                                 {$$.tipo=2;}
  | id                                                                      {$$.tipo=3;};

                                                                            
%%
void msgError(int nerror,int nlin,int ncol,const char *s) {
     switch (nerror) {
         case ERRLEXICO: fprintf(stderr,"Error lexico (%d,%d): caracter '%s' incorrecto\n",nlin,ncol,s);
            break;
         case ERRSINT: fprintf(stderr,"Error sintactico (%d,%d): en '%s'\n",nlin,ncol,s);
            break;
         case ERREOF: fprintf(stderr,"Error sintactico: fin de fichero inesperado\n");
            break;
         case ERRLEXEOF: fprintf(stderr,"Error lexico: fin de fichero inesperado\n");
            break;
            case ERRYADECL:  fprintf(stderr,"Error semantico(%d,%d): en '%s', ya existe en este ambito\n",nlin,ncol,s);
            break;
        case ERRNOSIMPLE:  fprintf(stderr,"Error semantico(%d,%d): en '%s', debe ser de tipo entero o real\n",nlin,ncol,s);
            break;
        case ERRNODECL:  fprintf(stderr,"Error semantico(%d,%d): en '%s', no ha sido declarado\n",nlin,ncol,s);
            break;
        case ERRTIPOS: fprintf(stderr,"Error semantico(%d,%d): en '%s', tipos incompatibles entero/real\n",nlin,ncol,s);
            break;
        case ERRNOENTEROIZQ:  fprintf(stderr,"Error semantico(%d,%d): en '%s', el operando izquierdo debe ser entero\n",nlin,ncol,s);
            break;
        case ERRNOENTERODER:  fprintf(stderr,"Error semantico(%d,%d): en '%s', el operando derecho debe ser entero\n",nlin,ncol,s);
            break;
        case ERRRANGO:  fprintf(stderr,"Error semantico(%d,%d): en '%s', rango incorrecto\n",nlin,ncol,s);
            break;
     }
    
    
     exit(1);
}

int yyerror(char *s) {
    if (findefichero) 
    {
       msgError(ERREOF,-1,-1,"");
    }
    else
    {  
       msgError(ERRSINT,nlin,ncol-strlen(yytext),yytext);
    }
}

int main(int argc,char *argv[]) {
    FILE *fent;
    if (argc==2){
        fent = fopen(argv[1],"rt");
    if (fent) {
        yyin = fent;
        yyparse();
        fclose(fent);
    }
    else
        fprintf(stderr,"No puedo abrir el fichero\n");
    }
    else
        fprintf(stderr,"Uso: ejemplo <nombre de fichero>\n");
}
bool buscarAmbito(TablaSimbolos *root,string nombre){
  for(size_t i=0;i<root->simbolos.size();i++){
        if(root->simbolos[i].nombre == nombre){
            return true;
        }
    }
    return false;
}
bool anyadir(TablaSimbolos *t,Simbolo s){
    for(size_t i=0; i<t->simbolos.size();i++){
        if(t->simbolos[i].nombre==s.nombre){
            return false;
        }
    }
    t->simbolos.push_back(s);
    return true;

}
Simbolo buscar(TablaSimbolos *root,string nombre){
    for(size_t i=0;i<root->simbolos.size();i++){
        if(root->simbolos[i].nombre == nombre){            
            return root->simbolos[i];
        }
    }
    if(root->root != NULL){ 
        return buscar(root->root,nombre);
    }

}
TablaSimbolos* createScope(TablaSimbolos* root){
    return new TablaSimbolos(root);

}