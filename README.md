# concept-suggester
A GATE (General Architecture for Text Engineering) application for suggesting entities and intents for natural language applications, based on a corpus. It also clusters documents into topics, based on their similarity (10 topics are used). This version has been updated for use with GATE 8.6.
This application is based on the idea of combining generic natural language understanding tools like part of speech taggers and parsers with generic semantic tools like WordNet and named entity recognizers. The tools propose an initial set of entities for a natural language understanding application, given a corpus. It is based on the observation that nouns often correspond to entities and noun modifiers like adjectives often correspond to the values of entities, while verbs often align with intents.
The generic natural language understanding tools identify syntactic constructions like noun phrases, verbs and adjectives. The semantic tools find semantic concepts like named entities (which are often the values of entities). 
An integration of Princeton's WordNet in the GATE 8.4 application allowed the tools to look at the meanings of the nouns and adjectives and find superordinate concepts, sibling concepts, and subordinate concepts, all of which are useful for suggesting what entities should be used in the application. This has been removed for the time being because the WordNet tools haven't yet been updated to work with GATE 8.6.

Intents are proposed based on verbs, possibly combined with their direct objects to create a "compound intent".

The latest version has added a topic clustering step based on the GATE LearningFramework unsupervised Topic Clustering tools (https://gatenlp.github.io/gateplugin-LearningFramework/LF_TrainTopicModel). The topic clustering uses the Mallet Latent Dirichlet Allocation (LDA) algorithm (http://mallet.cs.umass.edu/). After the corpus is annotated in the rule-based part of the system, the topic model is trained and applied to the input corpus.
After the pipeline runs, there will be results in the application/application-resources/dataDirectory directory. These will include information like the top topic for each document. 

It is still up to the developer to decide whether these suggestions are helpful.
A corpus of a few movie queries (a subset of the MIT movie corpus https://groups.csail.mit.edu/sls/downloads/movie) is included as an example.
The tool can be used within a GATE development environment and can also be used standalone from the command line with the command "suggest.bat".
The tool produces a trace of possible concepts in the GATE "messages" window as it goes through the analysis of a corpus, for example:

Utterance:
Find me all of the movies that starred harold ramis and bill murray 

intent string: find me all of the movies that starred harold ramis and bill murray

possible intent:lookFor

possible entity:movies

possible compound intent: lookForMovies

possible entity: ramis

possible compound intent: lookForRamis

possible entity:bill murray

possible compound intent: lookForBillmurray

possible entity value: starred

possible entity value: harold ramis: of entity: Person

possible value:starred:for entity:Person

(Note that "starred" is misanalyzed as an entity value because it was part-of-speech tagged as an adjective)

The results are saved in the working directory in the file "results.csv", which can be further analyzed with a spreadsheet. The results can also be saved in an "outputs" directory in GATE XML format. 
 
This tool requires Java 9.
Note that GATE itself (https://gate.ac.uk) is licensed under the LGPL.