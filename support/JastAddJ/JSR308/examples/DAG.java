/* Example adapted from http://types.cs.washington.edu/jsr308/specification/java-annotation-design.html */

import java.util.*;

@DefaultQualifier("NonNull")  
class DAG {

   Set<Edge> edges;          

    // ...

   List<Vertex> getNeighbors(@Interned @Readonly Vertex v) @Readonly { 
      List<Vertex> neighbors = new LinkedList<Vertex>();
      for (Edge e : edges)                
         if (e.from() == v)              
            neighbors.add(e.to());      
      return neighbors;
   }
}

// Stubs to make example typecheck.
class Edge {
  Vertex from() { return null; }
  Vertex to() { return null; }
}

class Vertex { }

@interface DefaultQualifier {String value(); }
@interface Interned { }
@interface Readonly { }
