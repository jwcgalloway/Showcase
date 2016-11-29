// Written by James Galloway as part of his assessment for:
// Queensland University of Technology - CAB402 - Programming Paradigms - Assignment 1
// This file contains helper methods that not directly responsible for performing any evolutionary event. 

namespace Assign1_FS_Pure

open System

module HelperMethods =

    type OriginEvent() = class end

    [<StructuredFormatDisplay("{dna}")>]
    type DNASegment = {
        dna : string; 
        dnaStart : int; 
        dnaEnd : int;
        originatingEvent : Object }


    /// <summary>
    /// Evaluates to the converted int value of a given string
    /// </summary>
    /// <param name="s">The string to be parsed</param>
    let toInt s =
        System.Int32.Parse(s)


    /// <summary>
    /// Evaluates to the species bearing the specified speciesID
    /// </summary>
    /// <param name="speciesID">The species ID of the species to be returned</param>
    let getDNA speciesID geneID speciesMap =
        Map.find geneID (Map.find speciesID speciesMap)
    

    /// <summary>
    /// Reverse the contents of a list
    /// </summary>
    /// <param name="l">The list to be reversed</param>
    let rec reverse l =
        match l with
        | [] -> []
        | [head] -> [head]
        | head::tail -> (reverse tail) @ [head]


    /// <summary>
    /// Copy DNA segments into a new list
    /// <param name="dna">The dna to be copied</param>
    /// <param name="acc">Accumulator for the copied DNA</param>
    let rec copyDNA dna acc =
        match dna with
        | [] -> reverse acc
        | head::tail -> copyDNA tail (head::acc)


    /// <summary>
    /// Evaluates to an array of all the DNA segments occuring before a specified index.
    /// This method will split a DNA segment if that index happens to be mid-way through
    /// a DNA segment.
    /// </summary>
    /// <param name="index">The specified index</param>
    /// <param name="segments">The DNA segments</param>
    /// <param name="count">Int used to keep track of the lengths of the DNA segments passed</param>
    /// <param name="acc">Accumulator for the DNA segments occuring before the specified index</param>
    let rec getSegmentsBefore index segments count acc =
        match segments with
        | [] -> reverse acc
        | head::tail -> 
            let nextCount = count + head.dna.Length
            if (nextCount < index) then
                getSegmentsBefore index tail nextCount (head::acc)
            else  
                let partialDNA = head.dna.Substring(0, index - count);
                if (partialDNA.Length > 0) then
                    let newSegment = 
                        {
                            dna = partialDNA; 
                            dnaStart = head.dnaStart
                            dnaEnd = head.dnaStart + partialDNA.Length - 1; 
                            originatingEvent = head.originatingEvent
                        }
                    reverse (newSegment::acc)
                else
                    reverse acc


    /// <summary>
    /// Evaluates to an array of all the DNA segments occuring after a specified index.
    /// This method will split a DNA segment if that index happens to be mid-way through
    /// a DNA segment.
    /// </summary>
    /// <param name="index">The specified index</param>
    /// <param name="segments">The DNA segments</param>
    /// <param name="count">Int used to keep track of the lengths of the DNA segments passed</param>
    /// <param name="split">Boolean flag used to track whether a segment was split</param>
    /// <param name="acc">Accumulator for the DNA segments occuring before the specified index</param>
    let rec getSegmentsAfter index segments count split acc =
        match segments with
        | [] -> reverse acc
        | head::tail -> 
            let nextCount = count + head.dna.Length
            if (nextCount > index) then
                if (split) then 
                    let partialDNA = head.dna.Substring(index - count)
                    if (partialDNA.Length > 0) then
                        let newSegment = 
                            { 
                                dna = partialDNA; 
                                dnaStart = head.dnaStart + (index - count); 
                                dnaEnd = head.dnaStart + (index - count) + partialDNA.Length - 1; 
                                originatingEvent = head.originatingEvent
                            }
                        getSegmentsAfter index tail nextCount false (newSegment::acc)
                    else
                        getSegmentsAfter index tail nextCount true (head::acc)
                else
                    getSegmentsAfter index tail nextCount false (head::acc)
            else 
                getSegmentsAfter index tail nextCount true acc