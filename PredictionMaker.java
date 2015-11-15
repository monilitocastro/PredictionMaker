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

 
 
 private void ruleOneFirstSet(){
   Set<String> keys = grammar.keySet();
   Iterator<String> it = keys.iterator();
   while(it.hasNext() ){
     String terminal = it.next();
     Set<String> termFirstSet = new HashSet<String>();
     termFirstSet.add(terminal );
     firstSet.put(terminal, termFirstSet);
   }
 }
 
 private void generateNonterminals(){
  Set<String> keys = grammar.keySet();
  //if(keys==null){System.out.println("NULLKEYS");System.exit(0);}
  keys.remove("<empty>");//treat <empty> (aka epsilon) as a terminal
  nonterminals.addAll(keys);
  Iterator<String> deletethis = keys.iterator();
  while(deletethis.hasNext() ){
    System.out.println("deletethis: '"+ deletethis.next() +"'" );
  }
 }
  
 PredictionMaker(String fileName){
  String deriveSymbol="->";
  String lhsDelimiters = " ";
  String orSymbol = " \\| ";
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
     String[] leaves = (temp.trim()).split(lhsDelimiters);
     LinkedList<String> production = new LinkedList<String>(Arrays.asList(leaves) );
     listOfLists.add(production);
   }
   if(!head.equals("") )grammar.put(head, listOfLists);
  }
  
  generateNonterminals();
  
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
      build.append(" " + itFirst + " ");
    }
    build.append("\n");
  }
  
  return build.toString();
 }

}