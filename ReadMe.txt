Top level class is the Interprerter class.

To extract join conditions from the WHERE clause of a join on tables corresponding 
to T1, T2, ... Tn listed in that order in the SELECT clause:

- First we decouple the expression tree of the where clause into its non-AND expression components. 
For example, the expression tree of a T1 > 1 AND Tn > 1 AND T1 > Tn is decoupled to [T1 > 1, Tn > 1, T1 > Tn]
- The Join Expression Tree is constructed left deep. So we separate out the 
decoupled expressions into three partions: a partition with expressions that 
reference only columns of the right-most table (T<sub>n</sub>) (called right-partition), 
another partion with expressions that references columns from both the right-most 
table and a table from the rest of the tables or expressions that references no 
tables at all (called parent-partition), and a last partition for expressions 
that do no reference the right-most table and references at least one of the 
other tables (called left-partition).
- We then construct a scan operator / select operator with a select condition of 
the expression tree we can construct from the right-partion. This will be the right 
child of the parent join operator. We also construct an expression tree from the 
parent-partition, and set the parent join operator's join condition to this expression tree. 
Finally, we remove the right-most tree from our list of trees (since we have handled all 
expressions related to this tree), and then recurse on the remaining trees using the 
the left-partition as our decoupled expressions to consider. The parent node (root) of 
the result of this recursive call becomes the left child of the parent join operator 
of the recursive caller.
- The recursion stops when we have two tables left: In this case, we handle the 
left-partition and left-most table just like we handle the right-most table and right partition as described above.

To run/debug a test using an input and output directory:

- Open the Interpreter.java file
- In the IntelliJ IDE select "Edit Configurations" in the top left
- Click on the + sign and select "Application"
- Use the following picture as a template
- In the Parameters field enter the link to you local input and output directories