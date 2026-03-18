@echo off
if not exist dist mkdir dist
javac -d dist src\main\java\lexer\*.java
java -cp dist lexer.Main
