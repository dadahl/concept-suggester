# concept-suggester
A GATE (General Architecture for Text Engineering) application for suggesting entities and intents for natural language applications, based on a corpus. This version has been updated for use with GATE 8.6.
This application is based on the idea of combining generic natural language understanding tools like part of speech taggers and parsers with generic semantic tools like WordNet and named entity recognizers. The tools propose an initial set of entities for a natural language understanding application, given a corpus. It is based on the observation that nouns often correspond to entities and noun modifiers like adjectives often correspond to the values of entities, while verbs often align with intents.
The generic natural language understanding tools identify syntactic constructions like noun phrases, verbs and adjectives. The semantic tools find semantic concepts like named entities (which are often the values of entities). An integration of Princeton's WordNet in the GATE 8.4 application allowed the tools to look at the meanings of the nouns and adjectives and find superordinate concepts, sibling concepts, and subordinate concepts, all of which are useful for suggesting what entities should be used in the application. This has been removed for the time being because the WordNet tools haven't yet been updated to work with GATE 8.6.
Intents are proposed based on verbs, possibly combined with their direct objects to create a "compound intent".
It is still up to the developer to decide whether these suggestions are helpful.
A corpus of movie queries (a subset of the MIT movie corpus https://groups.csail.mit.edu/sls/downloads/movie) is included as an example.
The tool can be used within a GATE development environment and can also be used standalone from the command line with the command "suggest.bat".
The tool produces a trace of possible concepts in the GATE "messages" window as it goes through the analysis of a corpus, for example:

Utterance:
Is there a nearby Indian restaurant that's open now?

Trace:

possible intent lookFor

possible intent inform

possible entity restaurant

possible compound intent lookForRestaurant

possible compound intent informRestaurant

possible entity value nearby

possible entity value Indian

possible entity value open

The results are saved in the working directory in the file "results.csv", which can be further analyzed with a spreadsheet.
 
This tool requires Java 9.
Note that GATE itself (https://gate.ac.uk) is licensed under the LGPL.