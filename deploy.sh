#!/bin/bash
# A simple variable example
#login="msaynevi-21"
login="make"
remoteFolder="/tmp/$login/"
fileName="SimpleServer"
fileExtension=".java"
computers=("localhost")
#computers=("tp-1a226-07" "tp-1a226-08" "tp-1a226-11")
for c in ${computers[@]}; do
  command0=("ssh" "$login@$c" "lsof -ti:3419 | xargs kill -9")
  command1=("ssh" "$login@$c" "rm -rf $remoteFolder;mkdir $remoteFolder")
  command2=("scp" "$fileName$fileExtension" "$login@$c:$remoteFolder$fileName$fileExtension")
  command3=("ssh" "$login@$c" "cd $remoteFolder;javac $fileName$fileExtension;java $fileName")
  echo ${command0[*]}
  "${command0[@]}"
  echo ${command1[*]}
  "${command1[@]}"
  echo ${command2[*]}
  "${command2[@]}"
  echo ${command3[*]}
  "${command3[@]}" &
done