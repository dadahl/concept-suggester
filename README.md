# concept-suggester
A GATE (General Architecture for Text Engineering) application for suggesting entities and intents for natural language applications, based on a corpus.
This is based on the idea of combining generic natural language understanding tools like part of speech taggers and parsers with generic semantic tools like WordNet and named entity recognizers. The tools propose an initial set of entities for a natural language understanding application, given a corpus. It is based on the observation that nouns often correspond to entities and noun modifiers like adjectives often correspond to the values of entities, while verbs often align with intents.
The generic natural language understanding tools identify syntactic constructions like noun phrases, verbs and adjectives. The semantic tools find semantic concepts like named entities (which are often the values of entities). An integration of Princeton's WordNet also allows the tools to look at the meanings of the nouns and adjectives and find superordinate concepts, sibling concepts, and subordinate concepts, all of which are useful for suggesting what entities should be used in the application.
Intents are proposed based on verbs, possibly combined with their direct objects to create a "compound intent".
It is still up to the developer to decide whether these suggestions are helpful.
A small corpus of restaurant search utterances is included as an example.
The tools are currently intended to be used within a GATE development environment, but could be exported to a stand-alone application.
It produces a trace of possible concepts as it goes through the analysis of an utterance, for example:

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

The current application uses the following GATE plugins, which can be found in your GATE installation:
 DocumentNormalizer
 OpenNLP
 WordNet_Suggester-master
 Tagger_Measurements
 Tagger_GATE-Time
 Tagger_DateNormalizer
 Tagger_Numbers
 MuNPEx
 Tools
 JAPE_Plus
 Tagger_NP_Chunking
 ANNIE
 WordNet
 
MIT license

Note that GATE itself (https://gate.ac.uk) is licensed under the LGPL and WordNet has its own license (https://wordnet.princeton.edu/license-and-commercial-use). 
