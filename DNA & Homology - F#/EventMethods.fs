// Written by James Galloway as part of his assessment for:
// Queensland University of Technology - CAB402 - Programming Paradigms - Assignment 1
// This file contains functions that perform the evolutionary events described
// in this assessment piece.

namespace Assign1_FS_Pure

open System
open Assign1_FS_Pure.HelperMethods

module EventMethods =
    
    
    /// <summary>
    /// Create a gene belonging to a specified species, bearing a gene ID and DNA.
    /// </summary>
    /// <param name="speciesID">The species ID of the species the new gene will belong to</param>
    /// <param name="geneID">The gene ID of the gene to be created</param>
    /// <param name="dna">The DNA belong to the new gene</param>
    let create speciesID geneID dna speciesAcc =
        let gene = Map.add geneID dna (Map.find speciesID speciesAcc)
        Map.add speciesID gene speciesAcc
    

    /// <summary>
    /// Find and replace a nucleotide at a given position with a new nucleotide
    /// </summary>
    /// <param name="speciesID">The species ID of the species the target gene belongs to</param>
    /// <param name="geneID">The gene ID of the gene to be snipped</param>
    /// <param name="snipIndex">The position of the nucleotide to be replaced</param>
    /// <param name="origNucl">The nucleotide to be replaced</param>
    /// <param name="newNucl">The new nucleotide</param>
    let snip speciesID geneID snipIndex origNucl newNucl speciesAcc =
        let currentDNA = getDNA speciesID geneID speciesAcc
        
        let rec getSnipSegments dna count snip acc =
            match dna with
            | [] -> acc
            | head::tail -> 
                let nextCount = count + head.dna.Length 
                if (nextCount > snipIndex) then 
                    if (snip) then 
                        let snippedDNA = head.dna
                        assert (head.dna.Substring(snipIndex - count, 1) = origNucl)
                        let snippedDNA = (snippedDNA.Remove(snipIndex - count, 1)).Insert(snipIndex - count, newNucl)
                        let snipSegment = 
                            { 
                                dna = snippedDNA; 
                                dnaStart = head.dnaStart;
                                dnaEnd = head.dnaEnd;
                                originatingEvent = head.originatingEvent}
                        getSnipSegments tail nextCount false (acc @ [snipSegment])
                    else 
                        getSnipSegments tail nextCount false (acc @ [head])
                else getSnipSegments tail nextCount true (acc @ [head])
            
        let snipSegments = getSnipSegments currentDNA 0 true []
        create speciesID geneID snipSegments speciesAcc
    

    /// <summary>
    /// Insert new nucleotides into the DNA of a specified gene
    /// </summary>
    /// <param name="speciesID">The species ID of the species the target gene belongs to</param>
    /// <param name="geneID">The gene ID of the gene that will have DNA inserted</param>
    /// <param name="insertIndex">The index at which the new DNA will be inserted</param>
    /// <param name="insertDNA">The DNA to be inserted</param>
    let insert speciesID geneID insertIndex insertSeg speciesAcc =
        let currentDNA = getDNA speciesID geneID speciesAcc
        let segmentsBefore = getSegmentsBefore insertIndex currentDNA 0 []
        let segmentsAfter = getSegmentsAfter insertIndex currentDNA 0 true []
        let newDNA = List.concat[ segmentsBefore; insertSeg; segmentsAfter ]
        create speciesID geneID newDNA speciesAcc


    /// <summary>
    /// Delete DNA of a specified length at a specified position
    /// </summary>
    /// <param name="speciesID">The species ID of the species the target gene belongs to</param>
    /// <param name="geneID">The gene ID of the gene that will have DNA deleted</param>
    /// <param name="deleteIndex">The index at which the DNA will be deleted</param>
    /// <param name="deleteLength">The length of the DNA to be deleted</param>
    let delete speciesID geneID deleteIndex deleteLength speciesAcc =
        let currentDNA = getDNA speciesID geneID speciesAcc
        let segmentsBefore = getSegmentsBefore deleteIndex currentDNA 0 []
        let segmentsAfter = getSegmentsAfter (deleteLength + deleteIndex) currentDNA 0 true []
        let newDNA = List.concat[ segmentsBefore; segmentsAfter ]
        create speciesID geneID newDNA speciesAcc


    /// <summary>
    /// Duplicate a set of genes and assign them a new gene ID
    /// </summary>
    /// <param name="speciesID">The species ID of the species the target gene belongs to</param>
    /// <param name="newGeneID">The gene ID of the new gene to be created</param>
    /// <param name="dupeGeneID">The gene ID of the gene to be duplicated</param>
    let duplicate speciesID newGeneID dupeGeneID speciesAcc =
        let currentDNA = getDNA speciesID dupeGeneID speciesAcc
        
        let newDNA = copyDNA currentDNA []
        create speciesID newGeneID newDNA speciesAcc
  

    /// <summary>
    /// Removes a set of genes from a species
    /// </summary>
    /// <param name="speciesID">The species ID of the species the target gene belongs to</param>
    /// <param name="geneID">The gene ID of the gene to be removed</param>
    let loss speciesID geneID speciesAcc =
        let genes = Map.remove geneID (Map.find speciesID speciesAcc)
        Map.add speciesID genes speciesAcc


    /// <summary>
    /// Creates two new genes by splitting an existing gene into two parts
    /// </summary>
    /// <param name="speciesID">The species ID of the species the target gene belongs to</param>
    /// <param name="newGeneID">The gene ID of the gene to be created</param>
    /// <param name="origGeneID">The gene ID of the gene that will be split</param>
    /// <param name="splitIndex">The position at which the DNA will be split</param>
    let fission speciesID newGeneID origGeneID splitIndex speciesAcc =
        let currentDNA = getDNA speciesID origGeneID speciesAcc

        let segmentsBefore = getSegmentsBefore splitIndex currentDNA 0 []
        let segmentsAfter = getSegmentsAfter splitIndex currentDNA 0 true []

        create speciesID origGeneID segmentsBefore speciesAcc |> create speciesID newGeneID segmentsAfter
    

    /// <summary>
    /// Creates a new gene by connecting two existing genes.  The new gene is assigned the same gene ID 
    /// as the first input gene.  The second gene is removed.
    /// </summary>
    /// <param name="speciesID">The species ID of the species the target gene belongs to</param>
    /// <param name="gene1ID">The gene ID of the first gene to be used in the combination</param>
    /// <param name="gene2ID">The gene ID of the second gene to be used in the combination</param>
    let fusion speciesID gene1ID gene2ID speciesAcc = 
        let gene1DNA = getDNA speciesID gene1ID speciesAcc
        let gene2DNA = getDNA speciesID gene2ID speciesAcc

        let rec joinDNA dna1 dna2 acc =
            match dna1 with
            | [] -> match dna2 with
                    | [] -> reverse acc
                    | head::tail -> joinDNA [] tail (head::acc)
            | head::tail -> joinDNA tail dna2 (head::acc)

        let newDNA = joinDNA gene1DNA gene2DNA []
        create speciesID gene1ID newDNA speciesAcc


    /// <summary>
    /// Creates a new species with the same set of genes as this species
    /// </summary>
    /// <param name="newSpeciesID">The species ID of the species to be created</param>
    /// <param name="copySpeciesID">The species ID of the species to be copied</param>
    let speciation newSpeciesID copySpeciesID speciesAcc =
        let copySpecies = Map.find copySpeciesID speciesAcc
        Map.add newSpeciesID copySpecies speciesAcc



    /// <summary>
    /// Compare a list of DNA segments with those of the genes contained in the species map
    /// in order to identify and return homologous genes
    /// </summary>
    /// <param name="keyGene">The gene to which all other genes are compared</param>
    let findRelations keySegs speciesMap relationsAcc =
        let rec compareSegs keySegs relationDNA relationSKey relationGKey relationsAcc =
            match keySegs with
            | [] -> relationsAcc
            | head::tail -> 
                compareSegs tail relationDNA relationSKey relationGKey (List.fold (fun relationsAcc relationSeg ->
                if (head.originatingEvent = relationSeg.originatingEvent && not(List.contains (relationSKey, relationGKey) relationsAcc)) then
                    if (head.dnaStart <= relationSeg.dnaEnd && relationSeg.dnaStart <= head.dnaEnd) then 
                        (relationSKey, relationGKey)::relationsAcc
                    else 
                        relationsAcc
                else
                    relationsAcc) relationsAcc relationDNA)

        Map.fold(fun relationsAcc speciesKey genes -> 
            Map.fold (fun relationsAcc geneKey gene -> compareSegs keySegs gene speciesKey geneKey relationsAcc) relationsAcc genes) relationsAcc speciesMap


    /// <summary>
    /// Calculates the homology between species and genes.
    /// </summary>
    /// <param name="speciesMap">Map containing the world's species and their genes</param>
    let calculateHomology speciesMap =
        Map.fold (fun homology speciesKey genes ->
            Map.fold(fun homology geneKey gene -> 
                Map.add (speciesKey, geneKey) (reverse (findRelations gene speciesMap [])) homology) homology genes) Map.empty speciesMap


    /// <summary> 
    /// Call the appropriate function corresponding to the event specified
    /// </summary>
    /// <param name="speciesMap">Map containing the world's species and their genes</param>
    /// <param name="event">The event line containing event parameters as read from the test file</param>
    let executeEvent speciesMap (event : string) =
        let origin = new Object()
        let eventParams = event.Split [|','|]
        match eventParams.[0] with
        | "create" ->
            // DNA record is defined here since this event creates new DNA
            let dna = 
                { 
                    dna = eventParams.[3]; 
                    dnaStart = 0;
                    dnaEnd = eventParams.[3].Length - 1; 
                    originatingEvent = new Object() 
                }
            create (toInt eventParams.[1]) (toInt eventParams.[2]) [dna] speciesMap
        | "snip" -> 
            snip (toInt eventParams.[1]) (toInt eventParams.[2]) (toInt eventParams.[3]) eventParams.[4] eventParams.[5] speciesMap
        | "insert" ->
            // DNA record is defined here since this event creates new DNA
            let dna = 
                { 
                    dna = eventParams.[4]; 
                    dnaStart = 0;
                    dnaEnd = eventParams.[4].Length - 1; 
                    originatingEvent = new Object() 
                }
            insert (toInt eventParams.[1]) (toInt eventParams.[2]) (toInt eventParams.[3]) [dna] speciesMap
        | "delete" -> 
           delete (toInt eventParams.[1]) (toInt eventParams.[2]) (toInt eventParams.[3]) (toInt eventParams.[4]) speciesMap
        | "duplicate" -> 
            duplicate (toInt eventParams.[1]) (toInt eventParams.[2]) (toInt eventParams.[3]) speciesMap
        | "loss" -> 
            loss (toInt eventParams.[1]) (toInt eventParams.[2]) speciesMap
        | "fission" -> 
            fission (toInt eventParams.[1]) (toInt eventParams.[2]) (toInt eventParams.[3]) (toInt eventParams.[4]) speciesMap
        | "fusion" -> 
            fusion (toInt eventParams.[1]) (toInt eventParams.[2]) (toInt eventParams.[3]) speciesMap
            |> loss (toInt eventParams.[1]) (toInt eventParams.[3])
        | "speciation" -> 
            speciation (toInt eventParams.[1]) (toInt eventParams.[2]) speciesMap
        | _ -> failwith "Test file error - Line did not match evolutionary event"