#!/usr/bin/env python
from mod_stemmer import modifiedstemmer
my_stemmer = modifiedstemmer.stemmer()
print(my_stemmer.stem('consistency'))
