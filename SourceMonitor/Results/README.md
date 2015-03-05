#Project 1: Static Analysis
Written by Amanpreet Powar & Stian Sandvold

## Problem description
To improve performance, maintainability and readability of a program, static analysis proves to be an
invaluable tool. It can tell us about which areas of the code to rewrite, eliminate, decouple, etc, so that
the overall performance of the code improves.


## Information
- ** Github repository: ** [inf3121-project-1](https://github.com/stian-sandvold/inf3121-project-1)
- ** Project used: ** A jigsaw-puzzle solver, from a former assignment in INF4130.
    - 2 files
    - About 550 lines of code

## Analysis review
![Kiviat Graph Metrics](https://raw.githubusercontent.com/stian-sandvold/inf3121-project-1/master/SourceMonitor/Results/Kiviat%20Metrics%20Graph%20-%20baseline.png)
### What does the graph tell you? How do you interpret the metrics applied on your project?

This graph tells me ...

I interpet the metrics applied to my project like this ...

![Kiviat Graph Metrics](https://raw.githubusercontent.com/stian-sandvold/inf3121-project-1/master/SourceMonitor/Results/Kiviat%20Metrics%20Graph%20-%20State_java.png)

### What does the graph tell you? How do you interpret the metrics applied on your file? How are they different the metrics you obtained on the whole project, compared with the metrics on this file?

This graph tells me ...

I interpet the metrics applied to my file like this ...

These metrics differ from the project metrics in the following ways ...

![Project Overview](https://raw.githubusercontent.com/stian-sandvold/inf3121-project-1/master/SourceMonitor/Results/File%20overview.png)
### Which is the biggest file you have in your project? How long the file? How many methods in it? 

The biggest file in the project is State.java

State.java is 300 lines long.

There are 2 constructors, 4 private (Including constructors) and 9 public methods. That makes a total of 13 methods (Including 2 constructors). According to SourceMonitor, there should only be 3 methods. Not sure what's going on there, but maybe it for some reason only counts private non-constructor methods?

![Project Overview](https://raw.githubusercontent.com/stian-sandvold/inf3121-project-1/master/SourceMonitor/Results/Method%20metrics.png)
### Which is the most complex method in your project? How many statements does it have?
AStarSearch is the most complex method in the project. It has a complexity of 8, and contains 27 statements.

### Would you refactor any of the methods you have in your project? 
...

### What can you say about how coupled/decoupled the program is?
...

### How would you improve your code, based on the metrics you have obtained with this analyzer? 
...

