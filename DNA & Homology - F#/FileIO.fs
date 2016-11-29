// Written by James Galloway as part of his assessment for:
// Queensland University of Technology - CAB402 - Programming Paradigms - Assignment 1
// This file contains functions related to the input and output of this program.  This includes
// reading the test file and writing the '.fa' and '.homologs' files.

namespace Assign1_FS_Pure

open System
open Assign1_FS_Pure.EventMethods
open Assign1_FS_Pure.HelperMethods

module FileIO = 
    
    /// <summary>
    /// Write the contents of the species map to the specified output file
    /// </summary>
    /// <param name="speciesMap">Map containing the world's species and their genes</param>
    /// <param name="filepath">The filepath to which the output file will be written</param>
    let writeToFASTA speciesMap filepath =
        use wr = new IO.StreamWriter(String.Concat(filepath, ".fa"), true)
        Map.iter (fun speciesKey genes -> 
            Map.iter (fun geneKey dna ->
                let line = String.Format(">SE{0}_G{1}", speciesKey, geneKey)
                wr.WriteLine(line)
                let line = List.fold (fun (sb : Text.StringBuilder) seg -> sb.Append(seg.dna)) (new Text.StringBuilder()) dna |> fun x -> x.ToString()
                wr.WriteLine(line)) genes) speciesMap


    /// <summary>
    /// Write the contents of the homology dictionary to the specied output file
    /// </summary>
    /// <param name="homology">Map containing the homologous relationships between genes</param>
    /// <param name="filepath">The filepath to which the output file will be written</param
    let writeToHomolog homologyMap filepath =
        use wr = new IO.StreamWriter(String.Concat(filepath, ".homologs"), true)

        Map.iter(fun key relations -> 
            let key = String.Format("SE{0}_G{1}:", fst key, snd key)
            wr.Write(key)

            let line = List.fold (fun (sb : Text.StringBuilder) relation -> 
                let relation = String.Format(" SE{0}_G{1}", fst relation, snd relation)
                sb.Append(relation)) (new Text.StringBuilder()) relations |> fun relation -> relation.ToString()
            wr.WriteLine(line)) homologyMap


    /// <summary>
    /// Read evolutionary events from file and execute them one by one and store
    /// updated results in an accumulator
    /// </summary>
    /// <param name="testFile">The filepath of the test file</param>
    let parseEvents testfile =
        let emptyWorld = Map.add 1 (Map.empty) Map.empty
        IO.File.ReadLines(testfile) |> Seq.fold (fun speciesMap -> executeEvent speciesMap) emptyWorld