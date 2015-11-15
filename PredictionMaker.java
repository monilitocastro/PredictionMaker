import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import java.lang.StringBuilder;
import java.io.File;
import java.io.FileNotFoundException;

public class PredictionMaker{
 private Hashtable<String, LinkedList< LinkedList<String> > > grammar;
 private Hashtable<String, Set<String> > firstSet;
 private Set<String> nonterminals;
 private Stack<String> stack;
 private Set<String> tokens;
 private Set<String> terminals;
 
 private void ruleTwoFirstSet(String name){
   if(firstSet.contains(name) && firstSet.get(name)!=null){
     (firstSet.get(name)).add(name);
   }else{
     Set<String> emptyset = new HashSet<String>();
     emptyset.add(new String("<empty>") );
     firstSet.put(name, emptyset);
   }
 }
 
 private void ruleOneFirstSet(){
   terminals = new HashSet<String>(tokens);
   terminals.removeAll(nonterminals);
   Iterator<String> it = terminals.iterator();
   while(it.hasNext() ){
     String term = it.next();
     Set<String> createFirstSet = new HashSet<String>();
     createFirstSet.add(term);
     //System.out.println("TERMINAL: "+term);
     if(!term.equals(""))firstSet.put( term, createFirstSet);
   }
 }
 
 private void findFirstTerminals(){
   Set<String> keys = grammar.keySet();
   Iterator<String> itS = keys.iterator();
   while(itS.hasNext()){
     String key = itS.next();
     LinkedList< LinkedList<String > > disjunction = grammar.get(key);
     Iterator<LinkedList<String > > itLS = disjunction.iterator();
     while(itLS.hasNext() ){
       LinkedList<String> conjunction = itLS.next();
       Iterator<String> itS2 = conjunction.iterator();
       if(itS2.hasNext() ){
         String term = itS2.next();
         if(terminals.contains(term)){
           if(firstSet.contains(key)){
             Set<String> first = firstSet.get(key);
             first.add(term);
           }else{
             Set<String> first = new HashSet<String>();
             first.add(term);
             firstSet.put(key, first);
           }
         }
       }
     }
   }
 }
 
 /*
 private void findFirstTerminals(){
   Set<String> keys = grammar.keySet();
   Iterator<String> it = keys.iterator();
   while(it.hasNext() ){
     String key = it.next();
     LinkedList< LinkedList<String > > disjunction = grammar.get(key);
     Iterator<LinkedList<String>> it2 = disjunction.iterator();
     while(it2.hasNext()){
       Iterator<String> it3 = (it2.next()).iterator();
       if(it3.hasNext() ){
           String lookFor = it3.next();
           if(terminals.contains(lookFor)){
            Set<String> first = firstSet.get(key);
            //first.add(lookFor);
            System.out.println("1st lookFor: " +lookFor +" key: "+key);
           }else{
            Set<String> first = new HashSet<String>();
            first.add(lookFor);
            firstSet.put(key, first);
            System.out.println("2nd lookFor: " +lookFor +" key: "+key);    
           }
       }
     }
   }
 }*/
 /*
  *        String lookFor = it3.next();
           if(terminals.contains(lookFor)){
            Set<String> first = firstSet.get(key);
            first.add(lookFor);
            System.out.println("1st lookFor: " +lookFor +" key: "+key);
           }else{
            Set<String> first = new HashSet<String>();
            first.add(lookFor);
            firstSet.put(key, first);
            System.out.println("2nd lookFor: " +lookFor +" key: "+key);    
           }
  * */
 private void generateNonterminals(){
  Set<String> keys = grammar.keySet();
  //if(keys==null){System.out.println("NULLKEYS");System.exit(0);}
  keys.remove("<empty>");//treat <empty> (aka epsilon) as a terminal
  nonterminals.addAll(keys);
 }
  
 PredictionMaker(String fileName){
  String deriveSymbol="->";
  String lhsDelimiters = " ";
  String orSymbol = " \\| ";
  tokens=new HashSet<String>();
  terminals = new HashSet<String>();
  nonterminals = new HashSet<String>();
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
   String head = lineArr[0].trim();
   String tempRhs = lineArr[lineArr.length-1].trim();
   String orTermsArr[] = tempRhs.split(orSymbol);
   LinkedList< String > orTerms = new LinkedList<String>(Arrays.asList(orTermsArr) );
   LinkedList< LinkedList<String> > listOfLists = new LinkedList< LinkedList<String> >(); 
   Iterator<String> it = orTerms.iterator();
   while(it.hasNext() ){
     String temp = it.next();
     temp = temp.trim();
     String[] leaves = temp.split(lhsDelimiters);
     LinkedList<String> production = new LinkedList<String>(Arrays.asList(leaves) );
     Iterator<String> it2 = production.iterator();
     while(it2.hasNext() ){
       String lookFor = it2.next();
       if(lookFor.equals("<empty>")){
         ruleTwoFirstSet(head);
       }
     }
     tokens.addAll(production);
     listOfLists.add(production);
   }
   if(!head.equals("") ){
     grammar.put(head, listOfLists);
     tokens.add(head);
   }
  }
  
  generateNonterminals();
  ruleOneFirstSet();
  findFirstTerminals();
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
  build.append("\nFirst sets\n--------\n");
  keys = firstSet.keySet();
  it = keys.iterator();
  while(it.hasNext() ){
    String key = it.next();
    build.append(key + " : ");
    Set<String> firstsFor = firstSet.get(key);
    Iterator<String> itFirst = firstsFor.iterator();
    while(itFirst.hasNext() ){
      build.append(" " + itFirst.next() + " ");
    }
    build.append("\n");
  }
  
  return build.toString();
 }

}