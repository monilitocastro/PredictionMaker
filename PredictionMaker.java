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

 PredictionMaker(String fileName, String deriveSymbol, String orSymbol, String lhsDelimiters){
  grammar = new Hashtable<String, LinkedList< LinkedList<String> > >();
  File file = new File(fileName);
  Scanner sc =null;
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
   LinkedList< LinkedList<String> > listOfLists = new LinkedList< LinkedList<String> >(); 
   for(String branches : orTermsArr){
     String[] leaves = (branches.trim()).split(lhsDelimiters);
     LinkedList<String> production = new LinkedList<String>(Arrays.asList(leaves) );
     listOfLists.add(production);
   }
   
   grammar.put(head, listOfLists);
   

  }
 }

/*
 public String toString(){
  StringBuilder build = new StringBuilder();
  Set<String> keys = grammar.keySet();"|"
  Iterator<String> it = keys.iterator();
  while(it.hasNext() ){
   String name = it.next();
   LinkedList<String> rhs = grammar.get(name);
   build.append(name+" : ");
   Iterator<String> it2 = rhs.iterator();
   build.append("[ ");
   while(it2.hasNext() ){
    String elem = it2.next();
    build.append( elem + " ");
   }
   build.append("]\n");
  }
  return build.toString();
 }*/

}