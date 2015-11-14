import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Set;
import java.util.Iterator;
import java.lang.StringBuilder;
import java.io.File;
import java.io.FileNotFoundException;

public class PredictionMaker{
 private Hashtable<String, LinkedList< LinkedList<String> > > grammar;
 private Hashtable<String, Set<String> > firstSet;
 private Set<String> Nonterminals;
 
 public void firstOf(String X){
   if(!Nonterminals.contains(X) ){
     if(firstSet.contains(X) ){
       return;
     }else{
       Set<String> term = new HashSet<String>();
       term.add(X);
       firstSet.put(X, term);
     }
   }else{
     if(firstSet.contains(X) ){
       return;
     }else{
       Set<String> term = new HashSet<String>();
       LinkedList<LinkedList<String> > disjunction = grammar.get(X)
       firstOf(disjunction);
       LinkedList<String> listOfFirstSymbols = helperFirstOf(disjunction);
       Iterator<String> it = listOfFirstSymbols.iterator();
       while(it.hasNext() ){
         String firstOfConjunction = it.next();
         term.add(firstSet.get(firstOfConjunction) );
       }
       firstSet.put(X, term);
     }
   }
 }
 
 public boolean allHaveEmpty(LinkedList<String> conjunction){
   Iterator<String> it = conjunction.iterator();
   boolean result= false;
   while(it.hasNext() ){
     result = result & (firstSet.get(it.next() ) ).contains("<empty>");
     if(!result) break;
   }
   return result;
 }
 public void firstOf(LinkedList<LinkedList<String> > disjunction){
   Iterator<String> it = (helperFirstOf(disjunction)).iterator();
   while(it.hasNext() ){
     firstOf(it.next() );
   }
 }
 
 public LinkedList<String> helperFirstOf(LinkedList<LinkedList<String> > disjunction){
   Iterator<LinkedList<String> > it = disjunction.iterator();
   LinkedList<String> result = new LinkedList<String>();
   while(it.hasNext() ){
     LinkedList<String> conjunction = it.next();
     result.add(conjunction.get(0) );
   }
   return result;
 }
 
 public void firstOf(LinkedList<String> conjunction)){
   boolean hasEmpty = allHaveEmpty(conjunction);
   
 }
 
 /*
 public Set<String> firstOf(String X){
   Set<String> result = new HashSet<String>();
   if(Nonterminals.contains(X)){
     //for each nonterminal production of X find the first of LinkedList<LinkedList<String>>
     result.add(firstOf(grammar.get(X)));
   }else{
     result.add(X);
   }
   return result;
 }
 
 public Set<String> firstOf(String X, Boolean b){
   Set<String> result = new HashSet<String>();
   result = firstOf(X);
   b = result.contains("<empty>");
   if(b){
     result.remove("<empty>");
   }
   return result;
 }
 
 public Set<String> firstOf(LinkedList<LinkedList<String> > disjunction){
   Set<String> result = new HashSet<String>();
   Iterator<LinkedList<String> > it = disjunction.iterator();
   while(it.hasNext() ){
     result.add(firstOf(it.next() ) );
   }
   return result;
 }
 
 public Set<String> firstOf(LinkedList<String> conjunction)){
   Set<String> result = new HashSet<String>();
   Iterator<String> it = conjunction.iterator();
   Boolean hasEmpty = new Boolean(true);
   while(it.hasNext() && b ){
     result.add(firstOf(it.next(), ) );
   }
   return result
 }*/
 
 public void generateNonTerminals(){
  Set<String> keys = grammar.keySet();
  keys.remove("<empty>");//treat <empty> (aka epsilon) as a terminal
  Nonterminals.addAll(keys);
 }
  
 PredictionMaker(String fileName){
  String deriveSymbol="->";
  String lhsDelimiters = " ";
  String orSymbol = " \\| ";
  firstSet = new Hashtable<String, Set<String> >();
  grammar = new Hashtable<String, LinkedList< LinkedList<String> > >();
  File file = new File(fileName);
  Scanner sc = null;
  try{
   sc = new Scanner(file);
  }catch(FileNotFoundException e){
   System.out.println("File not found: '"+fileName+"'");
  }
  if(sc==null){
   System.exit(1);
  }
  while(sc.hasNextLine() ){
   String line = sc.nextLine();
   String lineArr[] = line.split(deriveSymbol);
   String head = lineArr[0];
   String tempRhs = lineArr[lineArr.length-1].trim();
   String orTermsArr[] = tempRhs.split(orSymbol);
   LinkedList< String > orTerms = new LinkedList<String>(Arrays.asList(orTermsArr) );
   LinkedList< LinkedList<String> > listOfLists = new LinkedList< LinkedList<String> >(); 
   Iterator<String> it = orTerms.iterator();
   while(it.hasNext() ){
     String temp = it.next();
     String[] leaves = (temp.trim()).split(lhsDelimiters);
     LinkedList<String> production = new LinkedList<String>(Arrays.asList(leaves) );
     listOfLists.add(production);
   }
   if(!head.equals("") )grammar.put(head, listOfLists);
  }
  
 }


 public String toString(){
  StringBuilder build = new StringBuilder();
  Set<String> keys = grammar.keySet();
  Iterator<String> it = keys.iterator();
  while(it.hasNext() ){
   String name = it.next();
   LinkedList<LinkedList<String > > rhs = grammar.get(name);
   build.append(name+" : ");
   Iterator< LinkedList<String> > it2 = rhs.iterator();
   build.append("[ ");
   while(it2.hasNext() ){
    LinkedList<String> leaves = it2.next();
    Iterator<String> it3 = leaves.iterator();
    while(it3.hasNext() ){
      build.append( " " + it3.next() + " ");
    }
    
    if(it2.hasNext())build.append(" | ");
   }
   build.append("]\n");
  }
  return build.toString();
 }

}