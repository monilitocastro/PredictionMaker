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
 private Set<String> tokens;
 private Set<String> terminals;
 private Set<String> hasEmpty;
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
  private void cullEmptyNonterminals(){
   Set<String> keys = grammar.keySet();
   Iterator<String> keyIt = keys.iterator();
   while(keyIt.hasNext() ){
     String key = keyIt.next();
     boolean isEmpty = false;
     LinkedList< LinkedList<String> > disjunction = grammar.get(key);
     Iterator<LinkedList<String> > itLS = disjunction.iterator();
     while(itLS.hasNext() ){
       LinkedList<String> conjunction = itLS.next();
       Iterator<String> itS = conjunction.iterator();
       while(itS.hasNext() ){
         String token = itS.next();
         if(token.equals("<empty>") ){
           isEmpty = true;
         }
       }
     }
     if(!isEmpty){
       firstSet.get(key).remove("<empty>");
     }
   }
 }
  
//++++++++
  private void cascadeInit(){
   Set<String> keys = grammar.keySet();
   Iterator<String> itS = keys.iterator();
   while(itS.hasNext()){
     String key = itS.next();
     cascade(key);
   }
 }

 
  private void cascade(String key){
     LinkedList< LinkedList<String > > disjunction = grammar.get(key);
     Iterator<LinkedList<String > > itLS = disjunction.iterator();
     while(itLS.hasNext() ){
       LinkedList<String> conjunction = itLS.next();
       Iterator<String> itS2 = conjunction.iterator();
       boolean loop = true;
       while(itS2.hasNext() & loop){
         loop = false;
         String token = itS2.next();
         System.out.println("key "+ key + " cascade token: "+token);
         if(hasEmpty.contains(token) ){
           loop = true;
           Set<String> setCopy = firstSet.get(token);
           //if(nonterminals.contains(key) )setCopy.remove("<empty>");
           firstSet.get(key).addAll(setCopy );
         }else{
           if(!itS2.hasNext() )System.out.println("Border key"+ key+ " token "+token);
         }
       }
    }
 }
  //+++++++++
  
  
 private void findFirstNonterminalsInitiate(){
   Set<String> keys = grammar.keySet();
   Iterator<String> itS = keys.iterator();
   while(itS.hasNext()){
     String key = itS.next();
     findFirstNonterminals(key);
   }
 }

 
  private void findFirstNonterminals(String key){
     LinkedList< LinkedList<String > > disjunction = grammar.get(key);
     Iterator<LinkedList<String > > itLS = disjunction.iterator();
     while(itLS.hasNext() ){
       LinkedList<String> conjunction = itLS.next();
       Iterator<String> itS2 = conjunction.iterator();
       if(itS2.hasNext() ){
         String term = itS2.next();
         if(terminals.contains(term)){
           if(firstSet.containsKey(key)){
             firstSet.get(key).add(term);
           }else{
             Set<String> s = new HashSet<String>();
             s.add(term);
             firstSet.put(key, s);
           }
         }else{
           findFirstNonterminals(term);
           Set<String> s;
           if(!firstSet.containsKey(key) ){
             s = new HashSet<String>();
           }else{
             s = firstSet.get(key);
           }
           Set<String> setCopy = firstSet.get(term);
           
           s.addAll(setCopy );
           firstSet.put(key, s);
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
       Boolean hasAllEmpty = new Boolean(false);
       Set<String> collection = contiguousConjunctionsWithEmpty(conjunction, hasAllEmpty, itS );
       //System.out.println("key "+key + " hasAllEmpty "+hasAllEmpty );
       firstSet.get(key).addAll(collection);
     }
   }  
 }
 
 private Set<String> contiguousConjunctionsWithEmpty(LinkedList<String> conjunction, Boolean hasAllEmpty,Iterator<String> itS){
   //LinkedList<String> conjunction = itLS.next();
   Iterator<String> itS2 = conjunction.iterator();
   Set<String> result = new HashSet<String>();
   while(itS2.hasNext() ){
     String term = itS2.next();
     if(firstSet.get(term).contains("<empty>") ){
       result.addAll(firstSet.get(term) );
       System.out.println("term " + term+ " firstSet.toString() " + firstSet.get(term).toString() );
       if(itS.hasNext() & !term.equals("<empty>") ){
         System.out.println("Removing <empty> on term "+term);
         result.remove("<empty>");
       }
     }else{
       if( firstSet.get(term).contains("<empty>") ){
         System.out.println("1 edge term: "+term);
         //result.remove("<empty>");
       }
       System.out.println("2 edge term: "+term + " b "+firstSet.get(term).contains("<empty>"));
       break;
     }
   }
   return result;
 }

 private void generateNonterminals(){
  Set<String> keys = grammar.keySet();
  keys.remove("<empty>");
  nonterminals.addAll(keys);
 }
  
 private void generateHasEmpty(){
   Set<String> keys = grammar.keySet();
   Iterator<String> keyIt = keys.iterator();
   while(keyIt.hasNext() ){
     String key = keyIt.next();
     if(firstSet.get(key).contains("<empty>") ){
       hasEmpty.add(key);
     }
   }
 }
 PredictionMaker(String fileName){
  String deriveSymbol="->";
  String lhsDelimiters = " ";
  String orSymbol = " \\| ";
  hasEmpty = new HashSet<String>();
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
  generateHasEmpty();
  //cascadeInit();
  for(int i = 0; i < 2; i++){
    cascadeInit();
    //generateCascadingConjunctions();
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
  build.append("Tokens with Empty: "+hasEmpty.toString() );
  
  
  return build.toString();
 }

}