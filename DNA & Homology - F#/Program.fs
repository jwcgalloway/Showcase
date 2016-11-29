// Written by James Galloway as part of his assessment for:
// Queensland University of Technology - CAB402 - Programming Paradigms - Assignment 1

open Assign1_FS_Pure.HelperMethods
open Assign1_FS_Pure.EventMethods
open Assign1_FS_Pure.FileIO

[<EntryPoint>]
let main argv = 
    
    let speciesMap = parseEvents argv.[0]
    writeToFASTA speciesMap argv.[0]
    
    let homology = calculateHomology speciesMap 
    writeToHomolog homology argv.[0]
    0