#!/usr/bin/python
from org.apache.pig.scripting import Pig


inputFile = "openphacts.nt"
outputFile = "unweightedLitAsNode"
sample = "1" #0.0000001: 76 items, 0.0001: 75745 items
groupResults = True
useLongHash = True
if groupResults:
    outputFile += "Grouped"
if useLongHash:
    outputFile += "Hashed"
if int(float(sample)) != 1:
    outputFile += "_" + sample
pigScript = """
REGISTER lib/datafu.jar;
DEFINE UnorderedPairs datafu.pig.bags.UnorderedPairs();
REGISTER lib/d2s4pig-1.0.jar;
DEFINE NtLoader com.data2semantics.pig.loaders.NtLoader();
DEFINE LONGHASH com.data2semantics.pig.udfs.LongHash();
"""

if int(float(sample)) == 1:
    pigScript += """rdfGraph = LOAD '$inputFile' USING NtLoader() AS (sub:chararray, pred:chararray, obj:chararray);
    """
else:
    pigScript += """inputGraph = LOAD '$inputFile' USING NtLoader() AS (sub:chararray, pred:chararray, obj:chararray);
    rdfGraph = SAMPLE inputGraph $sample;
    """
#
#pigScript += """
#filteredGraph1 = filter rdfGraph by sub is not null;
#filteredGraph2 = filter filteredGraph1 by pred is not null;
#filteredGraph = filter filteredGraph2 by obj is not null;
#"""

if groupResults:
    if useLongHash:
        pigScript += """rdfGraphHashed = FOREACH rdfGraph GENERATE $longHash(sub), $longHash(obj);
rdfGraphGrouped = GROUP rdfGraphHashed BY $0;
rewrittenGraph = FOREACH rdfGraphGrouped GENERATE group, 1, rdfGraphHashed.$1;
"""
    else:
        pigScript += """rdfGraphGrouped = GROUP rdfGraph BY $0;
rewrittenGraph = FOREACH rdfGraphGrouped GENERATE group, 1, rdfGraph.$2;
"""
else:
    if useLongHash:
        pigScript += """rewrittenGraph = FOREACH rdfGraph GENERATE $longHash(sub), 1, $longHash(obj);
"""
    else:
        pigScript += """rewrittenGraph = FOREACH rdfGraph GENERATE sub, 1, obj;
"""


pigScript += """
rmf $outputFile
STORE rewrittenGraph INTO '$outputFile' USING PigStorage();
"""


P = Pig.compile(pigScript)
stats = P.bind().runSingle()