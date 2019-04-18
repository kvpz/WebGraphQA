#!/bin/bash

# this could probably be expanded to be a migration tool

for metaFile in ./*/meta
do
    mv -v -f "$metaFile" "$metaFile.dat" 
done