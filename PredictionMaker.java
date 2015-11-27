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
 private Hashtable<String, Set<String> > followSet;
 private Hashtable<String, Set<String> > trollSet;
 private Hashtable<String, Hashtable<String, String> > prTable;
 private Set<String> nonterminals;
 private Set<String> tokens;
 private Set<String> terminals;
 private Set<String> hasEmpty;
 private String startSymbol;
 
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
         if(hasEmpty.contains(token) ){
           loop = true;

         }
         Set<String> setCopy = new HashSet<String>(firstSet.get(token));
         if(nonterminals.contains(key) & itS2.hasNext() ) setCopy.remove("<empty>") ;
         firstSet.get(key).addAll(setCopy );
         if(setCopy.contains("<empty>") )hasEmpty.add(key);
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
 
//++++++++
  private void cascadeEdgeInit(){
   Set<String> keys = grammar.keySet();
   Iterator<String> itS = keys.iterator();
   while(itS.hasNext()){
     String key = itS.next();
     cascadeEdge(key);
   }
 }

 
  private void cascadeEdge(String key){
     LinkedList< LinkedList<String > > disjunction = grammar.get(key);
     Iterator<LinkedList<String > > itLS = disjunction.iterator();
     while(itLS.hasNext() ){
       LinkedList<String> conjunction = itLS.next();
       Iterator<String> itS2 = conjunction.iterator();
       boolean loop = true;
       while(itS2.hasNext() & loop){
         loop = false;
         String token = itS2.next();
         if(firstSet.get(token).contains("<empty>") ){
           loop = true;
         }else{
           //add token to key's entry for last token for later evaluation
           Set<String> entry;
           if(!trollSet.containsKey(key) ){
             entry = new HashSet<String>();
             entry.add(token);
             trollSet.put(key, entry);
           }else{
             entry = trollSet.get(key);
             entry.add(token);
           }
         }
         if(loop){
           //trollSet.get(token).add("<empty>");
           if(trollSet.containsKey(token) ){
             trollSet.get(token).add("<empty>");
           }else{
             Set<String> empty = new HashSet<String>();
             empty.add("<empty>");
             trollSet.put(token, empty);
           }
         }
       }
    }
 }
  private void normalizeTrollSet(){
    Hashtable<String, Set<String> > replacementHash = new Hashtable<String, Set<String> >();
    Set<String> keys = trollSet.keySet();
    Iterator<String> itKeys = keys.iterator();
    while(itKeys.hasNext() ){
      String key = itKeys.next();
      //System.out.println("key is "+key + " trollSet contains "+ trollSet.get(key).toString() );
      Set<String> items = trollSet.get(key);
      Iterator<String> itemIt = items.iterator();
      Set<String> replacementSet = new HashSet<String>();
      while(itemIt.hasNext() ){
        
        String lookFor = itemIt.next();
        //System.out.println("lookFor "+lookFor);
        boolean insert = false;
        if(nonterminals.contains(lookFor) ){
          replacementSet.addAll(firstSet.get(lookFor) );
          insert = true;
          //System.out.println("key "+ key+" replace "+ lookFor + " replacementSet "+replacementSet.toString() );
          //items.remove(lookFor);
          //items.addAll(firstSet.get(lookFor) );
        }
        if(insert){
          insert = false;
          if(replacementHash.containsKey(key) ){
            replacementHash.get(key).addAll(replacementSet);
          }else{
            replacementHash.put(key, replacementSet);
          }
        }
      }
      //exists = false;
    }
    //System.out.println("replacement Hash "+ replacementHash.toString() );
    trollSet.putAll(replacementHash);
    //System.out.println("trollSet "+ trollSet.toString() );
  }
  
  private void trolling(){
    //trollSet, firstSet
    //for each key look for any item A in its trollSet that has an empty in A's firstSet
    //if none exist then remove empty from key's first set
    boolean exists = false;
    Set<String> keys = trollSet.keySet();
    Iterator<String> itKeys = keys.iterator();
    while(itKeys.hasNext() ){
      String key = itKeys.next();
      Set<String> items = trollSet.get(key);
      Iterator<String> itemIt = items.iterator();
      while(itemIt.hasNext() ){
        String lookFor = itemIt.next();
        //System.out.println("key is " + key + " and lookFor "+lookFor);
        if(firstSet.get(lookFor).contains("<empty>") ){
          //exists = true;
        }
      }
      if(exists){
        //firstSet.get(key).remove("<empty>");
      }
      //exists = false;
    }
  }
  
  
  //+++++++++

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
 
 //*****************
  private void followRuleInit(){
   Set<String> keys = grammar.keySet();
   Iterator<String> itS = keys.iterator();
   while(itS.hasNext()){
     String key = itS.next();
     if(key.equals("<empty>") )continue;
     Set<String> empty = new HashSet<String>();
     if(!followSet.containsKey(key) ){
       followSet.put(key, empty);
     }
     followRuleDescend(key);
   }
 }

 
  private void followRuleDescend(String key){
     LinkedList< LinkedList<String > > disjunction = grammar.get(key);
     Iterator<LinkedList<String > > itLS = disjunction.iterator();
     String prevToken = "";
     while(itLS.hasNext() ){
       LinkedList<String> conjunction = itLS.next();
       Iterator<String> itS2 = conjunction.iterator();
       while(itS2.hasNext() ){
         String token = itS2.next();
         if(!prevToken.equals("") && !prevToken.equals("<empty>") ){
           Set<String> setCopy = new HashSet<String>(firstSet.get(token));
           setCopy.remove("<empty>");
           followSet.put(prevToken, setCopy );
         }
         if(!itS2.hasNext() && !token.equals("<empty>") ){
           //System.out.println("Error on " +key + " contains "+ followSet.containsKey(key) );
           Set<String> setCopy = new HashSet<String>(followSet.get(key));
           setCopy.remove("<empty>");
           followSet.put(token, setCopy );
           
           if(firstSet.get(token).contains("<empty>") ){
             followSet.get(prevToken).addAll(followSet.get(key) );
           }
         }
         //System.out.println("key "+ key +" token " +token);
         prevToken = token;
       }
    }
 }
 //******************
 
  private void generatePredictionTable(){
    //create nonterminal dimension first
    Iterator<String> itS = nonterminals.iterator();
    while(itS.hasNext() ){
      Hashtable<String, String> t_a  = new Hashtable<String, String>();
      prTable.put(itS.next(), t_a);
      
      //get the first elements of each
    }
  }
 
  private Set<String> firstOfConjunction( LinkedList<String> conjunction){
    Set<String> basket = new HashSet<String>();
    Iterator<String> itS = conjunction.iterator();
    while(itS.hasNext() ){
      basket.addAll(firstSet.get(itS.next() ) );
    }
    return basket;
  }
  
 PredictionMaker(String fileName){
  String deriveSymbol="->";
  String lhsDelimiters = " ";
  String orSymbol = " \\| ";
  hasEmpty = new HashSet<String>();
  tokens=new HashSet<String>();
  terminals = new HashSet<String>();
  nonterminals = new HashSet<String>();
  prTable = new Hashtable<String, Hashtable<String, String> >();
  followSet = new Hashtable<String, Set<String> >();
  firstSet = new Hashtable<String, Set<String> >();
  trollSet = new Hashtable<String, Set<String> >();
  grammar = new Hashtable<String, LinkedList< LinkedList<String> > >();
  terminals.add("<empty>");
  boolean isFirstToken = true;
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
   if(isFirstToken){
     isFirstToken = false;
     startSymbol = head;
     Set<String> dollar = new HashSet<String>();
     dollar.add("$");
     followSet.put(head, dollar );
   }
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
  for(int i = 0; i < 4; i++){
    cascadeInit();
    //generateCascadingConjunctions();
  }
  
  cascadeEdgeInit();
  normalizeTrollSet();
  trolling();
  
  //follow set
  followRuleInit();
  generatePredictionTable();
 }




 public String toString(){
  StringBuilder build = new StringBuilder();
  Set<String> keys = grammar.keySet();
  Iterator<String> it = keys.iterator();
  build.append("Start symbol: "+startSymbol+"\n\n");
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
    //Iterator<String> itFirst = firstsFor.iterator();
    build.append(firstSet.get(key  ) );
    build.append("\n");
  }
  
  build.append("\nFollow sets\n--------\n");
  keys = followSet.keySet();
  it = keys.iterator();
  while(it.hasNext() ){
    String key = it.next();
    build.append(key + " : ");
    Set<String> followFor = followSet.get(key);
    //Iterator<String> itFirst = followFor.iterator();
    build.append(followSet.get(key  ) );
    build.append("\n");
  }  
  
  return build.toString();
 }

}