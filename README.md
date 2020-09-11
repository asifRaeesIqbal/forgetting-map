Coding Test
Objective

The objective of this task is design, implement and test a thread-safe 'forgetting map'.
A 'forgetting' map should hold associations between a ‘key’ and some ‘content’. It should implement
at least two methods:

1. add (add an association)
2. find (find content using the specified key).

It should hold as many associations as it can, but no more than x associations at any time, with x being
a parameter passed to the constructor. Associations that are least used (in a sense of 'find') are
removed from the map as needed.

Implementation

Java would be preferable.
Use of LRUMap from Apache Commons Collections or LinkedHashMap is not permitted.
