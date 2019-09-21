#include <iostream>
#include <vector>
#include <sstream>
#include <cctype>
#include <ctype.h>
#include <algorithm>
#include <string> 

//***-------------------- en comun.h -------------------------------

#define ERRLEXICO    1
#define ERRSINT      2
#define ERREOF       3
#define ERRLEXEOF    4

#define ERRYADECL    5
#define ERRNOSIMPLE  6
#define ERRNODECL    7
#define ERRTIPOS     8
#define ERRNOENTEROIZQ 9
#define ERRNOENTERODER 10
#define ERRRANGO       11

void msgError(int nerror,int nlin,int ncol,const char *s);

typedef struct {
    char *lexema;
    int nlin,ncol;
    int tipo;
    string trad;
    string scope;
    string pre;
} MITIPO;

#define YYSTYPE MITIPO

struct Simbolo {
    string nombre;
    int tipo;
    string nomtrad;
};
struct TablaSimbolos {
    TablaSimbolos *root;
    vector<Simbolo> simbolos;
    TablaSimbolos(TablaSimbolos *t){root=t;}
};

bool buscarAmbito(TablaSimbolos *root,string nombre);
bool anyadir(TablaSimbolos* root,Simbolo s);
Simbolo buscar(TablaSimbolos* root,string nombre);
TablaSimbolos* createScope(TablaSimbolos* root);