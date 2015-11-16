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
     if(!term.equals(""))firstSet.put( term, createFirstSet);
   }
 }
 
  
 private void findFirstNonterminalsInitiate(){
   Set<String> keys = grammar.keySet();
   Iterator<String> itS = keys.iterator();
   while(itS.hasNext()){
     String key = itS.next();
     System.out.println("Descending with key "+key);
     findFirstNonterminals(key);
     System.out.println("Ascended with key "+key);
     System.out.println("3. firstSet: "+ firstSet.get("<h>").toString() );
   }
 }
 private void cullEmptyNonterminals(){
   Set<String> keys = grammar.keySet();
   Iterator<String> keyIt = keys.iterator();
   while(keyIt.hasNext() ){
     String key = keyIt.next();
     boolean hasEmpty = false;
     LinkedList< LinkedList<String> > disjunction = grammar.get(key);
     Iterator<LinkedList<String> > itLS = disjunction.iterator();
     while(itLS.hasNext() ){
       LinkedList<String> conjunction = itLS.next();
       Iterator<String> itS = conjunction.iterator();
       while(itS.hasNext() ){
         String token = itS.next();
         if(token.equals("<empty>") ){
           hasEmpty = true;
         }
       }
     }
     if(!hasEmpty){
       firstSet.get(key).remove("<empty>");
     }
   }
 }
 
  private void findFirstNonterminals(String key){
     LinkedList< LinkedList<String > > disjunction = grammar.get(key);
     Iterator<LinkedList<String > > itLS = disjunction.iterator();
     while(itLS.hasNext() ){
       //System.out.println("key "+key + " in itLS while loop");
       LinkedList<String> conjunction = itLS.next();
       Iterator<String> itS2 = conjunction.iterator();
       if(itS2.hasNext() ){
         //System.out.println("key "+key + " in itS2.hasNext if statement");
         String term = itS2.next();
         if(terminals.contains(term)){
           //System.out.println("key "+key+" in terminals.contains with term "+ term);
           if(firstSet.containsKey(key)){
             //System.out.println("key "+key + " in ifstment firstSet.containsKey");
             firstSet.get(key).add(term);
             //System.out.println("6. <h> firstSet: "+ firstSet.get("<h>").toString() );
           }else{
             //System.out.println("key "+key +" else of ifstmt firstSet.containsKey");
             Set<String> s = new HashSet<String>();
             s.add(term);
             firstSet.put(key, s);
           }
             if(key.equals("<h>"))System.out.println("<h> firstSet: "+ firstSet.get(key).toString() );
         }else{
           //System.out.println("key "+key+" else of term contains key if stmt");
           findFirstNonterminals(term);
           Set<String> s;
           if(!firstSet.containsKey(key) ){
             s = new HashSet<String>();
             //System.out.println("key "+key+ " firstSet does not contain key");
           }else{
             //System.out.println("key "+key+ " else firstSet does not contain key");
             s = firstSet.get(key);
             //System.out.println("2. firstSet: "+ firstSet.get(term).toString() + " term " +term);
           }
           Set<String> setCopy = firstSet.get(term);
           //setCopy.remove("<empty>");
           s.addAll(setCopy );
           firstSet.put(key, s);
         }
         //System.out.println("4. <h> firstSet: "+ firstSet.get("<h>").toString() );
       }
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
           //System.out.println("key '"+key+"' is in: "+firstSet.contains(key));
           if(firstSet.containsKey(key) ){
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
 
 private void generateCascadingConjunctions(){
   Set<String> keys = grammar.keySet();
   Iterator<String> itS = keys.iterator();
   while(itS.hasNext()){
     String key = itS.next();
     LinkedList< LinkedList<String > > disjunction = grammar.get(key);
     Iterator<LinkedList<String > > itLS = disjunction.iterator();
     while(itLS.hasNext() ){
       LinkedList<String> conjunction = itLS.next();
       Iterator<String> itS2 = conjunction.iterator();
       while(itS2.hasNext() ){
         String term = itS2.next();
         if(!firstSet.get(term).contains("<empty>")){
           System.out.println("term "+ term + " does not contain <empty> toString "+firstSet.get(term).toString());
           break;
         }else{
           System.out.println("keep going with term " +term);
         }
       }
     }
   }  
 }

 private void generateNonterminals(){
  Set<String> keys = grammar.keySet();
  keys.remove("<empty>");
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
  terminals.add("<empty>");
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
  findFirstNonterminalsInitiate();
  cullEmptyNonterminals();
  //generateCascadingConjunctions();
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
    build.append(firstSet.get(key  ) );
    build.append("\n");
  }
  
  
  return build.toString();
 }

}