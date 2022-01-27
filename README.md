# Bachelor Thesis: Combinatorial test methods in the analysis of actuarial software (Kombinatorische Testmethoden zur Analyse aktuarieller Software)

[![Download Arbeit](https://img.shields.io/badge/Download--PDF-Arbeit-green)](https://github.com/gsindlinger/Combinatorial-Testing-Use-Case/raw/main/Arbeit/file.pdf)

Software systems are continuously growing in complexity: This is accompanied by an increase in possible errors during operation - with enormous consequences. As a result, the importance of adequate software testing methods is increasing in the field of software development, aiming to detect and eliminate possible errors at an early stage.  As one of various systematic methods for test case generation, the principle of combinatorial testing is gradually growing in popularity: Based on the assumption that interaction of a few parameters contributes to the majority of most software errors, combinatorial testing provides an effective and inexpensive method for generating test cases and also offers the possibility of quantifying the quality of these test cases on a mathematical-combinatorial level.

In this thesis the basics of software testing and the theory of combinatorial testing were elaborated and, based on this, a practical approach to combinatorial testing was implemented in a use case from the field of actuarial software. The consulting and calculation platform for life insurance products of the german ALH Group [Easy Web Leben](https://www.al-h.de/Appserver/EasyWeb/App/Cockpit) served as use case sample. Based on different sub-questions, it was investigated whether the methods of Combinatorial Testing can be applied to such a system. In addition, four different, freely available algorithms were compared in an empirical study.

The results of the various investigations can be taken in detail from the thesis. The download is possible via the badge below the headline.

## Tools & algorithms used
- [ACTS](https://www.nist.gov/programs-projects/combinatorial-testing "ACTS"): IPOG, IPOG-F
- [PICT](https://github.com/microsoft/pict "PICT")
- [CASA](http://cse.unl.edu/~citportal/ "CASA")

<img src="https://github.com/gsindlinger/Combinatorial-Testing-Use-Case/blob/main/Arbeit/images/Algorithmen_%C3%9Cbersicht.jpg" width="500">


## Code information

The studies of the thesis were performed using Java mostly. The application of the command line based tools PICT and CASA was done via Java ProcessBuilder, the application of ACTS via GUI. The evaluation accuracy of the test data was determined using a customized API of the Java interface of ACTS. 

The main code of the work is located in the folder [Projekt\src\main\java](https://github.com/gsindlinger/Combinatorial-Testing-Use-Case/tree/main/Projekt/src/main/java "Projekt\src\main\java"): Package countCombinations contains classes for determining the number of variable-value configurations to cover, package useCase contains classes for implementing the various sub-questions of the work, and package util contains auxiliary classes and auxiliary methods.

Package useCase includes separate main methods for each sub-question of the work. There are also classes for approaches that do not contribute to the final result of the work, like BuildCTWedgeTxtRun (the tool CTWedge couldn't be accessed). The sub-package suts provides various data objects that served as the basis for the creation of the test system.

