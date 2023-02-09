#!/bin/zsh
source ~/.zshrc
rm out
javac -d out src/com/siat/cn/dai/**/*.java
java -cp out com.siat.cn.dai.Main
