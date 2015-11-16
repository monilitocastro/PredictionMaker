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
 
 private void generateConjunctionFirsts(){
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
          if(firstSet.containsKey(key) ){
            Set<String> first = firstSet.get(key);
            System.out.println("TERM: "+term);
            Set<String> firstTemp;
            if(firstSet.get(term)!=null){
              firstTemp = new HashSet<String>( firstSet.get(term) );
            }else{
              break;
            }
            firstTemp.remove("<empty>");
            first.addAll(firstTemp);
            if(!first.contains("<empty>")){
              break;
            }
          }
          
       }
     }
   }
 }
 
 private void findFirstNonterminalsInitiate(){
   Set<String> keys = grammar.keySet();
   Iterator<String> itS = keys.iterator();
   while(itS.hasNext()){
     String key = itS.next();
     findFirstNonterminals(key);
   }
 }
 
  private void findFirstNonterminals(String key){
    //System.out.println("Descending into key "+key);
     LinkedList< LinkedList<String > > disjunction = grammar.get(key);
     Iterator<LinkedList<String > > itLS = disjunction.iterator();
     while(itLS.hasNext() ){
       LinkedList<String> conjunction = itLS.next();
       Iterator<String> itS2 = conjunction.iterator();
       if(itS2.hasNext() ){
         String term = itS2.next();
         boolean b = terminals.contains(term);
         //System.out.println("term is  "+term);
         if(terminals.contains(term)){
           //System.out.println("First of "+ key + " is " + term);
           if(firstSet.containsKey(key)){
             firstSet.get(key).add(term);
           }else{
             //System.out.println("Does not contain key "+key+ " for term "+term);
             Set<String> s = new HashSet<String>();
             s.add(term);
             firstSet.put(key, s);
           }
           //Set<String> s = new HashSet<String>();
           //s.add(term);
           //firstSet.put(key, s);
         }else{
           findFirstNonterminals(term);
           System.out.println("View of: " + key+ ". firstSet has "+term + " is "+ firstSet.containsKey(term) );
           Set<String> s;
           if(!firstSet.containsKey(key) ){
             s = new HashSet<String>();
           }else{
             s = firstSet.get(key);
           }
           s.addAll(firstSet.get(term) );
           firstSet.put(key, s);
           
//Set<String> s = firstSet.get(key);
           //Set<String> s2 = firstSet.get(term);
           //if(s==null)s = new HashSet<String>();
           //if(s2==null)s2 = new HashSet<String>();
           //s.addAll(s2);
         }
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
  //generateConjunctionFirsts();
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