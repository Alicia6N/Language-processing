#!/bin/bash
flex plp4.l
bison -d plp4.y
g++ -o code plp4.tab.c lex.yy.c

